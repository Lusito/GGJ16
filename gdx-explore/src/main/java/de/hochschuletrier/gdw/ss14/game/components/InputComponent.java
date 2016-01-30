package de.hochschuletrier.gdw.ss14.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class InputComponent extends Component implements Pool.Poolable {

    public float moveX = 0f;
    public float moveY = 0f;
    public boolean isActionPressed = false;
    public boolean isActionUpPressed = false;
    public boolean isActionDownPressed = false;

    @Override
    public void reset() {
        moveX = 0f;
        moveY = 0f;
        isActionPressed = false;
        isActionUpPressed = false;
        isActionDownPressed = false;
    }
}
