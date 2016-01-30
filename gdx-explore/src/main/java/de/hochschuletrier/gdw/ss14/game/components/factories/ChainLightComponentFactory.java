package de.hochschuletrier.gdw.ss14.game.components.factories;

import com.badlogic.ashley.core.Entity;

import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;
import de.hochschuletrier.gdw.ss14.game.components.light.ChainLightComponent;

public class ChainLightComponentFactory extends ComponentFactory<EntityFactoryParam> {

	@Override
	public String getType() {
		return "PointLight";
	}

	@Override
	public void run(Entity entity, SafeProperties meta,
			SafeProperties properties, EntityFactoryParam param) {
        ChainLightComponent component = engine.createComponent(ChainLightComponent.class);
        component.offsetX = properties.getFloat("offsetX", 0.0f);
        component.offsetY = properties.getFloat("offsetY", 0.0f);
        entity.add(component);
	}
}
