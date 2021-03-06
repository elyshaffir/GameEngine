package skybox;

import cameras.Camera;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import settings.SkyboxSettings;
import fileSystem.PNGFile;

public class SkyboxRenderer {

    private static final float[] VERTICES = {
            -SkyboxSettings.SIZE,  SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE, -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            SkyboxSettings.SIZE, -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            SkyboxSettings.SIZE, -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            SkyboxSettings.SIZE,  SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE,  SkyboxSettings.SIZE, -SkyboxSettings.SIZE,

            -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE, -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE,  SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE,  SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE,  SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,  SkyboxSettings.SIZE,

            SkyboxSettings.SIZE, -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            SkyboxSettings.SIZE, -SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            SkyboxSettings.SIZE,  SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            SkyboxSettings.SIZE,  SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            SkyboxSettings.SIZE,  SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            SkyboxSettings.SIZE, -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,

            -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE,  SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            SkyboxSettings.SIZE,  SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            SkyboxSettings.SIZE,  SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            SkyboxSettings.SIZE, -SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,  SkyboxSettings.SIZE,

            -SkyboxSettings.SIZE,  SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            SkyboxSettings.SIZE,  SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            SkyboxSettings.SIZE,  SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            SkyboxSettings.SIZE,  SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE,  SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE,  SkyboxSettings.SIZE, -SkyboxSettings.SIZE,

            -SkyboxSettings.SIZE, -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            SkyboxSettings.SIZE, -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            SkyboxSettings.SIZE, -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,
            -SkyboxSettings.SIZE, -SkyboxSettings.SIZE,  SkyboxSettings.SIZE,
            SkyboxSettings.SIZE, -SkyboxSettings.SIZE,  SkyboxSettings.SIZE
    };

    private static PNGFile[] TEXTURE_FILES = {
            new PNGFile("skybox/right"),
            new PNGFile("skybox/left"),
            new PNGFile("skybox/top"),
            new PNGFile("skybox/bottom"),
            new PNGFile("skybox/back"),
            new PNGFile("skybox/front")};
    private static PNGFile[] NIGHT_TEXTURE_FILES = {
            new PNGFile("skybox/nightRight"),
            new PNGFile("skybox/nightLeft"),
            new PNGFile("skybox/nightTop"),
            new PNGFile("skybox/nightBottom"),
            new PNGFile("skybox/nightBack"),
            new PNGFile("skybox/nightFront")};

    private RawModel cube;
    private int textureID;
    private int nightTextureID;
    private SkyboxShader shader;
    private float time = 0; // Goes from 0 to 1, where .5 is night. 1 and 0 are day.
    private boolean day = true;

    public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix) {
        cube = loader.loadToVAO(VERTICES, 3);
        textureID = loader.loadCubeMap(TEXTURE_FILES);
        nightTextureID = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
        shader = new SkyboxShader();
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Camera camera, float r, float g, float b){
        shader.start();
        shader.loadViewMatrix(camera);
        shader.loadFogColor(r, g, b);
        shader.loadLevels();
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        bindTextures();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private float getCurrentTimeOfDay(){

        float timeDifference = DisplayManager.getFrameTimeSeconds() / SkyboxSettings.TIME_FACTOR;
        if (time <= 0 - SkyboxSettings.DAY_LENGTH)
            day = true;
        else if (time >= 1 + SkyboxSettings.DAY_LENGTH)
            day = false;
        if (day)
            time += timeDifference;
        else
            time -= timeDifference;

        if (time < 0)
            return 0;
        else if (time > 1)
            return 1;

        return time;
    }

    private void bindTextures(){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, nightTextureID);
        shader.loadBlendFactor(getCurrentTimeOfDay());
    }

}
