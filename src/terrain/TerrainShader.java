package terrain;


import entities.Camera;
import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import shaders.ShaderProgram;
import toolbox.Maths;

import java.util.List;

public class TerrainShader extends ShaderProgram {

    private static final String VERTEX_FILE = "/terrain/terrainVertexShader.txt";
    private static final String FRAGMENT_FILE = "/terrain/terrainFragmentShader.txt";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition[];
    private int location_lightColor[];
    private int location_attenuation[];
    private int location_shineDamper;
    private int location_density;
    private int location_gradient;
    private int location_reflectivity;
    private int location_backgroundTexture;
    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendMap;
    private int location_skyColor;
    private int location_plane;
    private int location_levels;
    private int location_toShadowMapSpace;
    private int location_shadowMap;
    private int location_shadowDistance;
    private int location_transitionDistance;
    private int location_pcfCount;
    private int location_mapSize;

    public TerrainShader() {
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
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_density = super.getUniformLocation("density");
        location_gradient = super.getUniformLocation("gradient");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
        location_skyColor = super.getUniformLocation("skyColor");
        location_plane = super.getUniformLocation("plane");

        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColor = new int[MAX_LIGHTS];
        location_attenuation = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++){
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
            location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }

        location_levels = super.getUniformLocation("levels");
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

    public void loadShadowSpaceMatrix(Matrix4f matrix){
        super.loadMatrix(location_toShadowMapSpace, matrix);
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

    public void connectTextureUnits(){
        super.loadInt(location_backgroundTexture, 0);
        super.loadInt(location_rTexture, 1);
        super.loadInt(location_gTexture, 2);
        super.loadInt(location_bTexture, 3);
        super.loadInt(location_blendMap, 4);
        super.loadInt(location_shadowMap, 5);
    }

    public void loadShineVariables(float damper, float reflectivity){
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadLevels(){
        super.loadFloat(location_levels, LEVELS);
    }

    public void loadLights(List<Light> lights){
        for (int i = 0; i < MAX_LIGHTS; i++){
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
        super.loadMatrix(location_projectionMatrix, projection);
    }

}
