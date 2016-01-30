package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.components.InputComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationState;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationStateComponent;

public class AnimationStateSystem extends IteratingSystem {

    @SuppressWarnings("unchecked")
    public AnimationStateSystem(int priority) {
        super(Family.all(AnimationComponent.class, AnimationStateComponent.class,
                InputComponent.class).get(), priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationStateComponent animState = ComponentMappers.animState.get(entity);
        AnimationComponent animComp = ComponentMappers.animation.get(entity);
        InputComponent inputComp = ComponentMappers.input.get(entity);
                
        if(inputComp.moveY > 0.0f) {
            animComp.animation = animState.changeTo(AnimationState.WALK_DOWN);
        } else if(inputComp.moveY < 0.0f) {
            animComp.animation = animState.changeTo(AnimationState.WALK_UP);
        } else if(inputComp.moveX < 0.0f) {
            animComp.animation = animState.changeTo(AnimationState.WALK_LEFT);
        } else if(inputComp.moveX > 0.0f) {
            animComp.animation = animState.changeTo(AnimationState.WALK_RIGHT);
        }
    }
}
