package de.hochschuletrier.gdw.ss14.game.components.factories;

import com.badlogic.ashley.core.Entity;

import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;
import de.hochschuletrier.gdw.ss14.game.components.BrokenBridgeComponent;

public class BrokenBridgeComponentFactory extends ComponentFactory<EntityFactoryParam> {

    @Override
    public String getType() {
        return "BrokenBridge";
    }

    @Override
    public void run(Entity entity, SafeProperties meta, SafeProperties properties, EntityFactoryParam param) {
        BrokenBridgeComponent component = engine.createComponent(BrokenBridgeComponent.class);
        component.vertical = properties.getBoolean("vertical");
        component.repairRadius = properties.getFloat("repairRadius", 50.f);
        entity.add(component);
    }
}
