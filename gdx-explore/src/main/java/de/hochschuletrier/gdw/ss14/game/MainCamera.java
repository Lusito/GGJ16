package de.hochschuletrier.gdw.ss14.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.hochschuletrier.gdw.commons.gdx.cameras.orthogonal.LimitedSmoothCamera;

public class MainCamera {

    private static LimitedSmoothCamera camera = new LimitedSmoothCamera();

    public static void update(float delta) {
        camera.update(delta);
    }

    public static void setDestination(float x, float y) {
        camera.setDestination(x, y);
    }

    public static void updateForced() {
        camera.updateForced();
    }

    public static final void resize(int width, int height) {
        camera.resize(width, height);
    }

    public static Vector3 getPosition() {
        return camera.getPosition();
    }

    public static void setBounds(float xMin, float yMin, float xMax, float yMax) {
        camera.setBounds(xMin, yMin, xMax, yMax);
    }

    public static void setCameraPosition(float x, float y) {
        camera.setCameraPosition(x, y);
    }

    public static final void setDestination(Vector2 p) {
        camera.setDestination(p);
    }

    public static float getFactor() {
        return camera.getFactor();
    }

    public static void setFactor(float factor) {
        camera.setFactor(factor);
    }

    public static void resetBounds() {
        camera.resetBounds();
    }

    public static OrthographicCamera getOrthographicCamera() {
        return camera.getOrthographicCamera();
    }

    public static void setCameraPosition(Vector3 pos) {
        camera.setCameraPosition(pos);
    }

    public static void setZoom(float newZoom) {
        camera.setZoom(newZoom);
    }

    public static float getZoom() {
        return camera.getZoom();
    }

    public static float getLeftOffset() {
        return camera.getLeftOffset();
    }

    public static float getTopOffset() {
        return camera.getTopOffset();
    }

    public static final void bind() {
        camera.bind();
    }
}
