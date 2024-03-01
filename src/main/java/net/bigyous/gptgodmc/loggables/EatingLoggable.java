package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.player.PlayerItemConsumeEvent;

import net.kyori.adventure.text.TextComponent;

public class EatingLoggable extends BaseLoggable{
    protected String entityName;
    protected String itemName;
    public EatingLoggable(PlayerItemConsumeEvent event){
       this.entityName = event.getPlayer().getName();
       this.itemName = event.getItem().getType().toString();
    }    

    @Override
    public String getLog() {
        return entityName + " consumed " + itemName;
    }

    @Override
    public boolean combine(Loggable other) {
        return false;
    }
}
