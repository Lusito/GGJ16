package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixModifierComponent;
import de.hochschuletrier.gdw.ss14.events.TeleportEvent;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
import de.hochschuletrier.gdw.ss14.game.components.TeleportOutComponent;

public class TeleportSystem extends IteratingSystem implements TeleportEvent.Listener {
    private PooledEngine engine;

    public TeleportSystem() {
        this(0);
    }

    public TeleportSystem(int priority) {
        super(Family.all(TeleportOutComponent.class, PositionComponent.class).get(), priority);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        
        TeleportEvent.unregister(this);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.engine = (PooledEngine)engine;
        TeleportEvent.register(this);
    }

    @Override
    public void onTeleportEvent(Entity teleporterEntity, Entity playerEntity) {
        PositionComponent destination = ComponentMappers.position.get(getEntities().first());
        PhysixModifierComponent modifier = ComponentMappers.physixModifier.get(playerEntity);
        if(modifier == null) {
            modifier = engine.createComponent(PhysixModifierComponent.class);
            playerEntity.add(modifier);
        }
        modifier.schedule(()-> {
            PhysixBodyComponent body = ComponentMappers.physixBody.get(playerEntity);
            body.setPosition(destination.x, destination.y);
        });
    }

}
