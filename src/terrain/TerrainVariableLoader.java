package terrain;

import cameras.Camera;
import lights.Light;
import org.lwjgl.util.vector.Vector4f;
import settings.RenderSettings;
import settings.ShadowSettings;

import java.util.List;

public class TerrainVariableLoader {
    public static void loadVariables(TerrainShader terrainShader, boolean colorOfHeights, Vector4f clipPlane, List<Light>lights, Camera camera){
        terrainShader.loadColorOfHeights(colorOfHeights);
        terrainShader.loadShadowVariables(ShadowSettings.SHADOW_DISTANCE, ShadowSettings.TRANSITION_DISTANCE, ShadowSettings.PCF_COUNT, ShadowSettings.SHADOW_MAP_SIZE);
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColor(RenderSettings.RED, RenderSettings.GREEN, RenderSettings.BLUE);
        terrainShader.loadFogVariables(RenderSettings.DENSITY, RenderSettings.GRADIENT);
        terrainShader.loadLights(lights);
        terrainShader.loadLevels();
        terrainShader.loadViewMatrix(camera);
    }
}
