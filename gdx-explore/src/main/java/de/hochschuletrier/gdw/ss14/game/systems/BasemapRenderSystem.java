package de.hochschuletrier.gdw.ss14.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Texture;
import de.hochschuletrier.gdw.commons.gdx.tiled.TiledMapRendererGdx;
import de.hochschuletrier.gdw.commons.resourcelocator.CurrentResourceLocator;
import de.hochschuletrier.gdw.commons.tiled.Layer;
import de.hochschuletrier.gdw.commons.tiled.TileSet;
import de.hochschuletrier.gdw.commons.tiled.TiledMap;
import de.hochschuletrier.gdw.commons.tiled.tmx.TmxImage;
import java.util.HashMap;

public class BasemapRenderSystem extends EntitySystem {

    private final HashMap<TileSet, Texture> tilesetImages = new HashMap();
    private TiledMapRendererGdx mapRenderer;
    private TiledMap map;

    public BasemapRenderSystem(int priority) {
        super(priority);
    }

    public void initMap(TiledMap map) {
        this.clearImages();
        this.map = map;

        for (TileSet tileset : map.getTileSets()) {
            TmxImage img = tileset.getImage();
            String filename = CurrentResourceLocator.combinePaths(tileset.getFilename(), img.getSource());
            tilesetImages.put(tileset, new Texture(filename));
        }
        mapRenderer = new TiledMapRendererGdx(map, tilesetImages);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        this.clearImages();
    }

    private void clearImages() {
        tilesetImages.values().forEach(Texture::dispose);
        tilesetImages.clear();
    }

    @Override
    public void update(float deltaTime) {
        for (Layer layer : map.getLayers()) {
            if (layer.isTileLayer()) {
                mapRenderer.render(0, 0, layer);
            }
        }
        mapRenderer.update(deltaTime);
    }
}
