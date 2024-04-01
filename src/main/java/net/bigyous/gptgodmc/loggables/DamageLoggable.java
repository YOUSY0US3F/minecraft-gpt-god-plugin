package net.bigyous.gptgodmc.loggables;

import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

import net.bigyous.gptgodmc.GPTGOD;

public class DamageLoggable extends BaseLoggable {
    private String entityName;
    private double damageAmount;
    private String damageSource;
    private boolean isValid;

    public DamageLoggable(EntityDamageEvent event) {
        this.entityName = event.getEntity().getName();
        this.damageAmount = event.getDamage();
        if(event.getDamageSource().getDirectEntity() != null){
            this.damageSource = event.getDamageSource().getDirectEntity().getName();
            if(event.getDamageSource().getDirectEntity() instanceof Projectile){
                Projectile projectile = (Projectile) event.getDamageSource().getDirectEntity();
                damageSource = projectile.getOwnerUniqueId() != null? GPTGOD.SERVER.getEntity(projectile.getOwnerUniqueId()).getName(): damageSource;
            }
            this.isValid = true;
        }
        else{
            this.isValid = false;
        }
        
    }

    @Override
    public String getLog() {
        if (!isValid){
            return null;
        }
        return entityName + " took " + Math.round(damageAmount) + " damage from " + damageSource;
    }

    @Override
    public boolean combine(Loggable other) {
        // Combine logic if needed
        return false;
    }
}
