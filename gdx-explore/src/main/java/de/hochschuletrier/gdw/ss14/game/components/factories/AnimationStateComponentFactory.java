package de.hochschuletrier.gdw.ss14.game.components.factories;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import com.badlogic.ashley.core.Entity;

import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.gdx.assets.AnimationExtended;
import de.hochschuletrier.gdw.commons.jackson.JacksonMap;
import de.hochschuletrier.gdw.commons.jackson.JacksonReader;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationState;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationStateComponent;

public class AnimationStateComponentFactory extends ComponentFactory<EntityFactoryParam>  {
    private static HashMap<String, HashMap<String, String>> animationStateTemplates;
    
    @Override
    public String getType() {
        return "AnimationState";
    }
    
    private static HashMap<String, String> getTemplate(String name) {
        if(animationStateTemplates == null) {
            try {
                animationStateTemplates = JacksonReader.readMapMap("data/json/animationStateTemplates.json", String.class);
            } catch (NoSuchFieldException | IllegalArgumentException
                    | IllegalAccessException | InstantiationException
                    | IOException | ParseException e) {
                throw new IllegalArgumentException("Could not load animationStateTemplates.json", e);
            }
        }
        
        return animationStateTemplates.get(name);
    }

    @Override
    public void run(Entity entity, SafeProperties meta,
            SafeProperties properties, EntityFactoryParam param) {
        AnimationStateComponent component = engine.createComponent(AnimationStateComponent.class);
        HashMap<String, String> template = getTemplate(properties.getString("template"));
        for(java.util.Map.Entry<String, String> entry : template.entrySet()) {
            AnimationState state = AnimationState.valueOf(entry.getKey());
            AnimationExtended animation = assetManager.getAnimation(entry.getValue());
            component.put(state, animation);
        }

        entity.add(component);
    }

}
