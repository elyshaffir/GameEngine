package engineTester;

import cameras.FreeCamera;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.Light;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;


public class MainGameLoop {

	public static void main(String[] args) {					
		
		DisplayManager.createDisplay("", false);
		Loader loader = new Loader();	
		
		RawModel model = OBJLoader.loadObjModel("models/dragon", loader);

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrain/grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain/mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrain/raceEnding"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain/path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrain/raceblendMap"));

		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap);

		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("textures/blankTexture")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(10);
		
		Entity entity = new Entity(staticModel, new Vector3f(0, 0, -30), 0, 0, 0, 1);
		Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1, 1, 1));
		
		FreeCamera camera = new FreeCamera();
		
		MasterRenderer renderer = new MasterRenderer(loader);
		
		while (!Display.isCloseRequested()){				
			entity.increaseRotation(0, 1, 0);
			camera.move();
			renderer.processTerrain(terrain);
			renderer.processEntity(entity);
			renderer.render(light, camera, false);
			DisplayManager.updateDisplay();			
		}
		renderer.cleanUp();		
		loader.cleanUp();
		DisplayManager.closeDisplay();		
	}

}
