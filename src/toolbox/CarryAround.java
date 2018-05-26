package toolbox;

import entities.Camera;
import entities.Entity;
import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import terrain.Terrain;

import java.util.List;

public class CarryAround extends MousePicker{

    private List<Entity> entities;
    private List<Light> lights;

    public CarryAround(Camera cam, Matrix4f projection, Terrain terrain, List<Entity> entities, List<Light> lights) {
        super(cam, projection, terrain);
        this.entities = entities;
        this.lights = lights;
    }

    public void update(){
        super.update();
        Vector3f terrainPoint = getCurrentTerrainPoint();
        if (terrainPoint != null){
            for (Entity entity:entities)
                entity.setPosition(terrainPoint);
            for (Light light:lights)
                light.setPosition(terrainPoint);
        }
    }
}
