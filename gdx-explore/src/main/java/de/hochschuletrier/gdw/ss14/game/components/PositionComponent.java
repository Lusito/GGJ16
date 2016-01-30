package de.hochschuletrier.gdw.ss14.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class PositionComponent extends Component implements Pool.Poolable {

    public float x;
    public float y;
    public float rotation;
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public int layer = 0;
    public int directionX;
    public int directionY = 1;
    
    @Override
    public void reset() {
        rotation = x = y = layer = 0;
        scaleX = scaleY = 1.0f;
        directionX = 0;
        directionY = 1;
    }

    public Vector2 getDirectionVector() {
        return new Vector2(1, 0).rotate(rotation);
    }
}
