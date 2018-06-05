package engineTester;

import animations.animatedModel.AnimatedModel;
import animations.animation.Animation;
import animations.loading.AnimatedModelLoader;
import animations.loading.AnimationLoader;
import animations.renderingData.AnimatedMesh;
import animations.renderingData.Texture;
import audio.AudioMaster;
import audio.BackgroundMusic;
import cameras.FreeCamera;
import fileSystem.DAEFile;
import fileSystem.WAVFile;
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
import lights.Light;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import scenes.Scene;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.CarryAround;
import fileSystem.OBJFile;
import fileSystem.PNGFile;
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
	private static List<AnimatedModel> animatedEntities = new ArrayList<>();

	private static Loader loader;
	private static MasterRenderer renderer;
	private static GuiRenderer guiRenderer;

	private static Entity dragon;

	private static Entity barrel;

	private static AnimatedModel animatedEntity;

	private static CarryAround carryAround;

	private static FreeCamera camera = new FreeCamera(new Vector3f(100, 15, 100));

	private static Fbo fbo;

	public static void main(String[] args) {					
		
		DisplayManager.createDisplay("", false);

		AudioMaster.init();
		BackgroundMusic.init(new WAVFile("audio/bounce"), false);

		loader = new Loader();
		renderer = new MasterRenderer(loader, camera);
		guiRenderer = new GuiRenderer(loader);

		PostProcessing.init(loader);

		Scene scene = createScene();

		boolean renderSkybox = true;
		boolean colorOfHeights = terrainList.get(0).getBlendMap() == null;

		fbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);

		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){

			dragon.increaseRotation(0, 1, 0);

			animatedEntity.update();

			camera.move();

			renderer.renderScene(scene, fbo, renderSkybox, colorOfHeights);
			PostProcessing.doPostProcessing(fbo.getColourTexture());
			guiRenderer.render(guiTextureList);

			DisplayManager.updateDisplay();			
		}
		cleanUp();
		DisplayManager.closeDisplay();		
	}

	private static void cleanUp(){
		animatedEntity.delete();
		PostProcessing.cleanUp();
		fbo.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		BackgroundMusic.cleanUp();
		AudioMaster.cleanUp();
	}

	private static Scene createScene(){
		createTerrain();
		createWater();
		createEntities();
		createNormalMapEntities();
		createAnimatedEntities();
		createLights();
		return new Scene(camera, lights, lights.get(0), entities, normalMapEntities, animatedEntities, terrainList, waterTiles);
	}

	private static void createAnimatedEntities(){
		animatedEntity  = AnimatedModelLoader.loadEntity(new DAEFile("models/player"),
				new PNGFile("textures/playerTexture"));
		Animation animation = AnimationLoader.loadAnimation(new DAEFile("models/player"));
		animatedEntity.doAnimation(animation);
		animatedEntities.add(animatedEntity);
	}

	private static void createTerrain(){
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture(new PNGFile("terrain/grassy2")));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture(new PNGFile("terrain/highest")));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture(new PNGFile("terrain/rockTerrain")));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture(new PNGFile("terrain/lowest")));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(new PNGFile("terrain/raceblendMap")));
		Terrain terrain = new Terrain(0, 0, loader, texturePack, new PNGFile("terrain/colorOfHeightTestHeightMap")); // replace heightmap for blendMap for randomness.
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
		RawModel dragonModel = OBJLoader.loadObjModel(new OBJFile("models/dragon"), loader);
		TexturedModel dragonStaticModel = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture(new PNGFile("textures/blankTexture"))));
		ModelTexture texture = dragonStaticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(10);
		dragon = new Entity(dragonStaticModel, new Vector3f(130, 30, 130), 0, 0, 0, 1);
		entities.add(dragon);
		carryAroundEntities.add(dragon);
	}

	private static void createNormalMapEntities(){
		RawModel barrelModel = NormalMappedObjLoader.loadObjModel(new OBJFile("models/barrel"), loader);
		TexturedModel barrelStaticModel = new TexturedModel(barrelModel, new ModelTexture(loader.loadTexture(new PNGFile("textures/barrel"))));
		barrelStaticModel.getTexture().setNormalID(loader.loadTexture(new PNGFile("normalMaps/barrelNormal")));
		ModelTexture texture = barrelStaticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(.5f);
		barrel = new Entity(barrelStaticModel, new Vector3f(150, 30, 150), 0, 0, 0, 1);
		normalMapEntities.add(barrel);
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
