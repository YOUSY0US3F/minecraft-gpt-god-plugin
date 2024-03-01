package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.player.PlayerFishEvent;

public class FishingLoggable extends BaseLoggable {
    
    private String playerName;
    private String fishedItemName;

    public FishingLoggable(PlayerFishEvent event){
        this.playerName = event.getPlayer().getName();
        this.fishedItemName = event.getCaught() == null? null : event.getCaught().getName();
    }
    public String getLog(){
        if (fishedItemName== null){
            return null;
        }
        return String.format("%s fished a %s!",playerName, fishedItemName);
    }
}
