package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;

import de.hochschuletrier.gdw.ss14.events.ReactionEvent;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.Game;
import de.hochschuletrier.gdw.ss14.game.components.MaterialComponent;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
import de.hochschuletrier.gdw.ss14.game.gamelogic.MaterialType;

public class ReactionSystem extends EntitySystem implements ReactionEvent.Listener {
    private PooledEngine engine;
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        
        this.engine = (PooledEngine) engine;
    }

    public ReactionSystem() {
        ReactionEvent.register(this);
    }

    @Override
    public void onReactionEvent(Entity first, Entity second) {
        MaterialComponent mat1 = ComponentMappers.mat.get(first);
        MaterialComponent mat2 = ComponentMappers.mat.get(second);
        
        switch(mat1.type) {
        case FIRE:
            if(mat2.type == MaterialType.ICE)
                onFireIceReaction(first, second);
            break;
        case ICE:
            if(mat2.type == MaterialType.FIRE)
                onFireIceReaction(second, first);
            break;
        case NONE:
            break;
        default:
            break;
        }
    }
    
    private void onFireIceReaction(Entity fire, Entity ice) {
        PositionComponent posComp = ComponentMappers.position.get(fire);
        Game.entityBuilder.createEntity("explosion", posComp.x, posComp.y);
        engine.removeEntity(fire);
        engine.removeEntity(ice);
    }
}
