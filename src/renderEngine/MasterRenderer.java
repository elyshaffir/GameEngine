package renderEngine;

import java.util.List;

import animations.renderer.AnimatedModelRenderer;
import cameras.Camera;
import entities.Entity;
import entities.EntityRenderer;
import entities.EntityVariableLoader;
import lights.Light;
import normalMappingRenderer.NormalMappingRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import postProcessing.Fbo;
import entities.EntityShader;
import scenes.Scene;
import settings.RenderSettings;
import shadows.ShadowMapMasterRenderer;
import terrain.TerrainRenderer;
import terrain.TerrainShader;
import skybox.SkyboxRenderer;
import terrain.TerrainVariableLoader;
import water.*;

public class MasterRenderer {

	private Matrix4f projectionMatrix;

	private EntityShader entityShader = new EntityShader();
	private EntityRenderer entityRenderer;

	private AnimatedModelRenderer animatedModelRenderer;

	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();

	private WaterRenderer waterRenderer;
	private WaterShader waterShader = new WaterShader();
	private WaterFrameBuffers waterFrameBuffers = new WaterFrameBuffers();

	private NormalMappingRenderer normalMappingRenderer;

	private SkyboxRenderer skyboxRenderer;

	private ShadowMapMasterRenderer shadowMapMasterRenderer;

	public MasterRenderer(Loader loader, Camera camera) {
		enableCulling();
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
		animatedModelRenderer = new AnimatedModelRenderer();
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		waterRenderer = new WaterRenderer(loader, waterShader, projectionMatrix, waterFrameBuffers);
		normalMappingRenderer = new NormalMappingRenderer(projectionMatrix);
		shadowMapMasterRenderer = new ShadowMapMasterRenderer(camera);
	}

	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void render(List<Light> lights, Light sun, Camera camera, Vector4f clipPlane, boolean renderSkybox, boolean colorOfHeights){
		prepare();

		entityShader.start();
		EntityVariableLoader.loadVariables(entityShader, clipPlane, lights, camera);
		entityRenderer.render(Processor.entities, shadowMapMasterRenderer.getToShadowMapSpaceMatrix());
		entityShader.stop();

		normalMappingRenderer.render(Processor.normalMapEntities, clipPlane, lights, camera);

		animatedModelRenderer.render(Processor.animatedEntities, camera, new Vector3f(0.2f, -0.3f, -0.8f), projectionMatrix);

		terrainShader.start();
		TerrainVariableLoader.loadVariables(terrainShader, colorOfHeights, clipPlane, lights, camera);
		terrainRenderer.render(Processor.terrains, shadowMapMasterRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();

		waterShader.start();
		WaterVariableLoader.loadVariables(waterShader, camera);
		waterRenderer.render(Processor.waterTiles, camera, sun, RenderSettings.NEAR_PLANE, RenderSettings.FAR_PLANE);
		waterShader.stop();

		if (renderSkybox)
			skyboxRenderer.render(camera, RenderSettings.RED, RenderSettings.GREEN, RenderSettings.BLUE);

		Processor.clearAll();
	}

	private void prepareWaterProcessing(Scene scene, boolean renderSkybox, boolean colorOfHeights){
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

		for (WaterTile water:scene.getWaterTileList()){
			waterFrameBuffers.bindReflectionFrameBuffer();

			float distance;
			distance = 2 * (scene.getCamera().getPosition().y - water.getHeight());
			boolean underWater = scene.getCamera().getPosition().y < water.getHeight();
			Vector4f clipPlaneReflection;
			Vector4f clipPlaneRefraction;

			if (!underWater){
				clipPlaneReflection = new Vector4f(0, 1, 0, -water.getHeight() + 1f);
				clipPlaneRefraction = new Vector4f(0, -1, 0, water.getHeight() + .5f);
			} else {
				clipPlaneReflection = new Vector4f(0, 0, 0, water.getHeight() - Float.MAX_VALUE);
				clipPlaneRefraction = new Vector4f(0, 1, 0, -water.getHeight() + 1f);
			}


			if (!underWater) {
				scene.getCamera().getPosition().y -= distance;
				scene.getCamera().invertPitch();
			}

			Processor.processForWater(scene);
			if (underWater)
				renderSkybox = false;

			renderScene(scene, clipPlaneReflection, renderSkybox, colorOfHeights);

			if (underWater)
				renderSkybox = true;

			if (!underWater) {
				scene.getCamera().getPosition().y += distance;
				scene.getCamera().invertPitch();
			}

			waterFrameBuffers.unbindCurrentFrameBuffer();

			waterFrameBuffers.bindRefractionFrameBuffer();
			Processor.processForWater(scene);
			renderScene(scene, clipPlaneRefraction, renderSkybox, colorOfHeights);
			waterFrameBuffers.unbindCurrentFrameBuffer();
		}

		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	}

	private void renderShadowMap(List<Entity> allEntities, Light sun){
		Processor.processForShadow(allEntities);
		shadowMapMasterRenderer.render(Processor.entities, sun);
		Processor.clearForShadow();
	}

	private void renderShadowMap(Scene scene){
		renderShadowMap(scene.getAllEntities(), scene.getSun());
	}

	public int getShadowMapTexture(){
		return shadowMapMasterRenderer.getShadowMap();
	}

	public void cleanUp(){
		entityShader.cleanUp();
		animatedModelRenderer.cleanUp();
		terrainShader.cleanUp();
		waterShader.cleanUp();
		normalMappingRenderer.cleanUp();
		shadowMapMasterRenderer.cleanUp();
	}

	private void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(RenderSettings.RED, RenderSettings.GREEN, RenderSettings.BLUE, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}

	private void createProjectionMatrix(){
		projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(RenderSettings.FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = RenderSettings.FAR_PLANE - RenderSettings.NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((RenderSettings.FAR_PLANE + RenderSettings.NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * RenderSettings.NEAR_PLANE * RenderSettings.FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	private void renderScene(Scene scene, Vector4f clipPlane, boolean renderSkybox, boolean colorOfHeights){
		render(scene.getLights(), scene.getSun(), scene.getCamera(), clipPlane, renderSkybox, colorOfHeights);
	}

	public void renderScene(Scene scene, Fbo fbo, boolean renderSkybox, boolean colorOfHeights){
		renderShadowMap(scene); // FIXME: Add animatedModels
		prepareWaterProcessing(scene, renderSkybox, colorOfHeights);

		fbo.bindFrameBuffer();
		Processor.processScene(scene);
		renderScene(scene, new Vector4f(0, -1, 0, 100000), renderSkybox, colorOfHeights);
		fbo.unbindFrameBuffer();
	}

}
