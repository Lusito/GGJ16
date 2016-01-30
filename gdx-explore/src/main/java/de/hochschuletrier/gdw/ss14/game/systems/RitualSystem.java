package de.hochschuletrier.gdw.ss14.game.systems;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;

import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixModifierComponent;
import de.hochschuletrier.gdw.commons.jackson.JacksonList;
import de.hochschuletrier.gdw.commons.jackson.JacksonReader;
import de.hochschuletrier.gdw.ss14.events.InputActionEvent;
import de.hochschuletrier.gdw.ss14.events.PickUpEvent;
import de.hochschuletrier.gdw.ss14.events.PickUpEvent.Listener;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.EntityBuilder;
import de.hochschuletrier.gdw.ss14.game.components.PickableComponent;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;
import de.hochschuletrier.gdw.ss14.game.components.RitualCasterComponent;

public class RitualSystem extends EntitySystem implements Listener, de.hochschuletrier.gdw.ss14.events.InputActionEvent.Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RitualSystem.class);
    
    private final EntityBuilder entityBuilder;

    private final float summonDistance;

    private final Map<String, ResourceDesc> resources = new HashMap<>();

    private final Map<String, RitualDesc> rituals = new HashMap<>();

    public RitualSystem(EntityBuilder entityBuilder) {
        this(entityBuilder, 0);
    }

    public RitualSystem(EntityBuilder entityBuilder, int priority) {
        super(priority);
        this.entityBuilder = entityBuilder;

        RitualSystemConfiguration conf = loadConf();
        summonDistance = conf.summonDistance;

        for (ResourceDesc desc : conf.resources) {
            resources.put(desc.id, desc);
        }

        for (RitualDesc desc : conf.rituals) {
            rituals.put(desc.id, desc);
        }
        
        PickUpEvent.register(this);
        InputActionEvent.register(this);
    }

    private static RitualSystemConfiguration loadConf() {
        try {
            return JacksonReader.read("data/json/rituals.json",
                    RitualSystemConfiguration.class);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InstantiationException | IOException | ParseException e) {
            throw new IllegalArgumentException("Couldn't load rituals.json", e);
        }
    }

    public void castRitual(Entity mage, String ritualId) {
        RitualCasterComponent comp = ComponentMappers.ritualCaster.get(mage);
        if (comp == null) {
            return;
        }

        if (!comp.availableRituals.contains(ritualId)) {
            LOGGER.info("Ritual \""+ritualId+"\n not available for player");
            return;
        }

        RitualDesc desc = rituals.get(ritualId);
        if (desc == null) {
            LOGGER.info("Ritual \""+ritualId+"\n not found");
            return;
        }

        if (!isResourcesAvailable(comp, desc)) {
            LOGGER.info("Ritual \""+ritualId+"\n not ready");
            return;
        }

        removeUsedResources(comp, desc);
        
        switch(desc.action) {
            case SPAWN:
                createSummonedEntity(mage, desc);
        }
    }

    private void createSummonedEntity(Entity mage, RitualDesc ritual) {
        PositionComponent magePos = ComponentMappers.position.get(mage);
        Vector2 mageDir = magePos.getDirectionVector();
        mageDir.scl(summonDistance);

        Vector2 summonPos = mageDir.add(magePos.x, magePos.y);

        Entity e = entityBuilder.createEntity(ritual.entityName, summonPos.x,
                summonPos.y);
        ComponentMappers.position.get(e).rotation = magePos.rotation;
        
        if(ritual.entityImpulse>0) {
            PhysixModifierComponent modifier = ComponentMappers.physixModifier.get(e);
            if(modifier!=null) {
                modifier.schedule(()->{
                    PhysixBodyComponent body = ComponentMappers.physixBody.get(e);
                    if(body!=null) {
                        body.applyImpulse(magePos.getDirectionVector().scl(ritual.entityImpulse));
                    }
                });
            }
        }
    }

    private void removeUsedResources(RitualCasterComponent caster,
            RitualDesc ritual) {
        for (String resource : ritual.resources) {
            caster.removeResource(resource);
        }
    }

    private boolean isResourcesAvailable(RitualCasterComponent caster,
            RitualDesc ritual) {
        for (String resource : ritual.resources) {
            if (!caster.hasResource(resource)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onPickupEvent(Entity entityWhoPickup, Entity whatsPickedUp) {
        RitualCasterComponent caster = ComponentMappers.ritualCaster.get(entityWhoPickup);
        if(caster==null)
            return;
        
        PickableComponent comp = ComponentMappers.pickable.get(whatsPickedUp);

        if(comp.resourceId!=null && comp.resourceCount>=1) {
            caster.addResources(comp.resourceId, comp.resourceCount);
        }
        
        if(comp.ritualId!=null)
            caster.addRitual(comp.ritualId);
        
        entityBuilder.removeEntity(whatsPickedUp);
    }

    @Override
    public void onDoAction(Entity mage) {
        RitualCasterComponent comp = ComponentMappers.ritualCaster.get(mage);
        if (comp == null)
            return;
        
        if(comp.availableRituals.isEmpty())
            return;
        
        comp.ritualIndex = normalizeIndex(comp.ritualIndex, comp.availableRituals.size());
        
        String ritualId = comp.availableRituals.get(comp.ritualIndex);
        castRitual(mage, ritualId);
    }

    private static final int normalizeIndex(int index, int count) {
        if(index<0)
            return count - index;
        else
            return index % count;
    }
    
    @Override
    public void onChangeAction(Entity mage, int diff) {
        RitualCasterComponent comp = ComponentMappers.ritualCaster.get(mage);
        if (comp == null || comp.availableRituals.isEmpty())
            return;
        
        comp.ritualIndex = normalizeIndex(comp.ritualIndex + diff, comp.availableRituals.size());
    }
    
    public List<RitualDesc> listRituals(Entity mage) {
        RitualCasterComponent comp = ComponentMappers.ritualCaster.get(mage);
        if (comp == null) {
            return Collections.emptyList();
        }

        List<RitualDesc> ret = new ArrayList<>(comp.availableRituals.size());
        for (String ritualId : comp.availableRituals) {
            RitualDesc desc = rituals.get(ritualId);
            if (desc != null) {
                ret.add(desc);
            }
        }

        return ret;
    }

    public List<ResourceDescWithCount> listResources(Entity mage) {
        RitualCasterComponent comp = ComponentMappers.ritualCaster.get(mage);
        if (comp == null) {
            return Collections.emptyList();
        }

        List<ResourceDescWithCount> ret = new ArrayList<>(
                comp.availableRituals.size());
        for (Entry<String, Integer> resource : comp.availableResources
                .entrySet()) {
            ResourceDesc desc = resources.get(resource.getKey());
            if (desc != null) {
                ret.add(new ResourceDescWithCount(desc, resource.getValue()));
            }
        }

        return ret;
    }

    public boolean isReady(Entity mage, RitualDesc ritual) {
        RitualCasterComponent comp = ComponentMappers.ritualCaster.get(mage);
        if (comp == null) {
            return false;
        }

        if (!comp.availableRituals.contains(ritual.getId())) {
            return false;
        }

        return isResourcesAvailable(comp, ritual);
    }

    public static final class RitualSystemConfiguration {

        float summonDistance;
        @JacksonList(ResourceDesc.class)
        List<ResourceDesc> resources;
        @JacksonList(RitualDesc.class)
        List<RitualDesc> rituals;
    }

    public static enum RitualAction {
        SPAWN
    }
    public static final class RitualDesc {

        String id;
        String name;
        String description;
        RitualAction action;
        String entityName;
        float entityImpulse;
        @JacksonList(String.class)
        List<String> resources;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getResources() {
            return resources;
        }
    }

    public static final class ResourceDesc {

        private String id;
        private String name;
        private String description;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    public static final class ResourceDescWithCount {

        public ResourceDesc desc;
        public int count;

        public ResourceDescWithCount(ResourceDesc desc, int count) {
            this.desc = desc;
            this.count = count;
        }
    }
    
}
