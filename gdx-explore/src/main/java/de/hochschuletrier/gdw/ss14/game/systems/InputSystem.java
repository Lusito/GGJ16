package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;

import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.components.InputComponent;

public class InputSystem extends IteratingSystem {
	
	public static int 	UP = Input.Keys.UP, 
						DOWN = Input.Keys.DOWN, 
						LEFT = Input.Keys.LEFT, 
						RIGHT = Input.Keys.RIGHT,
						ACTION = Input.Keys.SPACE;
	

    public InputSystem() {
        this(0);
    }

    public InputSystem(int priority) {
        super(Family.all(InputComponent.class, PhysixBodyComponent.class).get(), priority);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
    	
    	adjustInputs(entity, deltaTime);
    	adjustMovements(entity, deltaTime);
    }
    
    private void adjustInputs(Entity entity, float deltaTime){
    	InputComponent input = ComponentMappers.input.get(entity);
    	
    	final float speed = 50f;
    	
    	if(Gdx.input.isKeyPressed(UP)){
    		input.moveY = -speed;
    	} else if(Gdx.input.isKeyPressed(DOWN)){
    		input.moveY = speed;
    	} else {
    		input.moveY = 0;
    	}
    	
    	if(Gdx.input.isKeyPressed(LEFT)){
    		input.moveX = -speed;
    	} else if(Gdx.input.isKeyPressed(RIGHT)){
    		input.moveX = speed;
    	}else {
    		input.moveX = 0;
    	}
    	
    	input.isActionPressed = Gdx.input.isKeyPressed(ACTION);
    }
    
    private void adjustMovements(Entity entity, float deltaTime){
    	PhysixBodyComponent body = ComponentMappers.physixBody.get(entity);
    	InputComponent input = ComponentMappers.input.get(entity);
    	
    	body.setLinearVelocityX(input.moveX);
    	body.setLinearVelocityY(input.moveY);
    }

}