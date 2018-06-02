package cameras;

import entities.Entity;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class PlayerCamera extends Camera{

    private float basePitch = 0;
    private float distanceFromEntity = 50;
    private float baseAngleAroundEntity = 0;
    private float angleAroundEntity = 0;
    private float angleAroundEntityPitch = 0;
    private float angleAroundEntityRoll = 0;
    private boolean middleMouseButton = false;

    public PlayerCamera(float angleAroundEntity, float pitch, float roll){
        this.angleAroundEntity = angleAroundEntity;
        this.baseAngleAroundEntity = angleAroundEntity;
        super.setPitch(pitch);
        this.basePitch = pitch;
        this.angleAroundEntityPitch = pitch;
        super.setRoll(roll);
        this.angleAroundEntityRoll = roll;
    }

    private void calculateZoom(){
        distanceFromEntity -= Mouse.getDWheel() * .1f;
    }

    private void calculatePitch(){
        if (Mouse.isButtonDown(2)){
            middleMouseButton = true;
            angleAroundEntityPitch -= Mouse.getDY() * .1f;
        }
    }

    private void calculateAngleAroundEntity(){
        if (Mouse.isButtonDown(2)){
            middleMouseButton = true;
            angleAroundEntity -= Mouse.getDX() * .1f;
        }
    }

    private float calculateHorizontalDistance(){
        return (float) (distanceFromEntity * Math.cos(Math.toRadians(super.getPitch())));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromEntity * Math.sin(Math.toRadians(super.getPitch())));
    }

    private void calculateCameraPosition(Entity cameraEntity, float horizontalDistance, float verticalPosition){
        if (!Mouse.isButtonDown(2) && middleMouseButton && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            middleMouseButton = false;
            angleAroundEntity = baseAngleAroundEntity;
            angleAroundEntityPitch = basePitch;
        }

        float theta = cameraEntity.getRotY() + angleAroundEntity;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        super.getPosition().x = cameraEntity.getPosition().x - offsetX;
        super.getPosition().y = cameraEntity.getPosition().y + verticalPosition;
        super.getPosition().z = cameraEntity.getPosition().z - offsetZ;
    }

    public void move(Entity cameraEntity, boolean followPitch, boolean locked){
        if (!locked){
            calculateZoom();
            calculatePitch();
            calculateAngleAroundEntity();
        }
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(cameraEntity, horizontalDistance, verticalDistance);
        super.setYaw(180 - (cameraEntity.getRotY() + angleAroundEntity));
        if (followPitch)
            super.setPitch((cameraEntity.getRotZ() + angleAroundEntityPitch));
        else
            super.setPitch(angleAroundEntityPitch);
        super.setRoll((cameraEntity.getRotX() + angleAroundEntityRoll));
    }

    public void setAngleAroundEntity(float angleAroundEntity) {
        this.angleAroundEntity = angleAroundEntity;
    }
}
