package de.hochschuletrier.gdw.ss14.game.contactlisteners;

import com.badlogic.ashley.core.Entity;

import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContact;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContactAdapter;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.ss14.events.ReactionEvent;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.Game;
import de.hochschuletrier.gdw.ss14.game.components.MaterialComponent;
import de.hochschuletrier.gdw.ss14.game.gamelogic.MaterialType;
import de.hochschuletrier.gdw.ss14.game.systems.ReactionSystem;

public class ReactionListener extends PhysixContactAdapter {

    @Override
    public void beginContact(PhysixContact contact) {
        super.beginContact(contact);
        PhysixBodyComponent myComponent = contact.getMyComponent();
        PhysixBodyComponent otherComponent = contact.getOtherComponent();
        final Entity myEntity = myComponent.getEntity();
        
        if (otherComponent != null) {
            final Entity otherEntity = otherComponent.getEntity();

            if(otherEntity != null && ComponentMappers.mat.has(otherEntity)) {
                ReactionEvent.emit(myEntity, otherEntity);
            }
        } else {
            MaterialComponent myMat = ComponentMappers.mat.get(myEntity);
            if(myMat.type != MaterialType.ELEMENTAL)
                ReactionSystem.explode(myEntity);
            else
                Game.engine.removeEntity(myEntity);
        }
    }
}
