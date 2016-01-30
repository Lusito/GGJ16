package de.hochschuletrier.gdw.ss14.game.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class RitualCasterComponent extends Component implements Pool.Poolable {

    public Set<String> availableRituals = new HashSet<>();

    /**
     * name to count
     */
    public Map<String, Integer> availableResources = new HashMap<>();

    public boolean hasResource(String name) {
        return availableResources.getOrDefault(name, 0) > 0;
    }

    public void removeResource(String name) {
        Integer count = availableResources.get(name);
        if (count == null) {
            return;
        }

        count--;
        if (count <= 0) {
            availableResources.remove(name);
        } else {
            availableResources.put(name, count);
        }
    }

    public void addResource(String name) {
        Integer count = availableResources.getOrDefault(name, 0);
        availableResources.put(name, count + 1);
    }

    public void addRitual(String name) {
        availableRituals.add(name);
    }

    @Override
    public void reset() {
        availableRituals.clear();
    }

}
