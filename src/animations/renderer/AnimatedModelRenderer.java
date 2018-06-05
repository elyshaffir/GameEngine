package animations.renderer;

import animations.animatedModel.AnimatedModel;
import cameras.Camera;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

import java.util.List;

/**
 * 
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 * 
 * @author Karl
 *
 */
public class AnimatedModelRenderer {

	private AnimatedModelShader shader;

	/**
	 * Initializes the shader program used for rendering animated models.
	 */
	public AnimatedModelRenderer() {
		this.shader = new AnimatedModelShader();
	}

	public void render(List<AnimatedModel> entities, Camera camera, Vector3f lightDir, Matrix4f projectionMatrix) {
		for (AnimatedModel entity:entities)
			render(entity, camera, lightDir, projectionMatrix);
	}

	/**
	 * Renders an animated entity. The main thing to note here is that all the
	 * joint transforms are loaded up to the shader to a uniform array. Also 5
	 * attributes of the VAO are enabled before rendering, to include joint
	 * indices and weights.
	 * 
	 * @param entity
	 *            - the animated entity to be rendered.
	 * @param camera
	 *            - the camera used to render the entity.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	public void render(AnimatedModel entity, Camera camera, Vector3f lightDir, Matrix4f projectionMatrix) {
		prepare(camera, lightDir, projectionMatrix);
		entity.getTexture().bindToUnit(0);
		entity.getModel().bind(0, 1, 2, 3, 4);
		shader.jointTransforms.loadMatrixArray(entity.getJointTransforms());
		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		entity.getModel().unbind(0, 1, 2, 3, 4);
		finish();
	}

	/**
	 * Deletes the shader program when the game closes.
	 */
	public void cleanUp() {
		shader.cleanUp();
	}

	/**
	 * Starts the shader program and loads up the projection view matrix, as
	 * well as the light direction. Enables and disables a few settings which
	 * should be pretty self-explanatory.
	 * 
	 * @param camera
	 *            - the camera being used.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	private void prepare(Camera camera, Vector3f lightDir, Matrix4f projectionMatrix) {
		shader.start();
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		shader.projectionViewMatrix.loadMatrix(Matrix4f.mul(projectionMatrix, viewMatrix, null));
		shader.lightDirection.loadVec3(lightDir);
	}

	/**
	 * Stops the shader program after rendering the entity.
	 */
	private void finish() {
		shader.stop();
	}

}
