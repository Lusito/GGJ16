package de.hochschuletrier.gdw.ss14.game;

import com.badlogic.ashley.core.ComponentMapper;

import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixModifierComponent;
import de.hochschuletrier.gdw.ss14.game.components.ImpactSoundComponent;
import de.hochschuletrier.gdw.ss14.game.components.InputComponent;
import de.hochschuletrier.gdw.ss14.game.components.PickableComponent;
import de.hochschuletrier.gdw.ss14.game.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
import de.hochschuletrier.gdw.ss14.game.components.RitualCasterComponent;
import de.hochschuletrier.gdw.ss14.game.components.TriggerComponent;
import de.hochschuletrier.gdw.ss14.game.components.light.ChainLightComponent;
import de.hochschuletrier.gdw.ss14.game.components.light.ConeLightComponent;
import de.hochschuletrier.gdw.ss14.game.components.light.PointLightComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationStateComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.ParticleComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.TextureComponent;

public class ComponentMappers {

    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<TriggerComponent> trigger = ComponentMapper.getFor(TriggerComponent.class);
    public static final ComponentMapper<PhysixBodyComponent> physixBody = ComponentMapper.getFor(PhysixBodyComponent.class);
    public static final ComponentMapper<PhysixModifierComponent> physixModifier = ComponentMapper.getFor(PhysixModifierComponent.class);
    public static final ComponentMapper<ImpactSoundComponent> impactSound = ComponentMapper.getFor(ImpactSoundComponent.class);
    public static final ComponentMapper<AnimationComponent> animation = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<TextureComponent> texture = ComponentMapper.getFor(TextureComponent.class);
    public static final ComponentMapper<PointLightComponent> pointLight = ComponentMapper.getFor(PointLightComponent.class);
    public static final ComponentMapper<ChainLightComponent> chainLight = ComponentMapper.getFor(ChainLightComponent.class);
    public static final ComponentMapper<ConeLightComponent> coneLight = ComponentMapper.getFor(ConeLightComponent.class);
    public static final ComponentMapper<ParticleComponent> particle = ComponentMapper.getFor(ParticleComponent.class);
    public static final ComponentMapper<RitualCasterComponent> ritualCaster = ComponentMapper.getFor(RitualCasterComponent.class);
    public static final ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<PickableComponent> pickable = ComponentMapper.getFor(PickableComponent.class);
    public static final ComponentMapper<InputComponent> input = ComponentMapper.getFor(InputComponent.class);
    public static final ComponentMapper<AnimationStateComponent> animState = ComponentMapper.getFor(AnimationStateComponent.class);
}
