package water;

import lights.Light;
import org.lwjgl.util.vector.Matrix4f;
import shaders.ShaderProgram;
import toolbox.Maths;
import cameras.Camera;
import toolbox.GLSLFile;

public class WaterShader extends ShaderProgram {

	private final static GLSLFile VERTEX_FILE = new GLSLFile("water/waterVertex");
	private final static GLSLFile FRAGMENT_FILE = new GLSLFile("water/waterFragment");

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_reflectionTexture;
	private int location_refractionTexture;
	private int location_dudvMap;
	private int location_moveFactor;
	private int location_cameraPosition;
	private int location_normalMap;
	private int location_lightColor;
	private int location_lightPosition;
	private int location_depthMap;
	private int location_nearPlane;
	private int location_farPlane;
	private int location_waveStrength;
	private int location_shineDamper;
	private int location_reflectivity;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttributes(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_reflectionTexture = getUniformLocation("reflectionTexture");
		location_refractionTexture = getUniformLocation("refractionTexture");
		location_dudvMap = getUniformLocation("dudvMap");
		location_moveFactor = getUniformLocation("moveFactor");
		location_cameraPosition = getUniformLocation("cameraPosition");
		location_normalMap = getUniformLocation("normalMap");
		location_lightColor = getUniformLocation("lightColor");
		location_lightPosition = getUniformLocation("lightPosition");
		location_depthMap = getUniformLocation("depthMap");
		location_nearPlane = getUniformLocation("nearPlane");
		location_farPlane = getUniformLocation("farPlane");
		location_waveStrength = getUniformLocation("waveStrength");
		location_shineDamper = getUniformLocation("shineDamper");
		location_reflectivity = getUniformLocation("reflectivity");
	}

	public void loadWaterVariables(float waveStrength, float shineDamper, float reflectivity){
		super.loadFloat(location_waveStrength, waveStrength);
		super.loadFloat(location_shineDamper, shineDamper);
		super.loadFloat(location_reflectivity, reflectivity);
	}

	public void connectTextureUnits(){
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
		super.loadInt(location_dudvMap, 2);
		super.loadInt(location_normalMap, 3);
		super.loadInt(location_depthMap, 4);
	}

	public void loadLight(Light light){
		super.loadVector(location_lightColor, light.getColor());
		super.loadVector(location_lightPosition, light.getPosition());
	}

	public void loadMoveFactor(float factor){
		super.loadFloat(location_moveFactor, factor);
	}

	public void loadNearFarPlanes(float nearPlane, float farPlane){
		super.loadFloat(location_nearPlane, nearPlane);
		super.loadFloat(location_farPlane, farPlane);
	}

	void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector(location_cameraPosition, camera.getPosition());
	}

	void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}

}
