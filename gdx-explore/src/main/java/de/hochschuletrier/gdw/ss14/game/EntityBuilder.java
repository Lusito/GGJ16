package de.hochschuletrier.gdw.ss14.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

import de.hochschuletrier.gdw.commons.gdx.ashley.EntityFactory;
import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.ss14.game.components.factories.EntityFactoryParam;

public class EntityBuilder {

    private final PooledEngine engine;

    @SuppressWarnings({"unchecked", "rawtypes"})
    private final EntityFactory<EntityFactoryParam> entityFactory = new EntityFactory(
            "data/json/entities.json", Game.class);

    public EntityBuilder(PooledEngine engine) {
        this.engine = engine;
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

    public void init(AssetManagerX assetManager) {
        entityFactory.init(engine, assetManager);
    }

}
