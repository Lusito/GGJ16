﻿package de.hochschuletrier.gdw.ss14.game;

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
import de.hochschuletrier.gdw.commons.tiled.Layer;
import de.hochschuletrier.gdw.commons.tiled.LayerObject;
import de.hochschuletrier.gdw.commons.tiled.TiledMap;
import de.hochschuletrier.gdw.ss14.Main;
import de.hochschuletrier.gdw.ss14.game.components.ImpactSoundComponent;
import de.hochschuletrier.gdw.ss14.game.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.game.components.TriggerComponent;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.ImpactSoundListener;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.PickUpListener;
import de.hochschuletrier.gdw.ss14.game.contactlisteners.TriggerListener;
import de.hochschuletrier.gdw.ss14.game.systems.CameraSystem;
import de.hochschuletrier.gdw.ss14.game.systems.RenderSystem;
import de.hochschuletrier.gdw.ss14.game.systems.BasemapRenderSystem;
import de.hochschuletrier.gdw.ss14.game.systems.UpdatePositionSystem;
import de.hochschuletrier.gdw.ss14.game.systems.InputSystem;


public class Game extends InputAdapter {

    private final CVarBool physixDebug = new CVarBool("physix_debug", true, 0, "Draw physix debug");
    private final Hotkey togglePhysixDebug = new Hotkey(() -> physixDebug.toggle(false), Input.Keys.F1, HotkeyModifier.CTRL);

    private final PooledEngine engine = new PooledEngine(
            GameConstants.ENTITY_POOL_INITIAL_SIZE, GameConstants.ENTITY_POOL_MAX_SIZE,
            GameConstants.COMPONENT_POOL_INITIAL_SIZE, GameConstants.COMPONENT_POOL_MAX_SIZE
    );

    private final EntityBuilder entityBuilder = new EntityBuilder(engine);

    private final PhysixSystem physixSystem = new PhysixSystem(GameConstants.BOX2D_SCALE,
            GameConstants.VELOCITY_ITERATIONS, GameConstants.POSITION_ITERATIONS, GameConstants.PRIORITY_PHYSIX
    );
    private final PhysixDebugRenderSystem physixDebugRenderSystem = new PhysixDebugRenderSystem(GameConstants.PRIORITY_DEBUG_WORLD);
    
    private final RenderSystem renderSystem = new RenderSystem(new RayHandler(physixSystem.getWorld()), GameConstants.PRIORITY_RENDER);
    private final UpdatePositionSystem updatePositionSystem = new UpdatePositionSystem(GameConstants.PRIORITY_PHYSIX + 1);
    private final InputSystem inputSystem = new InputSystem();  
    private final CameraSystem cameraSystem = new CameraSystem(0);
    
    private Entity Player;

    private final BasemapRenderSystem basemapRenderSystem = new BasemapRenderSystem();
    
    public Game() {
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
        physixDebug.addListener((CVar) -> physixDebugRenderSystem.setProcessing(physixDebug.get()));
        addSystems();
        addContactListeners();
        entityBuilder.init(assetManager);
        setupPhysixWorld();
        
        TiledMap map = loadMap("data/maps/tryanewone.tmx");
        basemapRenderSystem.initMap(map);
        cameraSystem.adjustToMap(map);
    }

    private void addSystems() {
        engine.addSystem(physixSystem);
        engine.addSystem(physixDebugRenderSystem);
        engine.addSystem(renderSystem);
        engine.addSystem(updatePositionSystem);
        engine.addSystem(inputSystem);
        engine.addSystem(basemapRenderSystem);
        engine.addSystem(cameraSystem);
    }

    private TiledMap loadMap(String filename) {
        try {
            TiledMap map = new TiledMap(filename, LayerObject.PolyMode.ABSOLUTE);
            for (Layer layer : map.getLayers()) {
                if (layer.isObjectLayer()) {
                    for (LayerObject obj : layer.getObjects()) {
                        String type = obj.getProperty("EntityType", null);
                        if (type != null) {
                            entityBuilder.createEntity(type, obj.getX() + obj.getWidth() / 2.0f,
                                    obj.getY() - obj.getHeight() / 2.0f);
                        }
                    }
                }
            }
            return map;
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Map konnte nicht geladen werden: " + filename);
        }
    }

    private void addContactListeners() {
        PhysixComponentAwareContactListener contactListener = new PhysixComponentAwareContactListener();
        physixSystem.getWorld().setContactListener(contactListener);
        contactListener.addListener(ImpactSoundComponent.class, new ImpactSoundListener());
        contactListener.addListener(TriggerComponent.class, new TriggerListener());
        contactListener.addListener(PlayerComponent.class, new PickUpListener());
    }

    private void setupPhysixWorld() {
        physixSystem.setGravity(0, 0);
        
        this.Player = entityBuilder.createEntity("ball", 50, 50);
        
      /*  PhysixBodyDef bodyDef = new PhysixBodyDef(BodyDef.BodyType.StaticBody, physixSystem).position(410, 500).fixedRotation(false);
        Body body = physixSystem.getWorld().createBody(bodyDef);
        body.createFixture(new PhysixFixtureDef(physixSystem).density(1).friction(0.5f).shapeBox(800, 20));
        PhysixUtil.createHollowCircle(physixSystem, 180, 180, 150, 30, 6);

        createTrigger(410, 600, 3200, 40, (Entity entity) -> {
            engine.removeEntity(entity);
        });*/
    }

    public void update(float delta) {
        Main.getInstance().screenCamera.bind();
        engine.update(delta);
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
    
    
/*
    @Override
	public boolean keyDown(int keycode) {
    	final int speed = 50;
    	if (this.Player != null){
    		PhysixBodyComponent body = Player.getComponent(PhysixBodyComponent.class);
    		switch(keycode){
	    	case Input.Keys.LEFT:
	    		body.setLinearVelocityX(-speed);
	    		break;
	    	case Input.Keys.RIGHT:
	    		body.setLinearVelocityX(speed);
	    		break;
	    	case Input.Keys.DOWN:
	    		body.setLinearVelocityY(speed);
	    		break;
	    	case Input.Keys.UP:
	    		body.setLinearVelocityY(-speed);
	    		break;
	    	case Input.Keys.SPACE:
	    		
	    		break;
	    	}
    	}
		return super.keyDown(keycode);
	}
    
    

	@Override
	public boolean keyUp(int keycode) {
		if (this.Player != null){
			PhysixBodyComponent body = Player.getComponent(PhysixBodyComponent.class);
			body.setLinearVelocity(new Vector2());
		}
		return super.keyUp(keycode);
	}*/
	@Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == 0)
        	this.Player = entityBuilder.createEntity("ball", screenX, screenY);
        else
        	entityBuilder.createEntity("box", screenX, screenY);
        return true;
    }

    public InputProcessor getInputProcessor() {
        return this;
    }
}