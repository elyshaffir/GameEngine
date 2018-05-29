package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class ContrastChanger {

    private ImageRenderer renderer;
    private ContrastShader shader;

    private static final float CONTRAST = 0f;

    public ContrastChanger() {
        renderer = new ImageRenderer();
        shader = new ContrastShader();
    }

    public void render(int texture){
        shader.start();
        shader.loadContrast(CONTRAST);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp(){
        renderer.cleanUp();
        shader.cleanUp();
    }
}
