package de.hochschuletrier.gdw.ss14.game.components.factories;

import com.badlogic.ashley.core.Entity;

import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;
import de.hochschuletrier.gdw.ss14.game.components.SelfDestructComponent;

public class SelfDestructComponentFactory extends ComponentFactory<EntityFactoryParam> {

    @Override
    public String getType() {
        return "SelfDestruct";
    }

    @Override
    public void run(Entity entity, SafeProperties meta, SafeProperties properties, EntityFactoryParam param) {
        SelfDestructComponent component = engine.createComponent(SelfDestructComponent.class);
        component.seconds = properties.getFloat("seconds", 0);
        
        entity.add(component);
    }
}
