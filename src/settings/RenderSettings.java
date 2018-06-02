package settings;

public class RenderSettings {
    public static final float FOV = 80;
    public static final float NEAR_PLANE = 0.1f;

    public static final float FAR_PLANE = 10000;

    public static final float RED = .9f;
    public static final float GREEN = .9f;
    public static final float BLUE = 1f;

    public static final float DENSITY = 0.002f;
    public static final float GRADIENT = 5f;

    public static final int MAX_LIGHTS = 4; // If changed, update terrain shaders and default shaders as well;
    public static final float LEVELS = 3f;

    public static final float CONTRAST = 0f;
}
