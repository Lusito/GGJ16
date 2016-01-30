package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;

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

public class SoundSystem extends IteratingSystem implements PickUpEvent.Listener {
    private static final CVarEnum<SoundDistanceModel> distanceModel = new CVarEnum("snd_distanceModel", SoundDistanceModel.INVERSE, SoundDistanceModel.class, 0, "sound distance model");
    private static final CVarEnum<SoundEmitter.Mode> emitterMode = new CVarEnum("snd_mode", SoundEmitter.Mode.STEREO, SoundEmitter.Mode.class, 0, "sound mode");
    private AssetManagerX assetManager;

    @SuppressWarnings("unchecked")
    public SoundSystem(int priority) {
        super(Family.all(PlayerComponent.class, PositionComponent.class).get());

        SoundEmitter.setWorldScale(1.f);
        assetManager = Main.getInstance().getAssetManager();
        PickUpEvent.register(this);
    }
    
    public static void initCVars() {
        DevConsole console = Main.getInstance().console;
        console.register(distanceModel);
        distanceModel.addListener((CVar) -> distanceModel.get().activate());

        console.register(emitterMode);
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    
        SoundEmitter.updateGlobal();
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
        PositionComponent positionComponent = ComponentMappers.position.get(entity);
        
        SoundEmitter.setListenerPosition(positionComponent.x, positionComponent.y, 0, emitterMode.get());
    }

    @Override
    public void onPickupEvent(Entity entityWhoPickup, Entity whatsPickedUp) {
        SoundInstance si = playSound(entityWhoPickup, assetManager.getSound("click"));
        if(si != null)
            si.setVolume(0.1f);
    }
}
