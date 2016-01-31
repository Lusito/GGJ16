package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import de.hochschuletrier.gdw.commons.devcon.DevConsole;
import de.hochschuletrier.gdw.commons.devcon.cvar.CVarEnum;
import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.audio.SoundDistanceModel;
import de.hochschuletrier.gdw.commons.gdx.audio.SoundEmitter;
import de.hochschuletrier.gdw.commons.gdx.audio.SoundInstance;
import de.hochschuletrier.gdw.ss14.Main;
import de.hochschuletrier.gdw.ss14.events.PickUpEvent;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
import de.hochschuletrier.gdw.ss14.events.ExplosionEvent;
import de.hochschuletrier.gdw.ss14.events.RitualCastedEvent;
import de.hochschuletrier.gdw.ss14.game.GameConstants;
import de.hochschuletrier.gdw.ss14.game.utils.WaterRectangle;

public class SoundSystem extends IteratingSystem implements PickUpEvent.Listener, RitualCastedEvent.Listener, ExplosionEvent.Listener {
    private static final CVarEnum<SoundDistanceModel> distanceModel = new CVarEnum("snd_distanceModel", SoundDistanceModel.INVERSE, SoundDistanceModel.class, 0, "sound distance model");
    private static final CVarEnum<SoundEmitter.Mode> emitterMode = new CVarEnum("snd_mode", SoundEmitter.Mode.STEREO, SoundEmitter.Mode.class, 0, "sound mode");
    private AssetManagerX assetManager;
    private final Vector2 dummyPos = new Vector2();
    private SoundInstance waterSound;
    private SoundInstance natureSound;

    @SuppressWarnings("unchecked")
    public SoundSystem(int priority) {
        super(Family.all(PlayerComponent.class, PositionComponent.class).get());

        SoundEmitter.setWorldScale(1.f);
        assetManager = Main.getInstance().getAssetManager();
        PickUpEvent.register(this);
        RitualCastedEvent.register(this);
        ExplosionEvent.register(this);
    }
    
    public static void initCVars() {
        DevConsole console = Main.getInstance().console;
        console.register(distanceModel);
        distanceModel.addListener((CVar) -> distanceModel.get().activate());

        console.register(emitterMode);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        
        waterSound = SoundEmitter.playGlobal(assetManager.getSound("ambient_water"), true);
        waterSound.setVolume(0);
        natureSound = SoundEmitter.playGlobal(assetManager.getSound("ambient_nature"), true);
        natureSound.setVolume(0);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        waterSound.stop();
        natureSound.stop();
    }

    private SoundInstance playSound(Entity entity, Sound sound, float volume, float referenceDistance) {
        assert(sound != null);
        PositionComponent posComponent = ComponentMappers.position.get(entity);
        SoundInstance soundInstance = null;
        
        if(posComponent != null)
            soundInstance = SoundEmitter.playGlobal(sound, false, posComponent.x, posComponent.y, 0);
        else
            soundInstance = SoundEmitter.playGlobal(sound, false);
        
        if(soundInstance != null) {
        	soundInstance.setVolume(volume);
        	soundInstance.setReferenceDistance(referenceDistance);
        }
        
        return soundInstance;
    }
    
    private SoundInstance playSound(Entity entity, Sound sound) {
        return playSound(entity, sound, 3.f, 50.f);
    }
    
    @SuppressWarnings("unused")
    private SoundInstance playSound(Entity entity, Sound sound, float volume) {
        return playSound(entity, sound, volume, 50.f);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pos = ComponentMappers.position.get(entity);
        PlayerComponent player = ComponentMappers.player.get(entity);
        
        SoundEmitter.setListenerPosition(pos.x, pos.y, 0, emitterMode.get());
        
        updateWaterVolume(pos, player);
    }

    private void updateWaterVolume(PositionComponent pos, PlayerComponent player) {
        dummyPos.set(pos.x, pos.y);
        
        boolean first = true;
        float minDist = -1;
        for (WaterRectangle waterRect : player.waterRects) {
            float distance = waterRect.distanceTo(dummyPos);
            if(first || distance < minDist) {
                minDist = distance;
                first = false;
            }
        }
        float waterVolume = 0;
        final float max = GameConstants.WATER_LISTEN_MAX_DISTANCE;
        final float min = GameConstants.WATER_LISTEN_MIN_DISTANCE;
        if(!first && minDist <= max)
            waterVolume = 1 - Math.max(0, minDist - min) / (max - min);
        waterSound.setVolume(GameConstants.AMBIENT_VOLUME * waterVolume);
        natureSound.setVolume(GameConstants.AMBIENT_VOLUME * (1 - waterVolume));
    }

    @Override
    public void onPickupEvent(Entity entityWhoPickup, Entity whatsPickedUp) {
        SoundInstance si = playSound(entityWhoPickup, assetManager.getSound("pickup"));
        if(si != null)
            si.setVolume(0.1f);
    }
    @Override
    public void onRitualCastedEvent(Entity mage) {
        SoundInstance si = playSound(mage, assetManager.getSound("ritual_cast"));
        if(si != null)
            si.setVolume(0.3f);
    }

    @Override
    public void onExplosionEvent(float x, float y) {
        SoundEmitter.playGlobal(assetManager.getSound("fireball/"), false, x, y, 0);
    }
}
