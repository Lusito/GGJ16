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
import de.hochschuletrier.gdw.ss14.events.ExplosionEvent;
import de.hochschuletrier.gdw.ss14.events.GameWonEvent;
import de.hochschuletrier.gdw.ss14.events.InputActionEvent;
import de.hochschuletrier.gdw.ss14.events.PickUpEvent;
import de.hochschuletrier.gdw.ss14.events.PlayerMessageEvent;
import de.hochschuletrier.gdw.ss14.events.ReactionEvent;
import de.hochschuletrier.gdw.ss14.events.RitualCastedEvent;
import de.hochschuletrier.gdw.ss14.events.TeleportEvent;
import de.hochschuletrier.gdw.ss14.game.components.MaterialComponent;
import de.hochschuletrier.gdw.ss14.game.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
import de.hochschuletrier.gdw.ss14.game.components.TeleportInComponent;
import de.hochschuletrier.gdw.ss14.game.components.TriggerComponent;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.PickUpListener;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.ReactionListener;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.TeleportListener;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.TriggerListener;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.WaterListener;
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
import de.hochschuletrier.gdw.ss14.game.systems.renderers.LightRenderer;

public class Game extends InputAdapter {

    private final CVarBool physixDebug = new CVarBool("physix_debug", false, 0, "Draw physix debug");
    private final Hotkey togglePhysixDebug = new Hotkey(() -> physixDebug.toggle(false), Input.Keys.F1, HotkeyModifier.CTRL);

    public static PooledEngine engine;

    public static EntityBuilder entityBuilder;

    private PhysixSystem physixSystem;
    private PhysixDebugRenderSystem physixDebugRenderSystem;

    private RenderSystem renderSystem;
    private UpdatePositionSystem updatePositionSystem;
    private InputSystem inputSystem;
    private CameraSystem cameraSystem;

    private AnimationStateSystem animStateSystem;
    private SoundSystem soundSystem;
    private RitualSystem ritualSystem;
    private DeathSystem deathSystem;
    private ReactionSystem reactionSystem;
    private TeleportSystem teleportSystem;
    
    private Entity player;
    
    private Hud hud;

    private BasemapRenderSystem basemapRenderSystem;

    public Game() {
        // If this is a build jar file, disable hotkeys
        if (!Main.IS_RELEASE) {
            togglePhysixDebug.register();
        }
    }

    public void dispose() {
        togglePhysixDebug.unregister();
        hud.dispose();
        
        ExplosionEvent.unregisterAll();
        GameWonEvent.unregisterAll();
        InputActionEvent.unregisterAll();
        PickUpEvent.unregisterAll();
        PlayerMessageEvent.unregisterAll();
        ReactionEvent.unregisterAll();
        RitualCastedEvent.unregisterAll();
        TeleportEvent.unregisterAll();
        
        engine.removeSystem(physixSystem);
        engine.removeSystem(physixDebugRenderSystem);
        engine.removeSystem(renderSystem);
        engine.removeSystem(updatePositionSystem);
        engine.removeSystem(inputSystem);
        engine.removeSystem(basemapRenderSystem);
        engine.removeSystem(cameraSystem);
        engine.removeSystem(animStateSystem);
        engine.removeSystem(ritualSystem);
        engine.removeSystem(soundSystem);
        engine.removeSystem(reactionSystem);
        engine.removeSystem(teleportSystem);
        engine.removeSystem(deathSystem);
        
        engine.removeAllEntities();
    }

    public void init(AssetManagerX assetManager) {
        if(physixDebug != null)
            Main.getInstance().console.unregister(physixDebug);
        if(LightRenderer.rayHandler != null)
            LightRenderer.rayHandler.removeAll();
        
        engine = new PooledEngine(
                GameConstants.ENTITY_POOL_INITIAL_SIZE, GameConstants.ENTITY_POOL_MAX_SIZE,
                GameConstants.COMPONENT_POOL_INITIAL_SIZE, GameConstants.COMPONENT_POOL_MAX_SIZE
        );
        
        entityBuilder = new EntityBuilder(engine);
        
        createSystems();
        
        Main.getInstance().console.register(physixDebug);
        physixDebug.addListener((CVar) -> physixDebugRenderSystem.setProcessing(physixDebug.get()));
        physixDebugRenderSystem.setProcessing(physixDebug.get());
        addSystems();
        addContactListeners();
        entityBuilder.init(assetManager);
        setupPhysixWorld();

        TiledMap map = loadMap("data/maps/bigworld_grassd.tmx");
        basemapRenderSystem.initMap(map);
        cameraSystem.adjustToMap(map);
        entityBuilder.createEntitiesFromMap(map, physixSystem);
        
        hud = new Hud(assetManager, ritualSystem, player);
        
        TeleportEvent.emit(null, player);
        PlayerMessageEvent.emit("!");
    }
    
    private void createSystems() {
        physixSystem = new PhysixSystem(GameConstants.BOX2D_SCALE,
                GameConstants.VELOCITY_ITERATIONS, GameConstants.POSITION_ITERATIONS, GameConstants.PRIORITY_PHYSIX);
        physixDebugRenderSystem = new PhysixDebugRenderSystem(GameConstants.PRIORITY_DEBUG_WORLD);
        renderSystem = new RenderSystem(new RayHandler(physixSystem.getWorld()), GameConstants.PRIORITY_RENDER);
        updatePositionSystem = new UpdatePositionSystem(GameConstants.PRIORITY_PHYSIX + 1);
        inputSystem = new InputSystem();
        cameraSystem = new CameraSystem(0);
        animStateSystem = new AnimationStateSystem(GameConstants.PRIORITY_ANIMATION_STATE);
        soundSystem = new SoundSystem(GameConstants.PRIORITY_SOUND);
        deathSystem = new DeathSystem(0);
        reactionSystem = new ReactionSystem();
        teleportSystem = new TeleportSystem(0);
        ritualSystem = new RitualSystem(entityBuilder);
        basemapRenderSystem = new BasemapRenderSystem(GameConstants.PRIORITY_TILE_RENDERER);
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
        engine.addSystem(reactionSystem);
        engine.addSystem(teleportSystem);
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
//        contactListener.addListener(TriggerComponent.class, new TriggerListener());
        contactListener.addListener(PlayerComponent.class, new PickUpListener());
        contactListener.addListener(PlayerComponent.class, new WaterListener());
        contactListener.addListener(TeleportInComponent.class, new TeleportListener());
    }

    private void setupPhysixWorld() {
        physixSystem.setGravity(0, 0);

        player = entityBuilder.createEntity("player", 50, 50);
        
        // Quick hack for additional player light
        PositionComponent playerPos = ComponentMappers.position.get(player);
        Entity playerLight = entityBuilder.createEntity("playerLight", 0, 0);
        playerLight.remove(PositionComponent.class);
        playerLight.add(playerPos);
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
