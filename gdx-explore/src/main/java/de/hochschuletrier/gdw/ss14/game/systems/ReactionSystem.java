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
            case ICE:
                if(mat2.type==MaterialType.FIRE)
                    onFireIceReaction(second,first);
                break;
                
            case FIRE:
                if(mat2.type == MaterialType.ICE || mat2.type == MaterialType.WOOD)
                    onFireIceReaction(first, second);
                break;
                
            case EXPLOSION:
                if(mat2.type==MaterialType.BOULDER)
                    onBoulderExplosionReaction(second,first);
                break;
                
            case BOULDER:
                if(mat2.type==MaterialType.EXPLOSION)
                    onBoulderExplosionReaction(first,second);
                break;
                
            case WOOD:
                if(mat2.type==MaterialType.FIRE)
                    onFireIceReaction(second,first);
                break;
                
            case ELEMENTAL:
                if(mat2.type==MaterialType.MONSTER)
                    onMonsterElementalReaction(second,first);
                break;
                
            case MONSTER:
                if(mat2.type==MaterialType.ELEMENTAL)
                    onMonsterElementalReaction(first,second);
                break;
        
            default:
                break;
        }
        
        if(mat1.type==MaterialType.FIRE || mat1.type==MaterialType.ELEMENTAL)
            engine.removeEntity(first);
        
        if(mat2.type==MaterialType.FIRE || mat1.type==MaterialType.ELEMENTAL)
            engine.removeEntity(second);
    }
    
    private void onBoulderExplosionReaction(Entity boulder, Entity expl) {
        PositionComponent posComp = ComponentMappers.position.get(boulder);
        Game.entityBuilder.createEntity("explosion", posComp.x, posComp.y);
        engine.removeEntity(boulder);
    }

    private void onFireIceReaction(Entity fire, Entity ice) {
        PositionComponent posComp = ComponentMappers.position.get(ice);
        Game.entityBuilder.createEntity("explosion", posComp.x, posComp.y);
        engine.removeEntity(ice);
    }

    private void onMonsterElementalReaction(Entity monster, Entity elemental) {
        PositionComponent posComp = ComponentMappers.position.get(monster);
        Game.entityBuilder.createEntity("explosion", posComp.x, posComp.y);
        engine.removeEntity(monster);
    }
}
