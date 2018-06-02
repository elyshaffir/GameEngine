package entities;

import cameras.Camera;
import lights.Light;
import org.lwjgl.util.vector.Vector4f;
import settings.RenderSettings;
import settings.ShadowSettings;

import java.util.List;

public class EntityVariableLoader {
    public static void loadVariables(EntityShader entityShader, Vector4f clipPlane, List<Light> lights, Camera camera){
        entityShader.loadShadowVariables(ShadowSettings.SHADOW_DISTANCE, ShadowSettings.TRANSITION_DISTANCE, ShadowSettings.PCF_COUNT, ShadowSettings.SHADOW_MAP_SIZE);
        entityShader.loadClipPlane(clipPlane);
        entityShader.loadSkyColor(RenderSettings.RED, RenderSettings.GREEN, RenderSettings.BLUE);
        entityShader.loadFogVariables(RenderSettings.DENSITY, RenderSettings.GRADIENT);
        entityShader.loadLights(lights);
        entityShader.loadLevels();
        entityShader.loadViewMatrix(camera);
    }
}
