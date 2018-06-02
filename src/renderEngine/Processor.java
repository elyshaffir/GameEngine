package renderEngine;

import entities.Entity;
import models.TexturedModel;
import scenes.Scene;
import terrain.Terrain;
import water.WaterTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Processor {
    static Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    static Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<>();
    static List<Terrain> terrains = new ArrayList<>();
    static List<WaterTile> waterTiles = new ArrayList<>();

    private static void processAll(List<WaterTile> waterTileList, List<Terrain> terrainList, List<Entity> entityList, List<Entity> normalMapEntityList){
        processWaters(waterTileList);
        processTerrains(terrainList);
        processEntities(entityList);
        processNormalMapEntities(normalMapEntityList);
    }

    static void processScene(Scene scene){
        processAll(scene.getWaterTileList(), scene.getTerrainList(), scene.getEntityList(), scene.getNormalMapEntityList());
    }

    private static void processForWater(List<Terrain> terrainList, List<Entity> entityList, List<Entity> normalMapEntityList){
        processTerrains(terrainList);
        processEntities(entityList);
        processNormalMapEntities(normalMapEntityList);
    }

    static void processForWater(Scene scene){
        processForWater(scene.getTerrainList(), scene.getEntityList(), scene.getNormalMapEntityList());
    }

    static void processForShadow(List<Entity> entityList){
        processEntities(entityList);
    }

    static void clearForShadow(){
        entities.clear();
    }

    static void clearAll(){
        waterTiles.clear();
        terrains.clear();
        entities.clear();
        normalMapEntities.clear();
    }

    private static void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }

    private static void processTerrains(List<Terrain> terrainList){
        for (Terrain terrain:terrainList)
            processTerrain(terrain);
    }

    private static void processWater(WaterTile water){
        waterTiles.add(water);
    }

    private static void processWaters(List<WaterTile> waterTileList){
        for (WaterTile waterTile:waterTileList)
            processWater(waterTile);
    }

    private static void processEntity(Entity entity){
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null){
            batch.add(entity);
        }
        else{
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    private static void processNormalMapEntity(Entity entity){
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = normalMapEntities.get(entityModel);
        if (batch != null){
            batch.add(entity);
        }
        else{
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            normalMapEntities.put(entityModel, newBatch);
        }
    }

    private static void processEntities(List<Entity> entities){
        for (Entity entity:entities)
            processEntity(entity);
    }

    private static void processNormalMapEntities(List<Entity> entities){
        for (Entity entity:entities)
            processNormalMapEntity(entity);
    }
}
