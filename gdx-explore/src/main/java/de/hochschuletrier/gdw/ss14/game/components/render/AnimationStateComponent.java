package de.hochschuletrier.gdw.ss14.game.components.render;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

import de.hochschuletrier.gdw.commons.gdx.assets.AnimationExtended;

/**
 * Used for entities with different animation states. <br>
 * A player can be in an idle or jump state for example. <br>
 */
public class AnimationStateComponent extends Component implements Pool.Poolable {

    public HashMap<AnimationState, AnimationExtended> states = new HashMap<>();
    public AnimationState currentState;
    public boolean interruptible = true;

    public AnimationExtended get() {
        return states.get(currentState);
    }

    public void put(AnimationState state, AnimationExtended animation) {
        states.put(state, animation);
    }

    @Override
    public void reset() {
        states.clear();
        interruptible = true;
    }

}
