package de.hochschuletrier.gdw.ss14.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PositionComponent extends Component implements Pool.Poolable {

    public float x;
    public float y;
    public float rotation;
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public int layer = 0;
    
    @Override
    public void reset() {
        rotation = x = y = layer = 0;
        scaleX = scaleY = 1.0f;
    }
}
