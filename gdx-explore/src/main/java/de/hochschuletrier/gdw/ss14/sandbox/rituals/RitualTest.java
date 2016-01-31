package de.hochschuletrier.gdw.ss14.sandbox.rituals;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import box2dLight.RayHandler;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.hochschuletrier.gdw.commons.devcon.cvar.CVarBool;
import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.input.hotkey.Hotkey;
import de.hochschuletrier.gdw.commons.gdx.input.hotkey.HotkeyModifier;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixBodyDef;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixComponentAwareContactListener;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixFixtureDef;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixModifierComponent;
import de.hochschuletrier.gdw.commons.gdx.physix.systems.PhysixDebugRenderSystem;
import de.hochschuletrier.gdw.commons.gdx.physix.systems.PhysixSystem;
import de.hochschuletrier.gdw.ss14.Main;
import de.hochschuletrier.gdw.ss14.game.EntityBuilder;
import de.hochschuletrier.gdw.ss14.game.GameConstants;
import de.hochschuletrier.gdw.ss14.game.components.ImpactSoundComponent;
import de.hochschuletrier.gdw.ss14.game.components.RitualCasterComponent;
import de.hochschuletrier.gdw.ss14.game.components.TriggerComponent;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.ImpactSoundListener;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.TriggerListener;
import de.hochschuletrier.gdw.ss14.game.systems.RenderSystem;
import de.hochschuletrier.gdw.ss14.game.systems.RitualSystem;
import de.hochschuletrier.gdw.ss14.game.systems.RitualSystem.ResourceDescWithCount;
import de.hochschuletrier.gdw.ss14.game.systems.RitualSystem.RitualDesc;
import de.hochschuletrier.gdw.ss14.game.systems.UpdatePositionSystem;
import de.hochschuletrier.gdw.ss14.game.utils.PhysixUtil;
import de.hochschuletrier.gdw.ss14.sandbox.SandboxGame;

/**
 *
 * @author Santo Pfingsten
 */
public class RitualTest extends SandboxGame {

    private static final Logger logger = LoggerFactory
            .getLogger(RitualTest.class);

    private final CVarBool physixDebug = new CVarBool("physix_debug", true, 0,
            "Draw physix debug");
    private final Hotkey togglePhysixDebug = new Hotkey(
            () -> physixDebug.toggle(false), Input.Keys.F1, HotkeyModifier.CTRL);

    private final PooledEngine engine = new PooledEngine(
            GameConstants.ENTITY_POOL_INITIAL_SIZE,
            GameConstants.ENTITY_POOL_MAX_SIZE,
            GameConstants.COMPONENT_POOL_INITIAL_SIZE,
            GameConstants.COMPONENT_POOL_MAX_SIZE);

    private final EntityBuilder entityBuilder = new EntityBuilder(engine);

    private final PhysixSystem physixSystem = new PhysixSystem(
            GameConstants.BOX2D_SCALE, GameConstants.VELOCITY_ITERATIONS,
            GameConstants.POSITION_ITERATIONS, GameConstants.PRIORITY_PHYSIX);
    private final PhysixDebugRenderSystem physixDebugRenderSystem = new PhysixDebugRenderSystem(
            GameConstants.PRIORITY_DEBUG_WORLD);
    private final RenderSystem renderSystem = new RenderSystem(new RayHandler(physixSystem.getWorld()), GameConstants.PRIORITY_RENDER);
    private final UpdatePositionSystem updatePositionSystem = new UpdatePositionSystem(
            GameConstants.PRIORITY_PHYSIX + 1);
    private final RitualSystem ritualSystem = new RitualSystem(entityBuilder);

    public RitualTest() {
        // If this is a build jar file, disable hotkeys
        if (!Main.IS_RELEASE) {
            togglePhysixDebug.register();
        }
    }

    public void dispose() {
        togglePhysixDebug.unregister();
    }

    public void init(AssetManagerX assetManager) {
        Main.getInstance().console.register(physixDebug);
        physixDebug.addListener((CVar) -> physixDebugRenderSystem
                .setProcessing(physixDebug.get()));

        addSystems();
        addContactListeners();
        setupPhysixWorld();

        entityBuilder.init(assetManager);
    }

    private void addSystems() {
        engine.addSystem(physixSystem);
        engine.addSystem(physixDebugRenderSystem);
        engine.addSystem(renderSystem);
        engine.addSystem(updatePositionSystem);
        engine.addSystem(ritualSystem);
    }

    private void addContactListeners() {
        PhysixComponentAwareContactListener contactListener = new PhysixComponentAwareContactListener();
        physixSystem.getWorld().setContactListener(contactListener);
        contactListener.addListener(ImpactSoundComponent.class,
                new ImpactSoundListener());
        contactListener.addListener(TriggerComponent.class,
                new TriggerListener());
    }

    private void setupPhysixWorld() {
        physixSystem.setGravity(0, 24);
        PhysixBodyDef bodyDef = new PhysixBodyDef(BodyDef.BodyType.StaticBody,
                physixSystem).position(410, 500).fixedRotation(false);
        Body body = physixSystem.getWorld().createBody(bodyDef);
        body.createFixture(new PhysixFixtureDef(physixSystem).density(1)
                .friction(0.5f).shapeBox(800, 20));
        PhysixUtil.createHollowCircle(physixSystem, 180, 180, 150, 30, 6);

        createTrigger(410, 600, 3200, 40, (Entity entity) -> {
            engine.removeEntity(entity);
        });
    }

    public void update(float delta) {
        Main.getInstance().screenCamera.bind();
        engine.update(delta);
    }

    public void createTrigger(float x, float y, float width, float height,
            Consumer<Entity> consumer) {
        Entity entity = engine.createEntity();
        PhysixModifierComponent modifyComponent = engine
                .createComponent(PhysixModifierComponent.class);
        entity.add(modifyComponent);

        TriggerComponent triggerComponent = engine
                .createComponent(TriggerComponent.class);
        triggerComponent.consumer = consumer;
        entity.add(triggerComponent);

        modifyComponent.schedule(() -> {
            PhysixBodyComponent bodyComponent = engine
                    .createComponent(PhysixBodyComponent.class);
            PhysixBodyDef bodyDef = new PhysixBodyDef(BodyType.StaticBody,
                    physixSystem).position(x, y);
            bodyComponent.init(bodyDef, physixSystem, entity);
            PhysixFixtureDef fixtureDef = new PhysixFixtureDef(physixSystem)
                    .sensor(true).shapeBox(width, height);
            bodyComponent.createFixture(fixtureDef);
            entity.add(bodyComponent);
        });
        engine.addEntity(entity);
    }

    private Entity lastCreated = null;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            lastCreated = entityBuilder
                    .createEntity("player", screenX, screenY);
            lastCreated.getComponent(RitualCasterComponent.class).addRitual(
                    "fireball");
            lastCreated.getComponent(RitualCasterComponent.class).addRitual(
                    "test");

            lastCreated.getComponent(RitualCasterComponent.class).addResource(
                    "mana");
            lastCreated.getComponent(RitualCasterComponent.class).addResource(
                    "mana");

            lastCreated.getComponent(RitualCasterComponent.class).addResource(
                    "phosphorus");
        } else {
            entityBuilder.createEntity("box", screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (lastCreated != null) {
            StringBuilder out = new StringBuilder();

            out.append("Debug print:").append('\n');
            for (ResourceDescWithCount res : ritualSystem
                    .listResources(lastCreated)) {
                out.append(
                        "Res: " + res.count + " * " + res.desc.getId() + "; "
                        + res.desc.getName()).append('\n');
            }
            for (RitualDesc ritual : ritualSystem.listRituals(lastCreated)) {
                out.append(
                        "Ritual: " + ritual.getId() + "; " + ritual.getName()
                        + "; " + ritual.getDescription()).append('\n');
                out.append(
                        ritualSystem.isReady(lastCreated, ritual) ? "  - ready"
                        : "  - not ready").append('\n');
            }
            logger.info(out.toString());
            ritualSystem.castRitual(lastCreated, "fireball");
        }

        return true;
    }

    public InputProcessor getInputProcessor() {
        return this;
    }
}
