package de.hochschuletrier.gdw.ss14.game.components.factories;

import com.badlogic.ashley.core.Entity;

import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;
import de.hochschuletrier.gdw.ss14.game.components.light.PointLightComponent;

public class PointLightComponentFactory extends ComponentFactory<EntityFactoryParam> {

	@Override
	public String getType() {
		return "PointLight";
	}

	@Override
	public void run(Entity entity, SafeProperties meta,
			SafeProperties properties, EntityFactoryParam param) {
        PointLightComponent component = engine.createComponent(PointLightComponent.class);
        component.offsetX = properties.getFloat("offsetX", 0.0f);
        component.offsetY = properties.getFloat("offsetY", 0.0f);
        entity.add(component);
	}
	
}
