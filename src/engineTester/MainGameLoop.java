package engineTester;

import cameras.FreeCamera;
import fontMeshCreator.GUIText;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;

import normalMappingObjConverter.NormalMappedObjLoader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.Light;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.CarryAround;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;

public class MainGameLoop {

	private static List<Light> lights = new ArrayList<>();
	private static List<Light> carryAroundLights = new ArrayList<>();
	private static List<Terrain> terrainList = new ArrayList<>();
	private static List<GuiTexture> guiTextureList = new ArrayList<>();
	private static List<WaterTile> waterTiles = new ArrayList<>();
	private static List<Entity> entities = new ArrayList<>();
	private static List<Entity> normalMapEntities = new ArrayList<>();
	private static List<Entity> carryAroundEntities = new ArrayList<>();

	private static Loader loader;
	private static MasterRenderer renderer;
	private static GuiRenderer guiRenderer;

	private static Entity dragon;

	private static Entity barrel;

	private static CarryAround carryAround;

	private static FreeCamera camera = new FreeCamera(new Vector3f(100, 15, 100));

	public static void main(String[] args) {					
		
		DisplayManager.createDisplay("", false);

		loader = new Loader();
		renderer = new MasterRenderer(loader, camera);
		guiRenderer = new GuiRenderer(loader);

		// createGUIs();
		createTerrain();
		createWater();
		createEntities();
		createNormalMapEntities();
		createLights();

		boolean renderSkybox = false;

		Fbo fbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);
		PostProcessing.init(loader);

		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){

			dragon.increaseRotation(0, 1, 0);
			barrel.increaseRotation(0, 0, 0);

			camera.move();
			// carryAround.update();

			renderer.renderShadowMap(entities, lights.get(0));
			renderer.renderScene(camera, lights, entities, normalMapEntities, terrainList, waterTiles, fbo, renderSkybox);

			PostProcessing.doPostProcessing(fbo.getColourTexture());

			guiRenderer.render(guiTextureList);
			DisplayManager.updateDisplay();			
		}
		PostProcessing.cleanUp();
		fbo.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();		
		loader.cleanUp();
		DisplayManager.closeDisplay();		
	}

	private static void createTerrain(){
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrain/grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain/mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrain/raceEnding"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain/path"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrain/raceblendMap"));
		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap);
		terrainList.add(terrain);
		carryAround = new CarryAround(camera, renderer.getProjectionMatrix(), terrain, carryAroundEntities, carryAroundLights);
	}

	private static void createGUIs(){
		GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(.5f, .5f), new Vector2f(.5f, .5f));
		guiTextureList.add(shadowMap);
	}

	private static void createWater(){
		WaterTile water = new WaterTile(150, 150, 0);
		waterTiles.add(water);
	}

	private static void createEntities(){
		RawModel dragonModel = OBJLoader.loadObjModel("models/dragon", loader);
		TexturedModel dragonStaticModel = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("textures/blankTexture")));
		ModelTexture texture = dragonStaticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(10);
		dragon = new Entity(dragonStaticModel, new Vector3f(130, 30, 130), 0, 0, 0, 1);
		entities.add(dragon);
	}

	private static void createNormalMapEntities(){
		RawModel barrelModel = NormalMappedObjLoader.loadObjModel("models/barrel", loader);
		TexturedModel barrelStaticModel = new TexturedModel(barrelModel, new ModelTexture(loader.loadTexture("textures/barrel")));
		barrelStaticModel.getTexture().setNormalID(loader.loadTexture("normalMaps/barrelNormal"));
		ModelTexture texture = barrelStaticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(.5f);
		barrel = new Entity(barrelStaticModel, new Vector3f(150, 30, 150), 0, 0, 0, 1);
		normalMapEntities.add(barrel);
		carryAroundEntities.add(barrel);
	}

	private static void createLights(){
		Light sun = new Light(new Vector3f(1000000, 1500000, -1000000), new Vector3f(1.3f, 1.3f, 1.3f));
		Light red = new Light(dragon.getPosition(), new Vector3f(10, 0, 0), new Vector3f(1, 0.01f, 0.002f));
		Light green = new Light(dragon.getPosition(), new Vector3f(0, 10, 0), new Vector3f(1, 0.01f, 0.002f));
		Light blue = new Light(dragon.getPosition(), new Vector3f(0, 0, 10), new Vector3f(1, 0.01f, 0.002f));
		lights.add(sun);
		lights.add(red);
		lights.add(green);
		lights.add(blue);
		carryAroundLights.add(red);
		carryAroundLights.add(green);
		carryAroundLights.add(blue);
	}

}
