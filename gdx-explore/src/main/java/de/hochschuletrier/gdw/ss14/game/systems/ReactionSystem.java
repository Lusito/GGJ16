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
            else if(mat2.type == MaterialType.EXPLOSION)
                onStoneExplosionReaction(second, first);
            break;
        case STONE:
            if(mat2.type == MaterialType.EXPLOSION)
                onStoneExplosionReaction(first, second);
            break;
        case EXPLOSION:
            if(mat2.type == MaterialType.ICE || mat2.type == MaterialType.STONE)
                onStoneExplosionReaction(second,first);
            break;
        case NONE:
            break;
        default:
            break;
        }
    }
    
    private void onStoneExplosionReaction(Entity stone, Entity expl) {
        PositionComponent posComp = ComponentMappers.position.get(stone);
        float dx = ComponentMappers.position.get(expl).x - posComp.x;
        float dy = ComponentMappers.position.get(expl).y - posComp.y;
        Game.entityBuilder.createEntity("explosion", posComp.x + dx, posComp.y + dy);
        engine.removeEntity(stone);
    }

    private void onFireIceReaction(Entity fire, Entity ice) {
        PositionComponent posComp = ComponentMappers.position.get(fire);
        float dx = ComponentMappers.position.get(ice).x - posComp.x;
        float dy = ComponentMappers.position.get(ice).y - posComp.y;
        Game.entityBuilder.createEntity("explosion", posComp.x + dx, posComp.y + dy);
        engine.removeEntity(fire);
        engine.removeEntity(ice);
    }
}
