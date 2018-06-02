package guis;

import org.lwjgl.util.vector.Matrix4f;
import shaders.ShaderProgram;
import toolbox.GLSLFile;


public class GuiShader extends ShaderProgram {

    private static final GLSLFile VERTEX_FILE = new GLSLFile("guis/guiVertexShader");
    private static final GLSLFile FRAGMENT_FILE = new GLSLFile("guis/guiFragmentShader");

    private int location_transformationMatrix;

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttributes(0, "position");
    }




}
