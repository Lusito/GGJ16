package de.hochschuletrier.gdw.ss14.game.systems.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;

import de.hochschuletrier.gdw.commons.gdx.ashley.SortedSubIteratingSystem.SubSystem;
import de.hochschuletrier.gdw.commons.gdx.utils.DrawUtil;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.ParticleComponent;

public class ParticleRenderer extends SubSystem {

    @SuppressWarnings("unchecked")
    public ParticleRenderer() {
        super(Family.all(ParticleComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = ComponentMappers.position.get(entity);
        ParticleComponent particle = ComponentMappers.particle.get(entity);

        if (particle.loop) {
            for (ParticleEmitter pe : particle.particleEffect.getEmitters()) {
                pe.durationTimer = 0;
            }
        }
        particle.particleEffect.setPosition(position.x + particle.offsetX, position.y + particle.offsetY);
        particle.particleEffect.update(deltaTime);
        particle.particleEffect.draw(DrawUtil.batch);
    }

}
