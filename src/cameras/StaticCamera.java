package cameras;

import org.lwjgl.util.vector.Vector3f;

public class StaticCamera extends Camera {
    public StaticCamera(Vector3f position, float pitch, float yaw, float roll) {
        super.setPosition(position);
        super.setPitch(pitch);
        super.setYaw(yaw);
        super.setRoll(roll);
    }

    public void update(){
        super.update();
    }
}
