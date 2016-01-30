package de.hochschuletrier.gdw.ss14.game.components.factories;

import com.badlogic.ashley.core.Entity;

import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationComponent;

public class AnimationComponentFactory extends ComponentFactory<EntityFactoryParam> {

    @Override
    public String getType() {
        return "Animation";
    }

    @Override
    public void run(Entity entity, SafeProperties meta, SafeProperties properties, EntityFactoryParam param) {
        AnimationComponent component = engine.createComponent(AnimationComponent.class);
        component.animation = assetManager.getAnimation(properties.getString("animation"));
        component.flipX = properties.getBoolean("flipX", false);
        component.flipY = properties.getBoolean("flipY", true);
        component.offsetX = properties.getFloat("offsetX", 0.0f);
        component.offsetY = properties.getFloat("offsetY", 0.0f);
        assert (component.animation != null);
        entity.add(component);
    }
}