package de.hochschuletrier.gdw.ss14.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.utils.DrawUtil;
import de.hochschuletrier.gdw.ss14.Main;
import de.hochschuletrier.gdw.ss14.events.PlayerMessageEvent;
import de.hochschuletrier.gdw.ss14.game.components.RitualCasterComponent;
import de.hochschuletrier.gdw.ss14.game.systems.RitualSystem;
import de.hochschuletrier.gdw.ss14.game.systems.RitualSystem.RitualDesc;

public class Hud implements PlayerMessageEvent.Listener {

    private static final float MESSAGE_DURATION = 5.f;
    
    private static final Color COLOR_READY = Color.WHITE;
    private static final Color COLOR_NOT_READY = new Color(0.6f, 0.6f, 0.6f, 1);
    
    private Texture overlay;
    
    private Texture messageBubble;
    
    private BitmapFont font;
    
    private RitualSystem ritualSystem;
    
    private Entity player;
    
    private String message;
    
    private float messageTimout = 0.f;
    
    public Hud(AssetManagerX assetManager, RitualSystem ritualSystem, Entity player) {
        overlay = assetManager.getTexture("hud_bg");
        messageBubble = assetManager.getTexture("hud_msg_bg");
        font = assetManager.getFont("verdana_16");
        this.ritualSystem = ritualSystem;
        this.player = player;
        
        PlayerMessageEvent.register(this);
    }

    public void render() {
        Main.getInstance().screenCamera.bind();

        RitualDesc ritualDesc = ritualSystem.getCurrentRitual(player);
        if(ritualDesc==null)
            return;
        
        boolean ready = ritualSystem.isReady(player, ritualDesc);
        
        String missingResourceList = ready ? "" : buildMissingResourceList(ritualDesc);
        
        DrawUtil.batch.draw(overlay, 10, Gdx.graphics.getHeight() - 10, overlay.getWidth(), -overlay.getHeight());
     
        int textBoxWidth = overlay.getWidth()-70;
        
        font.setColor(ready ? COLOR_READY : COLOR_NOT_READY);
        
        float offset = Gdx.graphics.getHeight() - 190;
        offset += font.drawWrapped(DrawUtil.batch, ritualDesc.getName(), 50, (int)offset, textBoxWidth).height;
        
        offset += 15;
        offset += font.drawWrapped(DrawUtil.batch, ritualDesc.getDescription(), 50, (int)offset, textBoxWidth).height;

        offset += 15;
        font.drawWrapped(DrawUtil.batch, ready ? "Press SPACE to cast" : ("Missing: "+missingResourceList), 50, (int)offset, textBoxWidth);
        
        
        if(messageTimout>0.f) {
            float msgX = Gdx.graphics.getWidth()/2.f - messageBubble.getWidth() + 30; // TODO
            float msgY = Gdx.graphics.getHeight()/2.f - messageBubble.getHeight() - 30;
            int msgDir = -1;
            
            DrawUtil.batch.draw(messageBubble, msgX, msgY + (msgDir<0 ? messageBubble.getHeight() : 0), messageBubble.getWidth(), msgDir*messageBubble.getHeight());
            
            font.setColor(Color.WHITE);
            font.drawWrapped(DrawUtil.batch, message, (int) msgX+25, (int) msgY+25, (int) messageBubble.getWidth()-50);
        }
    }

    private String buildMissingResourceList(RitualDesc ritualDesc) {
        RitualCasterComponent comp = ComponentMappers.ritualCaster.get(player);
        if(comp==null)
            return "";
        
        List<String> missingIds = new ArrayList<>(ritualDesc.getResources());
        missingIds.removeAll(comp.availableResources.keySet());
        
        StringBuilder str = new StringBuilder();
        boolean first = true;
        int sameResCount = 0;
        String lastName = null;
        for(String id : missingIds) {
            if(first) first = false;
            else str.append(", ");
            
            String name = ritualSystem.getResource(id).getName();
            str.append(name);
            
            if(lastName==null)
                lastName = name;
                
            if(lastName.equals(name))
                sameResCount++;
        }
        
        if(sameResCount==missingIds.size() && sameResCount>1)
            return sameResCount+" "+lastName+"s";
        
        return str.toString();
    }

    public void update(float delta) {
        if(messageTimout>0.f)
            messageTimout-=delta;
    }
    
    public void dispose() {
    }

    @Override
    public void onPlayerMessageEvent(String message, boolean clear) {
        if(this.message!=null && (messageTimout>2.0f || this.message.length()<3) && !clear) {
            this.message+="\n"+message;
            
            String[] lines = this.message.split("\n");
            if(lines.length>2) {
                this.message = lines[lines.length-2] + "\n" + lines[lines.length-1];
            }
            
        } else {
            this.message=message;
        }

        messageTimout = MESSAGE_DURATION;
    }

}
