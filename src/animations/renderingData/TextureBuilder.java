package animations.renderingData;

import animations.utils.TextureUtils;
import fileSystem.PNGFile;

public class TextureBuilder {

    private boolean clampEdges = false;
    private boolean mipmap = false;
    private boolean anisotropic = true;
    private boolean nearest = false;

    private PNGFile file;

    protected TextureBuilder(PNGFile textureFile){
        this.file = textureFile;
    }

    public Texture create(){
        TextureData textureData = TextureUtils.decodeTextureFile(file);
        int textureId = TextureUtils.loadTextureToOpenGL(textureData, this);
        return new Texture(textureId, textureData.getWidth());
    }

    public TextureBuilder clampEdges(){
        this.clampEdges = true;
        return this;
    }

    public TextureBuilder normalMipMap(){
        this.mipmap = true;
        this.anisotropic = false;
        return this;
    }

    public TextureBuilder nearestFiltering(){
        this.mipmap = false;
        this.anisotropic = false;
        this.nearest = true;
        return this;
    }

    public TextureBuilder anisotropic(){
        this.mipmap = true;
        this.anisotropic = true;
        return this;
    }

    public boolean isClampEdges() {
        return clampEdges;
    }

    public boolean isMipmap() {
        return mipmap;
    }

    public boolean isAnisotropic() {
        return anisotropic;
    }

    public boolean isNearest() {
        return nearest;
    }

}

