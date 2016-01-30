package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;

import de.hochschuletrier.gdw.ss14.events.ReactionEvent;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.Game;
import de.hochschuletrier.gdw.ss14.game.components.MaterialComponent;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
import de.hochschuletrier.gdw.ss14.game.gamelogic.MaterialType;

public class ReactionSystem extends EntitySystem implements ReactionEvent.Listener {
    private PooledEngine engine;
    private Vector2 vec2 = new Vector2();
    
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
        
        if(mat2.type==MaterialType.FIRE || mat2.type==MaterialType.ELEMENTAL)
            engine.removeEntity(second);
    }
    
    private void onBoulderExplosionReaction(Entity boulder, Entity expl) {
        PositionComponent exploPos = ComponentMappers.position.get(expl);
        Game.entityBuilder.createEntity("explosion", exploPos.x, exploPos.y);
        engine.removeEntity(boulder);
    }

    private void onFireIceReaction(Entity fire, Entity ice) {
        Vector2 exploPos = getExplosionPos(fire, ice);
        Game.entityBuilder.createEntity("explosion", exploPos.x, exploPos.y);
        engine.removeEntity(fire);
        engine.removeEntity(ice);
    }

    private void onMonsterElementalReaction(Entity monster, Entity elemental) {
        Vector2 exploPos = getExplosionPos(monster, elemental);
        Game.entityBuilder.createEntity("explosion", exploPos.x, exploPos.y);
        engine.removeEntity(monster);
    }
    
    public static void explode(Entity entity) {
        PositionComponent posComp = ComponentMappers.position.get(entity);
        float addX = posComp.directionX * 25.f;
        float addY = posComp.directionY * 25.f;
        Game.entityBuilder.createEntity("explosion", posComp.x + addX, posComp.y + addY);
        Game.engine.removeEntity(entity);
    }
    
    private Vector2 getExplosionPos(Entity first, Entity second) {
        PositionComponent posComp = ComponentMappers.position.get(first);
        float dx = ComponentMappers.position.get(second).x - posComp.x;
        float dy = ComponentMappers.position.get(second).y - posComp.y;
        vec2.set(posComp.x + dx, posComp.y + dy);
        return vec2;
    }
}
