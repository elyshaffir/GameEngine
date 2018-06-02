package settings;

import org.lwjgl.util.vector.Vector4f;

public class ShadowSettings {
    public static final int SHADOW_MAP_SIZE = 4096;
    public static final int PCF_COUNT = 2;
    public static final float TRANSITION_DISTANCE = 10f;

    public static final float OFFSET = 15;
    public static final Vector4f UP = new Vector4f(0, 1, 0, 0);
    public static final Vector4f FORWARD = new Vector4f(0, 0, -1, 0);
    public static final float SHADOW_DISTANCE = 150;
}
