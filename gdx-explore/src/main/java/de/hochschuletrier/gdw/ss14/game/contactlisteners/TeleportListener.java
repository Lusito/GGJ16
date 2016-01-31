package de.hochschuletrier.gdw.ss14.game.contactlisteners;

import com.badlogic.ashley.core.Entity;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContact;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContactAdapter;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.ss14.events.TeleportEvent;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;

public class TeleportListener extends PhysixContactAdapter {

    @Override
    public void beginContact(PhysixContact contact) {
        super.beginContact(contact);

        PhysixBodyComponent otherComponent = contact.getOtherComponent();
        if (otherComponent != null && !contact.getOtherFixture().isSensor()) {
            final Entity otherEntity = otherComponent.getEntity();
            if(otherEntity != null && ComponentMappers.player.has(otherEntity)) {
                TeleportEvent.emit(contact.getMyComponent().getEntity(), otherEntity);
            }
        }
    }

}
