package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.entity.PlayerDeathEvent;
import net.kyori.adventure.text.TextComponent;
public class DeathLoggable extends BaseLoggable {
    
    private String deathMessage;

    public DeathLoggable(PlayerDeathEvent event){
        this.deathMessage = ((TextComponent) event.deathMessage()).content();
    }

    public String getLog(){
        return deathMessage;
    }

}
