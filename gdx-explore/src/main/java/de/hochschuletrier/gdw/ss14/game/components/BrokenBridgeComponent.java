package de.hochschuletrier.gdw.ss14.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class BrokenBridgeComponent extends Component implements Pool.Poolable {
    public float repairRadius = 50.f;
    
    @Override
    public void reset() {
        repairRadius = 50.f;
    }

}
