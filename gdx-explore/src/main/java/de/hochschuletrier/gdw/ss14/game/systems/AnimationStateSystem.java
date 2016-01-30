package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.hochschuletrier.gdw.ss14.game.components.render.AnimationComponent;

public class AnimationStateSystem extends IteratingSystem {

    @SuppressWarnings("unchecked")
    public AnimationStateSystem(int priority) {
        super(Family.all(AnimationComponent.class).get(), priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }
}
