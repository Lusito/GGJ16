package de.hochschuletrier.gdw.ss14.game.contactlisteners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContact;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContactAdapter;
import de.hochschuletrier.gdw.ss14.game.ComponentMappers;
import de.hochschuletrier.gdw.ss14.game.GameConstants;
import de.hochschuletrier.gdw.ss14.game.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.game.utils.WaterRectangle;

public class WaterListener extends PhysixContactAdapter {

    @Override
    public void beginContact(PhysixContact contact) {
        super.beginContact(contact);

        Object userData = contact.getOtherFixture().getBody().getUserData();
        if(userData instanceof WaterRectangle) {
            Entity entity = contact.getMyComponent().getEntity();
            PlayerComponent player = ComponentMappers.player.get(entity);
            player.waterRects.add((WaterRectangle)userData);
        }
    }

    @Override
    public void endContact(PhysixContact contact) {
        super.endContact(contact);
        
        Object userData = contact.getOtherFixture().getBody().getUserData();
        if(userData instanceof WaterRectangle) {
            Entity entity = contact.getMyComponent().getEntity();
            PlayerComponent player = ComponentMappers.player.get(entity);
            player.waterRects.remove((WaterRectangle)userData);
        }
    }

}
