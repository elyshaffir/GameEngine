package animations.loading;

import animations.animatedModel.*;
import animations.loading.colladaLoader.ColladaLoader;
import animations.renderingData.AnimatedMesh;
import animations.renderingData.MeshData;
import animations.renderingData.Texture;
import fileSystem.DAEFile;
import fileSystem.PNGFile;
import settings.AnimationSettings;

public class AnimatedModelLoader {

    /**
     * Creates an AnimatedEntity from the data in an entity file. It loads up
     * the collada model data, stores the extracted data in a VAO, sets up the
     * joint heirarchy, and loads up the entity's texture.
     *
     * @param entityFile
     *            - the file containing the data for the entity.
     * @return The animated entity (no animations applied though)
     */
    public static AnimatedModel loadEntity(DAEFile modelFile, PNGFile textureFile) {
        AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, AnimationSettings.MAX_WEIGHTS);
        AnimatedMesh model = createAnimatedMesh(entityData.getMeshData());
        Texture texture = loadTexture(textureFile);
        SkeletonData skeletonData = entityData.getJointsData();
        Joint headJoint = createJoints(skeletonData.headJoint);
        return new AnimatedModel(model, texture, headJoint, skeletonData.jointCount);
    }

    /**
     * Loads up the diffuse texture for the model.
     *
     * @param textureFile
     *            - the texture file.
     * @return The diffuse texture.
     */
    private static Texture loadTexture(PNGFile textureFile) {
        Texture diffuseTexture = Texture.newTexture(textureFile).anisotropic().create();
        return diffuseTexture;
    }

    /**
     * Constructs the joint-hierarchy skeleton from the data extracted from the
     * collada file.
     *
     * @param data
     *            - the joints data from the collada file for the head joint.
     * @return The created joint, with all its descendants added.
     */
    private static Joint createJoints(JointData data) {
        Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
        for (JointData child : data.children) {
            joint.addChild(createJoints(child));
        }
        return joint;
    }

    /**
     * Stores the mesh data in a VAO.
     *
     * @param data
     *            - all the data about the mesh that needs to be stored in the
     *            VAO.
     * @return The VAO containing all the mesh data for the model.
     */
    private static AnimatedMesh createAnimatedMesh(MeshData data) {
        AnimatedMesh vao = AnimatedMesh.create();
        vao.bind();
        vao.createIndexBuffer(data.getIndices());
        vao.createAttribute(0, data.getVertices(), 3);
        vao.createAttribute(1, data.getTextureCoords(), 2);
        vao.createAttribute(2, data.getNormals(), 3);
        vao.createIntAttribute(3, data.getJointIds(), 3);
        vao.createAttribute(4, data.getVertexWeights(), 3);
        vao.unbind();
        return vao;
    }

}
