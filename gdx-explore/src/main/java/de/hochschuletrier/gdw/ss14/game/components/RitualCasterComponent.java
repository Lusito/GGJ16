package de.hochschuletrier.gdw.ss14.game.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class RitualCasterComponent extends Component implements Pool.Poolable {

    public List<String> availableRituals = new ArrayList<>();

    /**
     * name to count
     */
    public Map<String, Integer> availableResources = new HashMap<>();
    
    public int ritualIndex;

    public float remainingTime = 0.f;
    
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
        addResources(name, 1);
    }
    public void addResources(String name, int incr) {
        name = name.trim();
        if(name.isEmpty())
            return;
        
        Integer count = availableResources.getOrDefault(name, 0);
        availableResources.put(name, count + incr);
    }

    public void addRitual(String name) {
        name = name.trim();
        if(!availableRituals.contains(name) && !name.isEmpty()) {
            availableRituals.add(name);
            ritualIndex = availableRituals.size()-1;
        }
    }

    @Override
    public void reset() {
        availableRituals.clear();
        availableResources.clear();
        ritualIndex = 0;
        remainingTime = 0.f;
    }

}
