package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.Game;
import de.hochschuletrier.gdw.ss14.game.components.DeathComponent;

public class DeathSystem extends IteratingSystem {
    public DeathSystem(int priority) {
        super(Family.all(DeathComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DeathComponent deathComponent = ComponentMappers.death.get(entity);
        
        deathComponent.timer -= deltaTime;
        
        if(deathComponent.timer < 0.0f) {
            Game.engine.removeEntity(entity);
        }
    }
}
