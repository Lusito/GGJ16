package de.hochschuletrier.gdw.ss14.game.systems.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.hochschuletrier.gdw.commons.gdx.ashley.SortedSubIteratingSystem.SubSystem;
import de.hochschuletrier.gdw.commons.gdx.utils.DrawUtil;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationComponent;

public class AnimationRenderer extends SubSystem {

    @SuppressWarnings("unchecked")
	public AnimationRenderer() {
        super(Family.all(AnimationComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
    	AnimationComponent animation = ComponentMappers.animation.get(entity);
        PositionComponent position = ComponentMappers.position.get(entity);

        if (animation.isActive) {
            animation.stateTime += deltaTime;
            animation.permanentStateTime += deltaTime;
            if (animation.stateTime >= animation.animation.animationDuration) {
                animation.stateTime %= animation.animation.animationDuration;
                animation.animationFinished = true;
            }
        }
        
        TextureRegion keyFrame = animation.animation.getKeyFrame(animation.permanentStateTime);
        int w = keyFrame.getRegionWidth();
        int h = keyFrame.getRegionHeight();
        
        keyFrame.flip(keyFrame.isFlipX() != animation.flipX, keyFrame.isFlipY() != !animation.flipY); 

        float x = position.x - w * 0.5f + (keyFrame.isFlipX() ? - animation.offsetX : animation.offsetX);
        float y = position.y - h * 0.5f + (keyFrame.isFlipY() ? - animation.offsetY : animation.offsetY);
        DrawUtil.batch.draw(keyFrame, x, y, w * 0.5f, h * 0.5f, w, h,  
        		position.scaleX, position.scaleY, position.rotation);
    }
}
