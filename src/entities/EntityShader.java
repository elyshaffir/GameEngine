package entities;

import cameras.Camera;
import org.lwjgl.util.vector.Matrix4f;

import lights.Light;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import settings.RenderSettings;
import shaders.ShaderProgram;
import toolbox.Maths;
import fileSystem.GLSLFile;

import java.util.List;

public class EntityShader extends ShaderProgram {
	
	private static final GLSLFile VERTEX_FILE = new GLSLFile("entities/entityVertexShader");
	private static final GLSLFile FRAGMENT_FILE = new GLSLFile("entities/entityFragmentShader");
	
	private int location_transformationMatrix;
	private int location_projecttionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColor[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_density;
	private int location_gradient;
	private int location_reflectivity;
	private int location_skyColor;
	private int location_plane;
	private int location_levels;
	private int location_useFakeLighting;
	private int location_numberOfRows;
	private int location_offset;
	private int location_toShadowMapSpace;
	private int location_shadowMap;
	private int location_shadowDistance;
	private int location_transitionDistance;
	private int location_pcfCount;
	private int location_mapSize;

	public EntityShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);		
	}

	@Override
	protected void bindAttributes() {
		super.bindAttributes(0, "position");
		super.bindAttributes(1, "textureCoords");
		super.bindAttributes(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {		
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projecttionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColor = super.getUniformLocation("skyColor");
		location_plane = super.getUniformLocation("plane");

		location_lightPosition = new int[RenderSettings.MAX_LIGHTS];
		location_lightColor = new int[RenderSettings.MAX_LIGHTS];
		location_attenuation = new int[RenderSettings.MAX_LIGHTS];
		for (int i = 0; i < RenderSettings.MAX_LIGHTS; i++){
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}

		location_levels = super.getUniformLocation("levels");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		location_shadowMap = super.getUniformLocation("shadowMap");
		location_shadowDistance = super.getUniformLocation("shadowDistance");
		location_transitionDistance = super.getUniformLocation("transitionDistance");
		location_pcfCount = super.getUniformLocation("pcfCount");
		location_mapSize = super.getUniformLocation("mapSize");
	}

	public void loadShadowVariables(float shadowDistance, float transitionDistance, int pcfCount, float mapSize){
		super.loadFloat(location_shadowDistance, shadowDistance);
		super.loadFloat(location_transitionDistance, transitionDistance);
		super.loadInt(location_pcfCount, pcfCount);
		super.loadFloat(location_mapSize, mapSize);
	}

	public void connectTextureUnits(){
		super.loadInt(location_shadowMap, 5);
	}

	public void loadToShadowSpaceMatrix(Matrix4f matrix){
		super.loadMatrix(location_toShadowMapSpace, matrix);
	}

	public void loadNumberOfRows(int numberOfRows){
		super.loadFloat(location_numberOfRows, numberOfRows);
	}

	public void loadOffset(float x, float y){
		super.load2DVector(location_offset, new Vector2f(x, y));
	}

	public void loadFakeLighting(boolean useFakeLighting){
		super.loadBoolean(location_useFakeLighting, useFakeLighting);
	}

	public void loadClipPlane(Vector4f plane){
		super.load4DVector(location_plane, plane);
	}

	public void loadSkyColor(float r, float g, float b){
		super.loadVector(location_skyColor, new Vector3f(r, g, b));
	}

	public void loadFogVariables(float density, float gradient){
		super.loadFloat(location_density, density);
		super.loadFloat(location_gradient, gradient);
	}

	public void loadShineVariables(float damper, float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadLevels(){
		super.loadFloat(location_levels, RenderSettings.LEVELS);
	}

	public void loadLights(List<Light> lights){
		for (int i = 0; i < RenderSettings.MAX_LIGHTS; i++){
			if (i < lights.size()){
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColor[i], lights.get(i).getColor());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			} else{
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColor[i], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projecttionMatrix, projection);
	}
	
	
}
