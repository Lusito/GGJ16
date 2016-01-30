package de.hochschuletrier.gdw.ss14.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class SelfDestructComponent extends Component implements Pool.Poolable {

    public float seconds;
    
    @Override
    public void reset() {
        seconds = 0;
    }
}
