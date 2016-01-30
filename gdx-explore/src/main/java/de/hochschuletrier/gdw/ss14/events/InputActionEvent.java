package de.hochschuletrier.gdw.ss14.events;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.SnapshotArray;

public class InputActionEvent {

    public static interface Listener {
        void onDoAction(Entity entity);
        void onChangeAction(Entity entity, int diff);
    }

    private static final SnapshotArray<Listener> listeners = new SnapshotArray<Listener>();

    public static void emitDoAction(Entity entity) {
        Object[] items = listeners.begin();
        for (int i = 0, n = listeners.size; i < n; i++) {
            ((Listener) items[i]).onDoAction(entity);
        }
        listeners.end();
    }
    public static void emitChangeAction(Entity entity, int diff) {
        Object[] items = listeners.begin();
        for (int i = 0, n = listeners.size; i < n; i++) {
            ((Listener) items[i]).onChangeAction(entity, diff);
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
