package de.hochschuletrier.gdw.ss14.game.contactlisteners;

import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContact;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContactAdapter;
import de.hochschuletrier.gdw.ss14.events.PickUpEvent;
import de.hochschuletrier.gdw.ss14.game.components.PickableComponent;

public class PickUpListener extends PhysixContactAdapter {

    @Override
    public void beginContact(PhysixContact contact) {
        super.beginContact(contact);

        if (contact.getOtherComponent().getEntity().getComponent(PickableComponent.class) != null) {
            PickUpEvent.emit(contact.getMyComponent().getEntity(), contact.getOtherComponent().getEntity());
        }
    }

}
