package de.hochschuletrier.gdw.ss14.events;

import com.badlogic.gdx.utils.SnapshotArray;

public class PlayerMessageEvent {

    public static interface Listener {

        void onPlayerMessageEvent(String message);
    }

    private static final SnapshotArray<Listener> listeners = new SnapshotArray<Listener>();

    public static void emit(String message) {
        Object[] items = listeners.begin();
        for (int i = 0, n = listeners.size; i < n; i++) {
            ((Listener) items[i]).onPlayerMessageEvent(message);
        }
        listeners.end();
    }

    public static void register(Listener listener) {
        listeners.add(listener);
    }

    public static void unregister(Listener listener) {
        listeners.removeValue(listener, true);
    }

    public static void unregisterAll() {
        listeners.clear();
    }

}
