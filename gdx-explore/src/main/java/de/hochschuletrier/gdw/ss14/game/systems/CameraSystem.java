package de.hochschuletrier.gdw.ss14.game.systems;

import java.util.ArrayList;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;

import de.hochschuletrier.gdw.commons.tiled.TiledMap;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.MainCamera;
import de.hochschuletrier.gdw.ss14.game.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;


public class CameraSystem extends EntitySystem implements EntityListener {
    private Entity toFollow;
    
    private final ArrayList<Entity> entities = new ArrayList<>();
    
    public CameraSystem(int priority) {
        super(priority);
        MainCamera.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        @SuppressWarnings("unchecked")
        Family family = Family.all(PlayerComponent.class).get();
        engine.addEntityListener(family, this);
    }
   
    @Override
    public void update(float deltaTime) {
        if(toFollow != null) {
            PositionComponent toFollowPos = ComponentMappers.position.get(toFollow);
            if(toFollowPos != null) {
                MainCamera.setDestination(toFollowPos.x, toFollowPos.y);
            }
        }   
        
    	MainCamera.update(deltaTime);
    }
    
    public void adjustToMap(TiledMap map) {
        assert(map != null);
        
        float totalMapWidth = map.getWidth() * map.getTileWidth();
        float totalMapHeight = map.getHeight() * map.getTileHeight();
        MainCamera.setBounds(0, 0, totalMapWidth, totalMapHeight);
    }

    @Override
    public void entityAdded(Entity entity) {
        entities.add(entity);

        if(toFollow == null)
            follow(entity);
    }

    @Override
    public void entityRemoved(Entity entity) {
        entities.remove(entity);
        
        if(entity == toFollow)
            if(entities.size() > 0)
                follow(entities.get(0));
            else
                toFollow = null;
    }
    
    private void follow(Entity entity) {
        toFollow = entity;
        assert(ComponentMappers.position.get(entity) != null);
    }
}
