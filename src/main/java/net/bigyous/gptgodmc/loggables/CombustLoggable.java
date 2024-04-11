package net.bigyous.gptgodmc.loggables;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityCombustEvent;

public class CombustLoggable extends BaseLoggable {
    private String victimName;
    private boolean isValid;
    public CombustLoggable(EntityCombustEvent event){
        this.victimName = event.getEntity().getName();
        this.isValid = event.getEntityType().equals(EntityType.PLAYER);
    }

    @Override
    public String getLog() {
        if(!isValid) return null;
        return String.format("%s caught on fire", victimName);
    }
}
