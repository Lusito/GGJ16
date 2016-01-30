package de.hochschuletrier.gdw.ss14.game.components.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.hochschuletrier.gdw.commons.gdx.ashley.ComponentFactory;
import de.hochschuletrier.gdw.commons.utils.SafeProperties;
import de.hochschuletrier.gdw.ss14.game.components.render.AnimationComponent;
import de.hochschuletrier.gdw.ss14.game.components.render.TextureComponent;

public class TextureComponentFactory extends ComponentFactory<EntityFactoryParam> {

    @Override
    public String getType() {
        return "Texture";
    }

    /*
	 * 	public Texture texture;
		public TextureRegion region;
		public boolean flipX;
		public boolean flipY = true;
	    public float offsetX = 0.0f;
	    public float offsetY = 0.0f;
     */
    @Override
    public void run(Entity entity, SafeProperties meta,
            SafeProperties properties, EntityFactoryParam param) {
        TextureComponent component = engine.createComponent(TextureComponent.class);
        component.texture = assetManager.getTexture(properties.getString("animation"));
        component.flipX = properties.getBoolean("flipX", false);
        component.flipY = properties.getBoolean("flipY", true);
        component.offsetX = properties.getFloat("offsetX", 0.0f);
        component.offsetY = properties.getFloat("offsetY", 0.0f);
        assert (component.texture != null);
        entity.add(component);
    }

}
