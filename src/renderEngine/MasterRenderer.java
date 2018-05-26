package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import shaders.StaticShader;
import terrain.TerrainShader;
import skybox.SkyboxRenderer;
import terrain.Terrain;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MasterRenderer {

	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 10000;

	public static final float RED = .9f;
	public static final float GREEN = .9f;
	public static final float BLUE = 1f;

	private static final float DENSITY = 0.002f;
	private static final float GRADIENT = 5f;

	private Matrix4f projectionMatrix;

	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;

	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();

	private WaterRenderer waterRenderer;
	private WaterShader waterShader = new WaterShader();
	private WaterFrameBuffers waterFrameBuffers = new WaterFrameBuffers();

	private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
	private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<>();
	private List<Terrain> terrainList = new ArrayList<>();
	private List<WaterTile> waterTiles = new ArrayList<>();

	private NormalMappingRenderer normalMappingRenderer;

	private SkyboxRenderer skyboxRenderer;


	public MasterRenderer(Loader loader) {
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		waterRenderer = new WaterRenderer(loader, waterShader, projectionMatrix, waterFrameBuffers);
		normalMappingRenderer = new NormalMappingRenderer(projectionMatrix);
	}

	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void render(List<Light> lights, Camera camera, Vector4f clipPlane, boolean renderSkybox){
		prepare();

		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(RED, GREEN, BLUE);
		shader.loadFogVariables(DENSITY, GRADIENT);
		shader.loadLights(lights);
		shader.loadLevels();
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();

		normalMappingRenderer.render(normalMapEntities, clipPlane, lights, camera);


		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColor(RED, GREEN, BLUE);
		terrainShader.loadFogVariables(DENSITY, GRADIENT);
		terrainShader.loadLights(lights);
		terrainShader.loadLevels();
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrainList);
		terrainShader.stop();

		waterShader.start();
		waterShader.loadViewMatrix(camera);
		waterRenderer.render(waterTiles, camera, lights.get(0), NEAR_PLANE, FAR_PLANE);
		waterShader.stop();

		if (renderSkybox)
			skyboxRenderer.render(camera, RED, GREEN, BLUE);

		waterTiles.clear();
		terrainList.clear();
		entities.clear();
		normalMapEntities.clear();
	}

	private void processTerrain(Terrain terrain){
		terrainList.add(terrain);
	}

	private void processTerrains(List<Terrain> terrainList){
		for (Terrain terrain:terrainList)
			processTerrain(terrain);
	}

	private void prepareWaterProcessing(Camera camera, List<Terrain> terrainList, List<Entity> entities, List<Entity> normalMapEntities, List<Light> lights, List<WaterTile> waterTiles, boolean renderSkybox){
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

		for (WaterTile water:waterTiles){
			waterFrameBuffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			processTerrains(terrainList);
			processEntities(entities);
			processNormalMapEntities(normalMapEntities);
			render(lights, camera, new Vector4f(0, 1, 0, -water.getHeight() + 1f), renderSkybox);
			camera.getPosition().y += distance;
			camera.invertPitch();
			waterFrameBuffers.unbindCurrentFrameBuffer();

			waterFrameBuffers.bindRefractionFrameBuffer();
			processTerrains(terrainList);
			processEntities(entities);
			render(lights, camera, new Vector4f(0, -1, 0, water.getHeight() + .5f), renderSkybox);
			waterFrameBuffers.unbindCurrentFrameBuffer();
		}

		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	}

	private void processWater(WaterTile water){
		waterTiles.add(water);
	}

	private void processWaters(List<WaterTile> waterTiles){
		for (WaterTile waterTile:waterTiles)
			processWater(waterTile);
	}

	private void processEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null){
			batch.add(entity);
		}
		else{
			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}

	private void processNormalMapEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMapEntities.get(entityModel);
		if (batch != null){
			batch.add(entity);
		}
		else{
			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
	}

	private void processEntities(List<Entity> entities){
		for (Entity entity:entities)
			processEntity(entity);
	}

	private void processNormalMapEntities(List<Entity> entities){
		for (Entity entity:entities)
			processNormalMapEntity(entity);
	}

	public void cleanUp(){
		shader.cleanUp();
		terrainShader.cleanUp();
		waterShader.cleanUp();
		normalMappingRenderer.cleanUp();
	}

	private void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);

	}

	private void createProjectionMatrix(){
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV/2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void renderScene(Camera camera, List<Light> lights, List<Entity> entities, List<Entity> normalMapEntities, List<Terrain> terrainList, List<WaterTile> waterTiles, boolean renderSkybox){
		prepareWaterProcessing(camera, terrainList, entities, normalMapEntities, lights, waterTiles, renderSkybox);

		processWaters(waterTiles);
		processTerrains(terrainList);
		processEntities(entities);
		processNormalMapEntities(normalMapEntities);
		render(lights, camera, new Vector4f(0, -1, 0, 100000), renderSkybox);
	}

}
