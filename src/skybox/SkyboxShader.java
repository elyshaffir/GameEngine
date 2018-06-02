package skybox;

import cameras.Camera;
import org.lwjgl.util.vector.Matrix4f;

import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import settings.RenderSettings;
import settings.SkyboxSettings;
import shaders.ShaderProgram;
import toolbox.Maths;

public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/skybox/skyboxVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/skybox/skyboxFragmentShader.glsl";

	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColor;
	private int location_cubeMap1;
	private int location_cubeMap2;
	private int location_blendFactor;
	private int location_levels;

	private float rotation = 0;

	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
        matrix.m32 = 0;
        rotation += SkyboxSettings.ROTATE_SPEED * DisplayManager.getFrameTimeSeconds();
        Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);
		super.loadMatrix(location_viewMatrix, matrix);
	}

	public void loadFogColor(float r, float g, float b){
		super.loadVector(location_fogColor, new Vector3f(r, g, b));
	}

	public void connectTextureUnits(){
		super.loadInt(location_cubeMap1 ,0);
		super.loadInt(location_cubeMap2 ,1);
	}

	public void loadBlendFactor(float blendFactor){
		super.loadFloat(location_blendFactor, blendFactor);
	}

	public void loadLevels(){
		super.loadFloat(location_levels, RenderSettings.LEVELS);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColor = super.getUniformLocation("fogColor");
		location_cubeMap1 = super.getUniformLocation("cubeMap1");
		location_cubeMap2 = super.getUniformLocation("cubeMap2");
		location_blendFactor = super.getUniformLocation("blendFactor");
		location_levels = super.getUniformLocation("levels");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttributes(0, "position");
	}

}
