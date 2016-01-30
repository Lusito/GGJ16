package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.EntityBuilder;
import de.hochschuletrier.gdw.ss14.game.components.SelfDestructComponent;

public class SelfDestructSystem extends IteratingSystem {

    private final EntityBuilder entityBuilder;
    
    public SelfDestructSystem(EntityBuilder entityBuilder) {
        this(entityBuilder, 0);
    }

    @SuppressWarnings("unchecked")
    public SelfDestructSystem(EntityBuilder entityBuilder, int priority) {
        super(Family.all(SelfDestructComponent.class).get(), priority);
        this.entityBuilder = entityBuilder;
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        SelfDestructComponent comp = ComponentMappers.selfDestruct.get(entity);
        if(comp!=null) {
            comp.seconds -= deltaTime;
            if(comp.seconds<=0.f) {
                entityBuilder.removeEntity(entity);
            }
        }
    }

}
