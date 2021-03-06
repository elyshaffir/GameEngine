package terrain;


import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import settings.TerrainSettings;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;
import fileSystem.PNGFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Terrain {

    private float x;
    private float z;
    private RawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;
    private TerrainTexture heightMap;

    private float[][] heights;

    private boolean colorOfHeights = false;

    public Terrain(int gridX, float gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, PNGFile heightMap) {
        this.x = gridX * TerrainSettings.SIZE;
        this.z = gridZ * TerrainSettings.SIZE;
        this.model = generateTerrain(loader, heightMap);
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.heightMap = new TerrainTexture(loader.loadTexture(heightMap)); // FIXME!
    }

    public Terrain(int gridX, float gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap){
        this.x = gridX * TerrainSettings.SIZE;
        this.z = gridZ * TerrainSettings.SIZE;
        this.model = generateRandomTerrain(loader);
        this.texturePack = texturePack;
        this.blendMap = blendMap;
    }

    public Terrain(int gridX, float gridZ, Loader loader, TerrainTexturePack texturePack){
        this.x = gridX * TerrainSettings.SIZE;
        this.z = gridZ * TerrainSettings.SIZE;
        this.model = generateRandomTerrain(loader);
        this.texturePack = texturePack;
        this.colorOfHeights = true;
    }

    public Terrain(int gridX, float gridZ, Loader loader, TerrainTexturePack texturePack, PNGFile heightMap){
        this.x = gridX * TerrainSettings.SIZE;
        this.z = gridZ * TerrainSettings.SIZE;
        this.model = generateTerrain(loader, heightMap);
        this.texturePack = texturePack;
        this.heightMap = new TerrainTexture(loader.loadTexture(heightMap));
        this.colorOfHeights = true;
    }

    public float getHeightOfTerrain(float worldX, float worldZ){
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = TerrainSettings.SIZE / ((float) heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0)
            return 0;
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float ret;
        if (xCoord <= (1-zCoord)) {
            ret = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ], 0), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            ret = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        return ret;
    }

    private RawModel generateTerrain(Loader loader, PNGFile heightMap){

        BufferedImage image = null;
        try {
            image = ImageIO.read(heightMap.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int VERTEX_COUNT = image.getHeight();

        int count = VERTEX_COUNT * VERTEX_COUNT;
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i = 0; i < VERTEX_COUNT; i++){
            for(int j = 0; j < VERTEX_COUNT; j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * TerrainSettings.SIZE;
                float height = getHeight(j, i, image);
                heights[j][i] = height;
                vertices[vertexPointer*3+1] = height;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * TerrainSettings.SIZE;
                Vector3f normal = calculateNormal(j, i, image);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private RawModel generateRandomTerrain(Loader loader){

        HeightsGenerator generator = new HeightsGenerator();

        int VERTEX_COUNT = 128;

        int count = VERTEX_COUNT * VERTEX_COUNT;
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i = 0; i < VERTEX_COUNT; i++){
            for(int j = 0; j < VERTEX_COUNT; j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * TerrainSettings.SIZE;
                float height = getRandomTerrainHeight(j, i, generator);
                heights[j][i] = height;
                vertices[vertexPointer*3+1] = height;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * TerrainSettings.SIZE;
                Vector3f normal = calculateRandomTerrainNormal(j, i, generator);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private Vector3f calculateNormal(int x, int z, BufferedImage heightMap){
        float heightL = getHeight(x - 1, z, heightMap);
        float heightR = getHeight(x + 1, z, heightMap);
        float heightD = getHeight(x, z - 1, heightMap);
        float heightU = getHeight(x, z + 1, heightMap);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    private Vector3f calculateRandomTerrainNormal(int x, int z, HeightsGenerator generator){
        float heightL = getRandomTerrainHeight(x - 1, z, generator);
        float heightR = getRandomTerrainHeight(x + 1, z, generator);
        float heightD = getRandomTerrainHeight(x, z - 1, generator);
        float heightU = getRandomTerrainHeight(x, z + 1, generator);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }


    private float getHeight(int x, int z, BufferedImage image){
        if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight())
            return 0;
        float height = image.getRGB(x, z);
        height += TerrainSettings.MAX_PIXEL_COLOR / 2f;
        height /= TerrainSettings.MAX_PIXEL_COLOR / 2f;
        height *= TerrainSettings.MAX_HEIGHT;
        return height;
    }

    private float getRandomTerrainHeight(int x, int z, HeightsGenerator generator){
        return generator.generateHeight(x, z);
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public TerrainTexture getHeightMap() {
        return heightMap;
    }

    public boolean isColorOfHeights() {
        return colorOfHeights;
    }
}
