package de.hochschuletrier.gdw.ss14.game.systems.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Texture;

import de.hochschuletrier.gdw.commons.gdx.ashley.SortedSubIteratingSystem.SubSystem;
import de.hochschuletrier.gdw.commons.gdx.utils.DrawUtil;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.TextureComponent;

public class TextureRenderer extends SubSystem {

    @SuppressWarnings("unchecked")
    public TextureRenderer() {
        super(Family.all(TextureComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        TextureComponent textureComponent = ComponentMappers.texture.get(entity);
        PositionComponent position = ComponentMappers.position.get(entity);

        Texture texture;
        float x, y, width, height;
        int srcX, srcY;

        if (textureComponent.region == null) {
            texture = textureComponent.texture;
            width = texture.getWidth();
            height = texture.getHeight();
            x = position.x;
            y = position.y;
            srcX = 0;
            srcY = 0;
        } else {
            texture = textureComponent.region.getTexture();
            width = textureComponent.region.getRegionWidth();
            height = textureComponent.region.getRegionHeight();
            x = position.x - width * 0.5f;
            y = position.y - height * 0.5f;
            srcX = textureComponent.region.getRegionX();
            srcY = textureComponent.region.getRegionY();
        }

        float originX = width * 0.5f;
        float originY = height * 0.5f;
        int srcWidth = (int) width;
        int srcHeight = (int) height;

        x += textureComponent.flipX ? -textureComponent.offsetX : textureComponent.offsetX;
        x += textureComponent.flipY ? -textureComponent.offsetY : textureComponent.offsetY;

        DrawUtil.batch.draw(texture, x, y, originX, originY, width, height, position.scaleX,
                position.scaleY, position.rotation, srcX, srcY, srcWidth, srcHeight,
                textureComponent.flipX, textureComponent.flipY);
    }
}
