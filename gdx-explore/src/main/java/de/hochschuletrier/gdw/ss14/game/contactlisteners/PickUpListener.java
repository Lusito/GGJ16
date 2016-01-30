package de.hochschuletrier.gdw.ss14.game.contactlisteners;

import com.badlogic.ashley.core.Entity;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContact;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContactAdapter;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.ss14.events.PickUpEvent;
import de.hochschuletrier.gdw.ss14.game.components.PickableComponent;

public class PickUpListener extends PhysixContactAdapter {

    @Override
    public void beginContact(PhysixContact contact) {
        super.beginContact(contact);

        PhysixBodyComponent otherComponent = contact.getOtherComponent();
        if (otherComponent != null) {
            final Entity otherEntity = otherComponent.getEntity();
            if(otherEntity != null && otherEntity.getComponent(PickableComponent.class) != null) {
                PickUpEvent.emit(contact.getMyComponent().getEntity(), otherEntity);
            }
        }
    }

}
