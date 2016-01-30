package de.hochschuletrier.gdw.ss14.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

import de.hochschuletrier.gdw.commons.gdx.ashley.EntityFactory;
import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.tiled.Layer;
import de.hochschuletrier.gdw.commons.tiled.LayerObject;
import de.hochschuletrier.gdw.commons.tiled.TiledMap;
import de.hochschuletrier.gdw.ss14.game.components.factories.EntityFactoryParam;

public class EntityBuilder {

    private final PooledEngine engine;

    @SuppressWarnings({"unchecked", "rawtypes"})
    private final EntityFactory<EntityFactoryParam> entityFactory = new EntityFactory(
            "data/json/entities.json", Game.class);

    public EntityBuilder(PooledEngine engine) {
        this.engine = engine;
    }
    
    public void init(AssetManagerX assetManager) {
        entityFactory.init(engine, assetManager);
    }

    public Entity createEntity(String name, float x, float y) {
        EntityFactoryParam factoryParam = new EntityFactoryParam();
        factoryParam.x = x;
        factoryParam.y = y;
        // TODO: set factoryParam.game ?
        Entity entity = entityFactory.createEntity(name, factoryParam);

        engine.addEntity(entity);
        return entity;
    }
    
    public void removeEntity(Entity entity) {
        engine.removeEntity(entity);
    }

    public void createEntitiesFromMap(TiledMap map) {
            for (Layer layer : map.getLayers()) {
                if (layer.isObjectLayer()) {
                    for (LayerObject obj : layer.getObjects()) {
                        String type = obj.getProperty("EntityType", null);
                        if (type != null) {
                            createEntity(type, obj.getX() + obj.getWidth() / 2.0f,
                                    obj.getY() - obj.getHeight() / 2.0f);
                        }
                    }
                }
            }
    }

}
