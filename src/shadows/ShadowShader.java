package shadows;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;
import fileSystem.GLSLFile;

public class ShadowShader extends ShaderProgram {
	
	private static final GLSLFile VERTEX_FILE = new GLSLFile("shadows/shadowVertexShader");
	private static final GLSLFile FRAGMENT_FILE = new GLSLFile("shadows/shadowFragmentShader");

	
	private int location_mvpMatrix;

	protected ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
		
	}
	
	protected void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttributes(0, "in_position");
		super.bindAttributes(1, "in_textureCoords");
	}

}
