package de.hochschuletrier.gdw.ss14.game.components.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixBodyDef;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixFixtureDef;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixModifierComponent;
import de.hochschuletrier.gdw.commons.gdx.physix.systems.PhysixSystem;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhysixBodyComponentFactory extends ComponentFactory<EntityFactoryParam> {

    public final static short PICKABLE = 0x0001;

    private static final Logger logger = LoggerFactory.getLogger(PhysixBodyComponentFactory.class);
    private PhysixSystem physixSystem;

    @Override
    public String getType() {
        return "PhysixBody";
    }

    @Override
    public void init(PooledEngine engine, AssetManagerX assetManager) {
        super.init(engine, assetManager);

        physixSystem = engine.getSystem(PhysixSystem.class);
    }

    @Override
    public void run(Entity entity, SafeProperties meta, SafeProperties properties, EntityFactoryParam param) {
        final PhysixModifierComponent modifyComponent = engine.createComponent(PhysixModifierComponent.class);
        modifyComponent.schedule(() -> {
            String type = properties.getString("type", "");
            switch (type) {
                case "circle":
                    addCircle(param, entity, properties);
                    break;
                case "box":
                    addBox(param, entity, properties);
                    break;
                default:
                    logger.error("Unknown type: {}", type);
                    break;
            }
        });
        entity.add(modifyComponent);
    }

    private void addCircle(EntityFactoryParam param, Entity entity, SafeProperties properties) {
        PhysixBodyComponent bodyComponent = getBodyComponent(param, entity, properties);
        PhysixFixtureDef fixtureDef = getFixtureDef(properties)
                .shapeCircle(properties.getFloat("size", 5));
        bodyComponent.createFixture(fixtureDef);
        entity.add(bodyComponent);
    }

    private void addBox(EntityFactoryParam param, Entity entity, SafeProperties properties) {
        PhysixBodyComponent bodyComponent = getBodyComponent(param, entity, properties);
        PhysixFixtureDef fixtureDef = getFixtureDef(properties)
                .shapeBox(properties.getFloat("size", 5), properties.getFloat("size", 5));
        bodyComponent.createFixture(fixtureDef);

        entity.add(bodyComponent);
    }

    private PhysixBodyComponent getBodyComponent(EntityFactoryParam param, Entity entity, SafeProperties properties) {
        PhysixBodyComponent bodyComponent = engine.createComponent(PhysixBodyComponent.class);
        PhysixBodyDef bodyDef = getBodyDef(param, properties);
        bodyComponent.init(bodyDef, physixSystem, entity);
        bodyComponent.setFixedRotation(properties.getBoolean("isFixedRotation", false));
        return bodyComponent;
    }

    private PhysixBodyDef getBodyDef(EntityFactoryParam param, SafeProperties properties) {
        final BodyType bodyType = properties.getBoolean("static") ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        return new PhysixBodyDef(bodyType, physixSystem)
                .position(param.x, param.y).fixedRotation(false);
    }

    private PhysixFixtureDef getFixtureDef(SafeProperties properties) {
        PhysixFixtureDef pfd = new PhysixFixtureDef(physixSystem)
                .density(properties.getFloat("density", 5))
                .friction(properties.getFloat("friction", 5))
                .restitution(properties.getFloat("restitution", 0))
                .sensor(properties.getBoolean("sensor"));

        if (properties.getBoolean("isPickable")) {
            pfd.category(PICKABLE);
        }

        return pfd;
    }
}
