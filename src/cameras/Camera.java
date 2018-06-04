package cameras;

import audio.AudioMaster;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch = 0;
    private float yaw = 0;
    private float roll = 0;

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void invertPitch(){
        pitch = -pitch;
    }

    protected void setPitch(float pitch) {
        this.pitch = pitch;
        if (this.pitch > 360)
            this.pitch -= 360;
        if (this.pitch < 0)
            this.pitch = 360 + this.pitch;
    }

    protected void setYaw(float yaw) {
        this.yaw = yaw;
        if (this.yaw > 360)
            this.yaw -= 360;
        if (this.yaw < 0)
            this.yaw = 360 + this.yaw;
    }

    protected void setRoll(float roll) {
        this.roll = roll;
        if (this.roll > 360)
            this.roll -= 360;
        if (this.roll < 0)
            this.roll = 360 + this.roll;
    }

    protected void update(){
        // AudioMaster.setListenerData(position);
    }
}
