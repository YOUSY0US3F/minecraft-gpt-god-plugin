package net.bigyous.gptgodmc.loggables;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.entity.Player;

public class CombustLoggable extends BaseLoggable {
    private String victimName;
    private boolean isValid;
    public CombustLoggable(EntityCombustEvent event){
        this.isValid = event.getEntityType().equals(EntityType.PLAYER);
        this.victimName = isValid? ((Player) event.getEntity()).getName() : null;
    }

    @Override
    public String getLog() {
        if(!isValid) return null;
        return String.format("%s caught on fire", victimName);
    }
}
