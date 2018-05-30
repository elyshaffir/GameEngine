package shadows;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "/shadows/shadowVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/shadows/shadowFragmentShader.glsl";

	
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
