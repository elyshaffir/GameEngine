package water;

import cameras.Camera;

public class WaterVariableLoader {
    public static void loadVariables(WaterShader waterShader, Camera camera){
        waterShader.loadViewMatrix(camera);
    }
}
