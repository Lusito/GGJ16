package de.hochschuletrier.gdw.ss14.game.contactlisteners;

import com.badlogic.ashley.core.Entity;

import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContact;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContactAdapter;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.ss14.events.ReactionEvent;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;

public class ReactionListener extends PhysixContactAdapter {

    @Override
    public void beginContact(PhysixContact contact) {
        super.beginContact(contact);
        PhysixBodyComponent myComponent = contact.getMyComponent();
        PhysixBodyComponent otherComponent = contact.getOtherComponent();
        if (otherComponent != null && myComponent != null) {
            final Entity myEntity = myComponent.getEntity();
            final Entity otherEntity = otherComponent.getEntity();

            if(otherEntity != null && ComponentMappers.mat.has(otherEntity)) {
                ReactionEvent.emit(myEntity, otherEntity);
            }
        }
    }
}
