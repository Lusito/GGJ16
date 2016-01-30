package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

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
            UP2 = Input.Keys.W,
            DOWN2 = Input.Keys.S,
            LEFT2 = Input.Keys.A,
            RIGHT2 = Input.Keys.D,
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
        	PhysixBodyComponent body = ComponentMappers.physixBody.get(entity);
        	body.setLinearVelocity(new Vector2());
            input.blockInputTime -= deltaTime;
            return;
        }
        
        adjustInputs(entity, deltaTime);
        adjustMovements(entity, deltaTime);
    }

    private void adjustInputs(Entity entity, float deltaTime) {
        InputComponent input = ComponentMappers.input.get(entity);
        PositionComponent posComponent = ComponentMappers.position.get(entity);
        
        
        if (Gdx.input.isKeyPressed(UP) || Gdx.input.isKeyPressed(UP2)) {
            input.moveY = -1;
            posComponent.directionY = -1;
            posComponent.directionX = 0;
        } else if (Gdx.input.isKeyPressed(DOWN) || Gdx.input.isKeyPressed(DOWN2)) {
            input.moveY = 1;
            posComponent.directionY = 1;
            posComponent.directionX = 0;
        } else {
            input.moveY = 0;
        }

        if (Gdx.input.isKeyPressed(LEFT) || Gdx.input.isKeyPressed(LEFT2)) {
            input.moveX = -1;
            posComponent.directionX = -1;
            posComponent.directionY = 0;
        } else if (Gdx.input.isKeyPressed(RIGHT) || Gdx.input.isKeyPressed(RIGHT2)) {
            input.moveX = 1;
            posComponent.directionX = 1;
            posComponent.directionY = 0;
        } else {
            input.moveX = 0;
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

        final float speed = 150f;
        float moveLen = Vector2.len(input.moveX, input.moveY);
        
        float velX=0.f, velY=0.f;
        
        if(moveLen>0.f) {
            velX = input.moveX / moveLen * speed;
            velY = input.moveY / moveLen * speed;
        }

        body.setLinearVelocityX(velX);
        body.setLinearVelocityY(velY);
    }

}
