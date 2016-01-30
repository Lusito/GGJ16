package de.hochschuletrier.gdw.ss14.game.components.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;
import de.hochschuletrier.gdw.ss14.game.components.MaterialComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.ParticleComponent;
import de.hochschuletrier.gdw.ss14.game.gamelogic.MaterialType;

public class ParticleComponentFactory extends ComponentFactory<EntityFactoryParam> {

    @Override
    public String getType() {
        return "ParticleEffect";
    }

    @Override
    public void run(Entity entity, SafeProperties meta, SafeProperties properties, EntityFactoryParam param) {
        ParticleComponent component = engine.createComponent(ParticleComponent.class);
        component.loop = properties.getBoolean("loop", true);
        component.offsetX = properties.getFloat("offsetX", 0.0f);
        component.offsetY = properties.getFloat("offsetY", 0.0f);
        component.particleEffect = new ParticleEffect(assetManager.getParticleEffect(properties.getString("effect")));
        component.particleEffect.start();
        assert(component.particleEffect != null);
        entity.add(component);
    }
}
