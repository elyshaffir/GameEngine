package cameras;

import entities.Camera;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class FreeCamera extends Camera{

    private static final float MOVE_SPEED = 200;
    private int mouseButton = 0;

    private float currentSpeed;
    private float currentStrafeSpeed;

    public FreeCamera(){}
    public FreeCamera(Vector3f position) {
        super.setPosition(position);
    }
    public FreeCamera(int mouseButton) {
        this.mouseButton = mouseButton;
    }
    public FreeCamera(int mouseButton, Vector3f position) {
        this.mouseButton = mouseButton;
        super.setPosition(position);
    }

    private void calculatePitch(){
        if (Mouse.isButtonDown(mouseButton)){
            float pitchChange = Mouse.getDY() * .1f;
            super.setPitch(super.getPitch() - pitchChange);
        }
    }

    private void calculateYaw(){
        if (Mouse.isButtonDown(mouseButton)){
            float yawChange = Mouse.getDX() * .3f;
            super.setYaw(super.getYaw() + yawChange);
        }
    }

    private void checkMovementInput(){
        if (Keyboard.isKeyDown(Keyboard.KEY_W)){
            currentSpeed = MOVE_SPEED;
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_S)){
            currentSpeed = -MOVE_SPEED;
        }
        else{
            currentSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A)){
            currentStrafeSpeed = MOVE_SPEED;
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_D)){
            currentStrafeSpeed = -MOVE_SPEED;
        }
        else{
            currentStrafeSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
            super.setPosition(new Vector3f(super.getPosition().x, super.getPosition().y + MOVE_SPEED / 450, super.getPosition().z));
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            super.setPosition(new Vector3f(super.getPosition().x, super.getPosition().y - MOVE_SPEED / 450, super.getPosition().z));

    }

    private void checkRotationInput(){
        calculatePitch();
        calculateYaw();
    }

    public void move(){

        checkMovementInput();
        checkRotationInput();

        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        float strafeDistance = currentStrafeSpeed * DisplayManager.getFrameTimeSeconds();

        float dx = (float) (distance * Math.sin(Math.toRadians(super.getYaw())));
        float dz = (float) -(distance * Math.cos(Math.toRadians(super.getYaw())));

        float strafeDx = (float) -(strafeDistance * Math.cos(Math.toRadians(super.getYaw())));
        float strafeDz = (float) -(strafeDistance * Math.sin(Math.toRadians(super.getYaw())));

        super.setPosition(new Vector3f(super.getPosition().x + dx, super.getPosition().y, super.getPosition().z + dz));
        super.setPosition(new Vector3f(super.getPosition().x + strafeDx, super.getPosition().y, super.getPosition().z + strafeDz));
    }
}
