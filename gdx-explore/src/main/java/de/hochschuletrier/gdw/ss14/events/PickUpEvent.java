package de.hochschuletrier.gdw.ss14.events;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.SnapshotArray;

public class PickUpEvent {
    public static interface Listener {
        void onPickupEvent(Entity entityWhoPickup, Entity whatsPickedUp);
    }

    private static final SnapshotArray<Listener> listeners = new SnapshotArray<Listener>();

    public static void emit(Entity entityWhoPickup, Entity whatsPickedUp) {
        Object[] items = listeners.begin();
        for (int i = 0, n = listeners.size; i < n; i++) {
            ((Listener) items[i]).onPickupEvent(entityWhoPickup, whatsPickedUp);
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
