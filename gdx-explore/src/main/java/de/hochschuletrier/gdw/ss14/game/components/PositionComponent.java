package de.hochschuletrier.gdw.ss14.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class PositionComponent extends Component implements Pool.Poolable {

    public float x;
    public float y;
    public float rotation;

    @Override
    public void reset() {
        rotation = x = y = 0;
    }
    
    public Vector2 getDirectionVector() {
    	return new Vector2(1, 0).rotate(rotation);
    }
}
