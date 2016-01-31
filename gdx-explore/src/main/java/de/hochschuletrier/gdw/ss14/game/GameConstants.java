package de.hochschuletrier.gdw.ss14.game;

public class GameConstants {

    // Priorities for entity systems
    public static final int PRIORITY_PHYSIX = 0;
    public static final int PRIORITY_ANIMATION_STATE = 5;
    public static final int PRIORITY_ENTITIES = 10;
    public static final int PRIORITY_TILE_RENDERER = 19;
    public static final int PRIORITY_RENDER = 20;
    public static final int PRIORITY_DEBUG_WORLD = 30;
    public static final int PRIORITY_HUD = 40;
    public static final int PRIORITY_SOUND = 50;
    public static final int PRIORITY_REMOVE_ENTITIES = 1000;

    // PooledEngine parameters
    public static final int ENTITY_POOL_INITIAL_SIZE = 32;
    public static final int ENTITY_POOL_MAX_SIZE = 256;
    public static final int COMPONENT_POOL_INITIAL_SIZE = 32;
    public static final int COMPONENT_POOL_MAX_SIZE = 256;

    // Physix parameters
    public static final int POSITION_ITERATIONS = 3;
    public static final int VELOCITY_ITERATIONS = 8;
    public static final int BOX2D_SCALE = 40;
    public static final short MASK_EVERYTHING  = 0xFFF;
    public static final short CATEGORY_LIT = 0x002;
    public static final short CATEGORY_NON_LIT = 0x004;
    public static final short CATEGORY_WATER = 0x008;
    
    public static float MUSIC_FADE_TIME = 2;
    public static float MUSIC_VOLUME_MENU = 0.4f;
    public static float MUSIC_VOLUME_INGAME = 0.08f;
    public static float WATER_LISTEN_MIN_DISTANCE = 50;
    public static float WATER_LISTEN_MAX_DISTANCE = 300;
    public static float AMBIENT_VOLUME = 0.1f;

    // Light parameters
    public static final int LIGHT_RAYS = 360;
    public static final float LIGHT_AMBIENT = 0.05f;
    public static final boolean LIGHT_BLUR = true;
    public static final int LIGHT_BLURNUM = 3;
    public static final boolean LIGHT_SHADOW = true;
    public static final boolean LIGHT_DIFFUSE = false;
}
