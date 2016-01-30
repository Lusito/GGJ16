package de.hochschuletrier.gdw.ss14.game.contactlisteners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContact;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContactAdapter;
import de.hochschuletrier.gdw.commons.gdx.physix.components.PhysixBodyComponent;
import de.hochschuletrier.gdw.commons.gdx.physix.systems.PhysixSystem;
import de.hochschuletrier.gdw.ss14.events.ReactionEvent;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.Game;
import de.hochschuletrier.gdw.ss14.game.GameConstants;
import de.hochschuletrier.gdw.ss14.game.components.PositionComponent;

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
            PositionComponent posComp = ComponentMappers.position.get(myEntity);
            Vector2 otherPos = contact.getOtherFixture().getBody().getPosition();
            float x = otherPos.x * GameConstants.BOX2D_SCALE;
            float y = otherPos.y * GameConstants.BOX2D_SCALE;
            float dx = x - posComp.x;
            float dy = y - posComp.y;
            Game.entityBuilder.createEntity("explosion", posComp.x + dx, posComp.y + dy);
            Game.engine.removeEntity(myEntity);
        }
    }
}
