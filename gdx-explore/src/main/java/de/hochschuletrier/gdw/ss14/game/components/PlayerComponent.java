package de.hochschuletrier.gdw.ss14.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import de.hochschuletrier.gdw.ss14.game.utils.WaterRectangle;
import java.util.HashSet;

public class PlayerComponent extends Component implements Pool.Poolable {
    public final HashSet<WaterRectangle> waterRects = new HashSet();
    public float waterDistance = 0;

    @Override
    public void reset() {
        waterRects.clear();
    }
}
