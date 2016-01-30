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

    private static final float MESSAGE_DURATION = 2.f;
    
    private static final Color COLOR_READY = Color.WHITE;
    private static final Color COLOR_NOT_READY = new Color(0.4f, 0.4f, 0.4f, 1);
    
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
        font = assetManager.getFont("verdana_24");
        this.ritualSystem = ritualSystem;
        this.player = player;
        
        PlayerMessageEvent.register(this);
    }

    public void render() {
        Main.getInstance().screenCamera.bind();

        RitualDesc ritualDesc = ritualSystem.getCurrentRitual(player);
        if(ritualDesc==null)
            return;

        float fontScaleX = font.getScaleX();
        float fontScaleY = font.getScaleY();
        
        boolean ready = ritualSystem.isReady(player, ritualDesc);
        
        String missingResourceList = ready ? "" : buildMissingResourceList(ritualDesc);
        
        DrawUtil.batch.draw(overlay, 10, Gdx.graphics.getHeight() - 10, overlay.getWidth(), -overlay.getHeight());
     
        int textBoxWidth = overlay.getWidth()-70;
        
        font.setColor(ready ? COLOR_READY : COLOR_NOT_READY);

        font.setScale(1.0f);
        
        float offset = Gdx.graphics.getHeight() - 190;
        offset += font.drawWrapped(DrawUtil.batch, ritualDesc.getName(), 50, offset, textBoxWidth).height;

        font.setScale(0.72f);
        
        offset += 15;
        offset += font.drawWrapped(DrawUtil.batch, ritualDesc.getDescription(), 50, offset, textBoxWidth).height;

        offset += 15;
        font.drawWrapped(DrawUtil.batch, ready ? "Press SPACE to cast" : ("Missing: "+missingResourceList), 50, offset, textBoxWidth);
        
        
        if(messageTimout>0.f) {
            int msgX = 200; // TODO
            int msgY = 400;
            int msgDir = -1;
            
            DrawUtil.batch.draw(messageBubble, msgX, msgY + (msgDir<0 ? messageBubble.getHeight() : 0), messageBubble.getWidth(), msgDir*messageBubble.getHeight());
            
            font.setColor(Color.WHITE);
            font.setScale(1.2f);
            font.drawWrapped(DrawUtil.batch, message, msgX+20, msgY+20, messageBubble.getWidth()-40);
        }

        
        font.setScale(fontScaleX, fontScaleY);
    }

    private String buildMissingResourceList(RitualDesc ritualDesc) {
        RitualCasterComponent comp = ComponentMappers.ritualCaster.get(player);
        if(comp==null)
            return "";
        
        List<String> missingIds = new ArrayList<>(ritualDesc.getResources());
        missingIds.removeAll(comp.availableResources.keySet());
        
        StringBuilder str = new StringBuilder();
        boolean first = true;
        for(String id : missingIds) {
            if(first) first = false;
            else str.append(", ");
            
            str.append(ritualSystem.getResource(id).getName());
        }
        return str.toString();
    }

    public void update(float delta) {
        if(messageTimout>0.f)
            messageTimout-=delta;
    }
    
    public void dispose() {
    }

    @Override
    public void onPlayerMessageEvent(String message) {
        if(messageTimout>0.5f && message!=null) {
            this.message+="\n"+message;
            
        } else {
            this.message=message;
        }

        messageTimout = MESSAGE_DURATION;
    }

}
