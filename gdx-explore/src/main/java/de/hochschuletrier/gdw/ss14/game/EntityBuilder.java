package de.hochschuletrier.gdw.ss14.game;

import de.hochschuletrier.gdw.ss14.game.utils.WaterRectangle;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import de.hochschuletrier.gdw.commons.gdx.ashley.EntityFactory;
import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixBodyDef;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixFixtureDef;
import de.hochschuletrier.gdw.commons.gdx.physix.systems.PhysixSystem;
import de.hochschuletrier.gdw.commons.tiled.Layer;
import de.hochschuletrier.gdw.commons.tiled.LayerObject;
import de.hochschuletrier.gdw.commons.tiled.TileInfo;
import de.hochschuletrier.gdw.commons.tiled.TiledMap;
import de.hochschuletrier.gdw.commons.tiled.utils.RectangleGenerator;
import de.hochschuletrier.gdw.commons.utils.Rectangle;
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

    public void createEntitiesFromMap(TiledMap map, PhysixSystem physixSystem) {
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
        
        // Generate static world
        int tileWidth = map.getTileWidth();
        int tileHeight = map.getTileHeight();
        RectangleGenerator generator = new RectangleGenerator();
        generator.generate(map,
                (Layer layer, TileInfo info) -> info.getBooleanProperty("water", false),
                (Rectangle rect) -> addSolid(physixSystem, rect, tileWidth, tileHeight, true));
        generator.generate(map,
                (Layer layer, TileInfo info) -> info.getBooleanProperty("wall", false),
                (Rectangle rect) -> addSolid(physixSystem, rect, tileWidth, tileHeight, false));

    }

    private void addSolid(PhysixSystem physixSystem, Rectangle rect, int tileWidth, int tileHeight, boolean isWater) {
        float width = rect.width * tileWidth;
        float height = rect.height * tileHeight;
        final int x0 = rect.x * tileWidth;
        final int y0 = rect.y * tileHeight;
        float x = x0 + width / 2;
        float y = y0 + height / 2;

        PhysixBodyDef bodyDef = new PhysixBodyDef(BodyDef.BodyType.StaticBody, physixSystem)
                    .position(x, y).fixedRotation(false);
        Body body = physixSystem.getWorld().createBody(bodyDef);
        
        
        if(isWater) {
            body.createFixture(new PhysixFixtureDef(physixSystem)
            .density(1).friction(0.5f).shapeBox(width, height)
            .mask(GameConstants.MASK_EVERYTHING).category(GameConstants.CATEGORY_WATER));
            body.setUserData(new WaterRectangle(x0, y0, width, height));
        } else {
            body.createFixture(new PhysixFixtureDef(physixSystem)
            .density(1).friction(0.5f).shapeBox(width, height));
        }

    }
}
