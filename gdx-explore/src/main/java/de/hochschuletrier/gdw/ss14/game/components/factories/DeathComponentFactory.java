package de.hochschuletrier.gdw.ss14.game.components.factories;

import com.badlogic.ashley.core.Entity;

import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;
import de.hochschuletrier.gdw.ss14.game.components.DeathComponent;

public class DeathComponentFactory extends ComponentFactory<EntityFactoryParam> {

    @Override
    public String getType() {
        return "Death";
    }

    @Override
    public void run(Entity entity, SafeProperties meta, SafeProperties properties, EntityFactoryParam param) {
        DeathComponent component = engine.createComponent(DeathComponent.class);
        component.timer = properties.getFloat("time", 0.0f);
        entity.add(component);
    }
}
