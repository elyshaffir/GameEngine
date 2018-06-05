package scenes;

import animations.animatedModel.AnimatedModel;
import cameras.Camera;
import entities.Entity;
import lights.Light;
import terrain.Terrain;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private Camera camera;
    private List<Light> lights;
    private Light sun;
    private List<Entity> entityList;
    private List<Entity> normalMapEntityList;
    private List<AnimatedModel> animatedEntities;
    private List<Entity> allEntities;
    private List<Terrain> terrainList;
    private List<WaterTile> waterTileList;

    public Scene(Camera camera, List<Light> lights, Light sun, List<Entity> entityList, List<Entity> normalMapEntityList, List<AnimatedModel> animatedEntities, List<Terrain> terrainList, List<WaterTile> waterTileList) {
        this.camera = camera;
        this.lights = lights;
        this.sun = sun;
        this.entityList = entityList;
        this.normalMapEntityList = normalMapEntityList;
        this.animatedEntities = animatedEntities;

        this.allEntities = new ArrayList<>(entityList);
        this.allEntities.addAll(normalMapEntityList);

        this.terrainList = terrainList;
        this.waterTileList = waterTileList;
    }

    public Camera getCamera() {
        return camera;
    }

    public List<Light> getLights() {
        return lights;
    }

    public Light getSun() {
        return sun;
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public List<Entity> getNormalMapEntityList() {
        return normalMapEntityList;
    }

    public List<Entity> getAllEntities() {
        return allEntities;
    }

    public List<Terrain> getTerrainList() {
        return terrainList;
    }

    public List<WaterTile> getWaterTileList() {
        return waterTileList;
    }

    public List<AnimatedModel> getAnimatedEntities() {
        return animatedEntities;
    }
}
