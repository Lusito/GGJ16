package de.hochschuletrier.gdw.ss14.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class DeathComponent extends Component implements Pool.Poolable {
    public float timer;
    
    @Override
    public void reset() {
        timer = 0;
    }

}
