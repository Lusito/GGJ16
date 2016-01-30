//package de.hochschuletrier.gdw.ss14.game.systems;
//
//import com.badlogic.ashley.core.Engine;
//import com.badlogic.ashley.core.Entity;
//import com.badlogic.ashley.core.Family;
//import com.badlogic.ashley.systems.IteratingSystem;
//import com.badlogic.gdx.Gdx;
//
//import de.hochschuletrier.gdw.commons.gdx.cameras.orthogonal.LimitedSmoothCamera;
//import de.hochschuletrier.gdw.commons.tiled.TiledMap;
//import de.hochschuletrier.gdw.ss14.Main;
//import de.hochschuletrier.gdw.ss14.game.components.PlayerComponent;
//import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
//
//public class LimitedSmoothCameraSystem extends IteratingSystem {
//    
//    private final LimitedSmoothCamera camera = new LimitedSmoothCamera();
//    
//    public LimitedSmoothCameraSystem(int priority) {
//        super(Family.all(PlayerComponent.class,PositionComponent.class).get(), priority);
//    }
//
//    @Override
//    public void addedToEngine(Engine engine) {
//        super.addedToEngine(engine);
//        camera.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        Main.getInstance().addScreenListener(camera);
//    }
//
//    @Override
//    public void removedFromEngine(Engine engine) {
//        super.removedFromEngine(engine);
//        Main.getInstance().removeScreenListener(camera);
//    }
//    
//    public void initMap(TiledMap map) {
//        camera.setBounds(0, 0, map.getWidth() * map.getTileWidth(), map.getHeight() * map.getTileHeight());
//    }
//
//    @Override
//    protected void processEntity(Entity entity, float deltaTime) {
//        PositionComponent position = entity.getComponent(PositionComponent.class);
//        camera.setDestination(position.x , position.y);
//        camera.update(deltaTime);
//        camera.bind();
//    }       
//
//    public LimitedSmoothCamera getCamera() {
//        return camera;
//    }
//}
