package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.components.InputComponent;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
import de.hochschuletrier.gdw.ss14.game.components.RitualCasterComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationState;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationStateComponent;

public class AnimationStateSystem extends IteratingSystem {

    @SuppressWarnings("unchecked")
    public AnimationStateSystem(int priority) {
        super(Family.all(AnimationComponent.class, AnimationStateComponent.class, PositionComponent.class).get(), priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationStateComponent animState = ComponentMappers.animState.get(entity);
        AnimationComponent animComp = ComponentMappers.animation.get(entity);
        InputComponent inputComp = ComponentMappers.input.get(entity);
        PositionComponent posComp = ComponentMappers.position.get(entity);
        
        RitualCasterComponent ritualComp = ComponentMappers.ritualCaster.get(entity);
        if(ritualComp!=null && ritualComp.remainingTime>0.f) {
            if(posComp.directionY > 0.0f) {
                animComp.animation = animState.changeTo(AnimationState.SUMMON_DOWN);
            } else if(posComp.directionY < 0.0f) {
                animComp.animation = animState.changeTo(AnimationState.SUMMON_UP);
            } else if(posComp.directionX < 0.0f) {
                animComp.animation = animState.changeTo(AnimationState.SUMMON_LEFT);
            } else {
                animComp.animation = animState.changeTo(AnimationState.SUMMON_RIGHT);
            }
            return;
        }
        
        boolean idle = false;
        
        if(inputComp != null) {
            if(inputComp.moveX > 0.0f) {
                animComp.animation = animState.changeTo(AnimationState.WALK_RIGHT);
            } else if(inputComp.moveX < 0.0f) {
                animComp.animation = animState.changeTo(AnimationState.WALK_LEFT);
            } else if(inputComp.moveY < 0.0f) {
                animComp.animation = animState.changeTo(AnimationState.WALK_UP);
            } else if(inputComp.moveY > 0.0f) {
                animComp.animation = animState.changeTo(AnimationState.WALK_DOWN);
            } else {
                idle = true;
            }
        } else {
            idle = true;
        }
        
        if(idle) {
            if(posComp.directionY > 0.0f) {
                animComp.animation = animState.changeTo(AnimationState.IDLE_DOWN);
            } else if(posComp.directionY < 0.0f) {
                animComp.animation = animState.changeTo(AnimationState.IDLE_UP);
            } else if(posComp.directionX < 0.0f) {
                animComp.animation = animState.changeTo(AnimationState.IDLE_LEFT);
            } else if(posComp.directionX > 0.0f) {
                animComp.animation = animState.changeTo(AnimationState.IDLE_RIGHT);
            }
        }
    }
}
