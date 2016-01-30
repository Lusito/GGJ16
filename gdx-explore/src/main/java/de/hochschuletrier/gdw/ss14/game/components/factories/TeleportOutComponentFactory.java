package de.hochschuletrier.gdw.ss14.game.components.factories;

import com.badlogic.ashley.core.Entity;

import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;
import de.hochschuletrier.gdw.ss14.game.components.TeleportOutComponent;

public class TeleportOutComponentFactory extends ComponentFactory<EntityFactoryParam> {

    @Override
    public String getType() {
        return "TeleportOut";
    }

    @Override
    public void run(Entity entity, SafeProperties meta, SafeProperties properties, EntityFactoryParam param) {
        entity.add(engine.createComponent(TeleportOutComponent.class));
    }
}
