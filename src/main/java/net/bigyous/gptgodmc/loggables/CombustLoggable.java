package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.entity.EntityCombustByEntityEvent;

public class CombustLoggable extends BaseLoggable {
    private String combusterName;
    private String victimName;
    public CombustLoggable(EntityCombustByEntityEvent event){
        this.combusterName = event.getCombuster().getName();
        this.victimName = event.getEntity().getName();
    }

    @Override
    public String getLog() {
        return String.format("%s set %s on fire", combusterName, victimName);
    }
}
