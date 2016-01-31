package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import de.hochschuletrier.gdw.ss14.events.ExplosionEvent;

import de.hochschuletrier.gdw.ss14.events.ReactionEvent;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.Game;
import de.hochschuletrier.gdw.ss14.game.components.DeathComponent;
import de.hochschuletrier.gdw.ss14.game.components.MaterialComponent;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;

public class DeathSystem extends IteratingSystem {
    
    @SuppressWarnings("unchecked")
    public DeathSystem(int priority) {
        super(Family.all(DeathComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DeathComponent deathComponent = ComponentMappers.death.get(entity);
        
        deathComponent.timer -= deltaTime;
        
        if(deathComponent.timer < 0.0f) {
            if(deathComponent.explosionRadius>0.f) {
                onExplosion(entity, deathComponent);
            }
            Game.engine.removeEntity(entity);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void onExplosion(Entity entity, DeathComponent deathComponent) {
        PositionComponent position = ComponentMappers.position.get(entity);
        if(position==null)
            return;

        Game.entityBuilder.createEntity("explosion", position.x, position.y);
        ExplosionEvent.emit(position.x, position.y);
        
        
        float maxRadius2 = deathComponent.explosionRadius * deathComponent.explosionRadius;
        
        for(Entity target : Game.engine.getEntitiesFor(Family.all(PositionComponent.class, MaterialComponent.class).get())) {
            PositionComponent targetPos = ComponentMappers.position.get(target);
            float dist2 = Vector2.len2(position.x-targetPos.x, position.y-targetPos.y);
            if(dist2<maxRadius2) {
                ReactionEvent.emit(entity, target);
            }
        }
    }
}
