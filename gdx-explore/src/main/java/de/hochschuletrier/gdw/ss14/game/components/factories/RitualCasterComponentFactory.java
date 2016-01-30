package de.hochschuletrier.gdw.ss14.game.components.factories;

import com.badlogic.ashley.core.Entity;

import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;
import de.hochschuletrier.gdw.ss14.game.components.RitualCasterComponent;

public class RitualCasterComponentFactory extends ComponentFactory<EntityFactoryParam> {

    @Override
    public String getType() {
        return "RitualCaster";
    }

    @Override
    public void run(Entity entity, SafeProperties meta, SafeProperties properties, EntityFactoryParam param) {
        RitualCasterComponent component = engine.createComponent(RitualCasterComponent.class);
        entity.add(component);
    }
}
