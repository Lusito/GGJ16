package de.hochschuletrier.gdw.ss14.game.components.render;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

import de.hochschuletrier.gdw.commons.gdx.assets.AnimationExtended;

/**
 * 
 * Use for anything that is supposed to be animated in the game. <br>
 * If it's not animated use TextureComponent. <br>
 * The animation can be easily set with the global available AssetManagerX class. <br>
 * Usage example: assetManager.getAnimation("walking") <br>
 * Make sure the animation is <b>not null</b>. Otherwise you will get an assertion error or a NullPointerException. <br>
 * <br>
 * The animations can be found in the resources/animations folder. They should be set/changed in the available json file and loaded
 * at the start of the game.
 */
public class AnimationComponent extends Component implements Pool.Poolable {

    public boolean flipX = false;
    public boolean flipY = false;
    public boolean isActive = true;
    public AnimationExtended animation;
    public float stateTime;
    public float permanentStateTime;
    public boolean animationFinished;
    public float offsetX = 0.0f;
    public float offsetY = 0.0f;
    
    /**
     * Starting counting at 1.
     */
    public void setFrame(int frameNumber) {
    	assert(animation != null && frameNumber > 0);
    	
    	stateTime = animation.animationDuration / animation.getFrameCount() * (frameNumber - 1);
    	permanentStateTime = animation.animationDuration / animation.getFrameCount() * (frameNumber - 1);
    }
    
    @Override
    public void reset() {
        flipY = false;
        isActive = true;
        animation = null;
        stateTime = 0;
        permanentStateTime = 0;
        animationFinished = false;
    }
}
