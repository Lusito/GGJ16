package de.hochschuletrier.gdw.ss14.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

import de.hochschuletrier.gdw.ss14.game.gamelogic.MaterialType;

public class MaterialComponent extends Component implements Pool.Poolable {
    public MaterialType type = MaterialType.NONE;
    
    @Override
    public void reset() {
        type = MaterialType.NONE;
    }

}
