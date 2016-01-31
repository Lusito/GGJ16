package de.hochschuletrier.gdw.ss14.game.utils;

import com.badlogic.gdx.math.Vector2;

public class WaterRectangle {
    private static final Vector2 dummyA = new Vector2();
    private static final Vector2 dummyB = new Vector2();
    private static final Vector2 dummyC = new Vector2();
    private static final Vector2 dummyD = new Vector2();
    
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    
    public WaterRectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    private float distToSegmentSquared(Vector2 p, Vector2 v, Vector2 w) {
        float l2 = v.dst2(w);
        if (l2 == 0) {
            return p.dst2(v);
        }
        float t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2;
        if (t < 0) {
            return p.dst2(v);
        }
        if (t > 1) {
            return p.dst2(w);
        }
        return p.dst2(v.x + t * (w.x - v.x), v.y + t * (w.y - v.y));
    }

    private float distToSegment(Vector2 p, Vector2 v, Vector2 w) {
        return (float) Math.sqrt(distToSegmentSquared(p, v, w));
    }
    
    public float distanceTo(Vector2 p) {
        dummyA.set(x, y);
        dummyB.set(x, y + height);
        dummyC.set(x + width, y + height);
        dummyD.set(x + width, y);
        float ab = distToSegment(p, dummyA, dummyB);
        float bc = distToSegment(p, dummyB, dummyC);
        float cd = distToSegment(p, dummyC, dummyD);
        float da = distToSegment(p, dummyD, dummyA);
        
        return Math.min(Math.min(ab, bc), Math.min(cd, da));
    }
}
