package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.player.PlayerItemConsumeEvent;

import net.kyori.adventure.text.TextComponent;

public class EatingLoggable extends BaseLoggable{
    private String entityName;
    private String itemName;
    private boolean isValid;
    public EatingLoggable(PlayerItemConsumeEvent event){
       this.entityName = event.getPlayer().getName();
       this.itemName = ((TextComponent)event.getItem().displayName()).content();
    }    

    @Override
    public String getLog() {
        if (!isValid){
            return null;
        }
        return entityName + " consumed " + itemName;
    }

    @Override
    public boolean combine(Loggable other) {
        return false;
    }
}
