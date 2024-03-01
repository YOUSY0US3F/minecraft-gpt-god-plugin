package net.bigyous.gptgodmc.loggables;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackLoggable extends BaseLoggable {
    private String attackerName;
    private String targetName;
    private Boolean isValid = false;

    public AttackLoggable(EntityDamageByEntityEvent event) {
        this.attackerName = event.getDamager().getName();
        this.targetName = event.getEntity().getName();
        this.isValid = event.getDamager().getType().equals(EntityType.PLAYER) ||
        event.getEntity().getType().equals(EntityType.PLAYER);
    }

    @Override
    public String getLog() {
        if(!isValid) return null;
        return attackerName + " attacked " + targetName;
    }

    @Override
    public boolean combine(Loggable other) {
        // Combine logic if needed
        return false;
    }
}
