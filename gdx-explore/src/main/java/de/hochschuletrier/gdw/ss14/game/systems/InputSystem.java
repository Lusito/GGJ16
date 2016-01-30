package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.ss14.events.InputActionEvent;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.components.InputComponent;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;

public class InputSystem extends IteratingSystem {

    public static int UP = Input.Keys.UP,
            DOWN = Input.Keys.DOWN,
            LEFT = Input.Keys.LEFT,
            RIGHT = Input.Keys.RIGHT,
            ACTION = Input.Keys.SPACE,
            ACTION_UP = Input.Keys.E,
            ACTION_DOWN = Input.Keys.Q;

    public InputSystem() {
        this(0);
    }

    @SuppressWarnings("unchecked")
    public InputSystem(int priority) {
        super(Family.all(InputComponent.class, PhysixBodyComponent.class, PositionComponent.class).get(), priority);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {

        InputComponent input = ComponentMappers.input.get(entity);
        if(input.blockInputTime>0.f) {
            input.blockInputTime -= deltaTime;
            return;
        }
        
        adjustInputs(entity, deltaTime);
        adjustMovements(entity, deltaTime);
    }

    private void adjustInputs(Entity entity, float deltaTime) {
        InputComponent input = ComponentMappers.input.get(entity);
        PositionComponent posComponent = ComponentMappers.position.get(entity);
        
        final float speed = 150f;

        if (Gdx.input.isKeyPressed(LEFT)) {
            input.moveX = -speed;
            posComponent.directionX = -1;
            posComponent.directionY = 0;
        } else if (Gdx.input.isKeyPressed(RIGHT)) {
            input.moveX = speed;
            posComponent.directionX = 1;
            posComponent.directionY = 0;
        } else {
            input.moveX = 0;
        }
        
        if (Gdx.input.isKeyPressed(UP)) {
            input.moveY = -speed;
            posComponent.directionY = -1;
            posComponent.directionX = 0;
        } else if (Gdx.input.isKeyPressed(DOWN)) {
            input.moveY = speed;
            posComponent.directionY = 1;
            posComponent.directionX = 0;
        } else {
            input.moveY = 0;
        }

        boolean actionPressedNow = Gdx.input.isKeyPressed(ACTION);
        if(!actionPressedNow && input.isActionPressed) {
            InputActionEvent.emitDoAction(entity);
        }
        input.isActionPressed = actionPressedNow;

        boolean actionUpPressedNow = Gdx.input.isKeyPressed(ACTION_UP);
        if(!actionUpPressedNow && input.isActionUpPressed) {
            InputActionEvent.emitChangeAction(entity, 1);
        }
        input.isActionUpPressed = actionUpPressedNow;

        boolean actionDownPressedNow = Gdx.input.isKeyPressed(ACTION_DOWN);
        if(!actionDownPressedNow && input.isActionDownPressed) {
            InputActionEvent.emitChangeAction(entity, -1);
        }
        input.isActionDownPressed = actionDownPressedNow;
    }

    private void adjustMovements(Entity entity, float deltaTime) {
        PhysixBodyComponent body = ComponentMappers.physixBody.get(entity);
        InputComponent input = ComponentMappers.input.get(entity);
        
        body.setLinearVelocityX(input.moveX);
        body.setLinearVelocityY(input.moveY);
    }

}
