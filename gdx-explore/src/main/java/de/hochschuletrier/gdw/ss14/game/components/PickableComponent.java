package de.hochschuletrier.gdw.ss14.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PickableComponent extends Component implements Pool.Poolable {

    public int resourceCount;
    
    public String resourceId;
    
    public String ritualId;
    
    @Override
    public void reset() {
        resourceCount = 0;
        resourceId = null;
        ritualId = null;
    }
}
