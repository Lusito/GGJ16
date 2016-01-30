package de.hochschuletrier.gdw.ss14.game;

import box2dLight.RayHandler;

import java.util.function.Consumer;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
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
import de.hochschuletrier.gdw.commons.tiled.LayerObject;
import de.hochschuletrier.gdw.commons.tiled.TiledMap;
import de.hochschuletrier.gdw.ss14.Main;
import de.hochschuletrier.gdw.ss14.events.TeleportEvent;
import de.hochschuletrier.gdw.ss14.game.components.MaterialComponent;
import de.hochschuletrier.gdw.ss14.game.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.game.components.TeleportInComponent;
import de.hochschuletrier.gdw.ss14.game.components.TriggerComponent;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.PickUpListener;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.ReactionListener;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.TeleportListener;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.TriggerListener;
import de.hochschuletrier.gdw.ss14.game.systems.AnimationStateSystem;
import de.hochschuletrier.gdw.ss14.game.systems.CameraSystem;
import de.hochschuletrier.gdw.ss14.game.systems.DeathSystem;
import de.hochschuletrier.gdw.ss14.game.systems.ReactionSystem;
import de.hochschuletrier.gdw.ss14.game.systems.RenderSystem;
import de.hochschuletrier.gdw.ss14.game.systems.BasemapRenderSystem;
import de.hochschuletrier.gdw.ss14.game.systems.RitualSystem;
import de.hochschuletrier.gdw.ss14.game.systems.SoundSystem;
import de.hochschuletrier.gdw.ss14.game.systems.UpdatePositionSystem;
import de.hochschuletrier.gdw.ss14.game.systems.InputSystem;
import de.hochschuletrier.gdw.ss14.game.systems.TeleportSystem;

public class Game extends InputAdapter {

    private final CVarBool physixDebug = new CVarBool("physix_debug", true, 0, "Draw physix debug");
    private final Hotkey togglePhysixDebug = new Hotkey(() -> physixDebug.toggle(false), Input.Keys.F1, HotkeyModifier.CTRL);

    public static final PooledEngine engine = new PooledEngine(
            GameConstants.ENTITY_POOL_INITIAL_SIZE, GameConstants.ENTITY_POOL_MAX_SIZE,
            GameConstants.COMPONENT_POOL_INITIAL_SIZE, GameConstants.COMPONENT_POOL_MAX_SIZE
    );

    public static final EntityBuilder entityBuilder = new EntityBuilder(engine);

    private final PhysixSystem physixSystem = new PhysixSystem(GameConstants.BOX2D_SCALE,
            GameConstants.VELOCITY_ITERATIONS, GameConstants.POSITION_ITERATIONS, GameConstants.PRIORITY_PHYSIX
    );
    private final PhysixDebugRenderSystem physixDebugRenderSystem = new PhysixDebugRenderSystem(GameConstants.PRIORITY_DEBUG_WORLD);

    private final RenderSystem renderSystem = new RenderSystem(new RayHandler(physixSystem.getWorld()), GameConstants.PRIORITY_RENDER);
    private final UpdatePositionSystem updatePositionSystem = new UpdatePositionSystem(GameConstants.PRIORITY_PHYSIX + 1);
    private final InputSystem inputSystem = new InputSystem();
    private final CameraSystem cameraSystem = new CameraSystem(0);

    private final AnimationStateSystem animStateSystem = new AnimationStateSystem(GameConstants.PRIORITY_ANIMATION_STATE);
    private final SoundSystem soundSystem = new SoundSystem(GameConstants.PRIORITY_SOUND);
    private final RitualSystem ritualSystem = new RitualSystem(entityBuilder);
    private final DeathSystem deathSystem = new DeathSystem(0);

    private Entity player;
    
    private Hud hud;

    private final BasemapRenderSystem basemapRenderSystem = new BasemapRenderSystem(GameConstants.PRIORITY_TILE_RENDERER);

    public Game() {
        // If this is a build jar file, disable hotkeys
        if (!Main.IS_RELEASE) {
            togglePhysixDebug.register();
        }
    }

    public void dispose() {
        togglePhysixDebug.unregister();
        hud.dispose();
    }

    public void init(AssetManagerX assetManager) {
        Main.getInstance().console.register(physixDebug);
        physixDebug.addListener((CVar) -> physixDebugRenderSystem.setProcessing(physixDebug.get()));
        addSystems();
        addContactListeners();
        entityBuilder.init(assetManager);
        setupPhysixWorld();

        TiledMap map = loadMap("data/maps/newworld.tmx");
        basemapRenderSystem.initMap(map);
        cameraSystem.adjustToMap(map);
        entityBuilder.createEntitiesFromMap(map, physixSystem);
        
        hud = new Hud(assetManager, ritualSystem, player);
        
        TeleportEvent.emit(null, player);
    }

    private void addSystems() {
        engine.addSystem(physixSystem);
        engine.addSystem(physixDebugRenderSystem);
        engine.addSystem(renderSystem);
        engine.addSystem(updatePositionSystem);
        engine.addSystem(inputSystem);
        engine.addSystem(basemapRenderSystem);
        engine.addSystem(cameraSystem);
        engine.addSystem(animStateSystem);
        engine.addSystem(ritualSystem);
        engine.addSystem(soundSystem);
        engine.addSystem(new ReactionSystem());
        engine.addSystem(new TeleportSystem(0));
        engine.addSystem(deathSystem);
    }

    private TiledMap loadMap(String filename) {
        try {
            return new TiledMap(filename, LayerObject.PolyMode.ABSOLUTE);
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Map konnte nicht geladen werden: " + filename);
        }
    }

    private void addContactListeners() {
        PhysixComponentAwareContactListener contactListener = new PhysixComponentAwareContactListener();
        physixSystem.getWorld().setContactListener(contactListener);
//        contactListener.addListener(ImpactSoundComponent.class, new ImpactSoundListener());
        contactListener.addListener(MaterialComponent.class, new ReactionListener());
        contactListener.addListener(TriggerComponent.class, new TriggerListener());
        contactListener.addListener(PlayerComponent.class, new PickUpListener());
        contactListener.addListener(TeleportInComponent.class, new TeleportListener());
    }

    private void setupPhysixWorld() {
        physixSystem.setGravity(0, 0);

        player = entityBuilder.createEntity("player", 50, 50);
        entityBuilder.createEntity("ice", 500, 100);
    }

    public void update(float delta) {
        Main.getInstance().screenCamera.bind();
        engine.update(delta);
        hud.update(delta);
        hud.render();
    }

    public void createTrigger(float x, float y, float width, float height, Consumer<Entity> consumer) {
        Entity entity = engine.createEntity();
        PhysixModifierComponent modifyComponent = engine.createComponent(PhysixModifierComponent.class);
        entity.add(modifyComponent);

        TriggerComponent triggerComponent = engine.createComponent(TriggerComponent.class);
        triggerComponent.consumer = consumer;
        entity.add(triggerComponent);

        modifyComponent.schedule(() -> {
            PhysixBodyComponent bodyComponent = engine.createComponent(PhysixBodyComponent.class);
            PhysixBodyDef bodyDef = new PhysixBodyDef(BodyType.StaticBody, physixSystem).position(x, y);
            bodyComponent.init(bodyDef, physixSystem, entity);
            PhysixFixtureDef fixtureDef = new PhysixFixtureDef(physixSystem).sensor(true).shapeBox(width, height);
            bodyComponent.createFixture(fixtureDef);
            entity.add(bodyComponent);
        });
        engine.addEntity(entity);
    }

    public InputProcessor getInputProcessor() {
        return this;
    }
}
