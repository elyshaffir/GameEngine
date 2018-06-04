package postProcessing;

import shaders.ShaderProgram;
import fileSystem.GLSLFile;

public class ContrastShader extends ShaderProgram {

	private static final GLSLFile VERTEX_FILE = new GLSLFile("postProcessing/postProcessingVertex");
	private static final GLSLFile FRAGMENT_FILE = new GLSLFile("postProcessing/contrastFragment");

	private int location_contrast;
	
	public ContrastShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_contrast = super.getUniformLocation("contrast");
	}

	public void loadContrast(float contrast){
		super.loadFloat(location_contrast, contrast);
	}


	@Override
	protected void bindAttributes() {
		super.bindAttributes(0, "position");
	}

}
