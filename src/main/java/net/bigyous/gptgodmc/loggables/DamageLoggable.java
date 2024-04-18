package net.bigyous.gptgodmc.loggables;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.bigyous.gptgodmc.GPTGOD;

public class DamageLoggable extends BaseLoggable {
    protected String entityName;
    protected double damageAmount;
    protected String damageSource;
    protected boolean isValid;

    public DamageLoggable(EntityDamageEvent event) {
        this.entityName = event.getEntity().getName();
        this.damageAmount = event.getDamage();
        this.isValid = !event.getCause().equals(DamageCause.ENTITY_ATTACK) && !event.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK) && event.getEntityType().equals(EntityType.PLAYER);
        if(event.getCause().equals(DamageCause.PROJECTILE)){
            this.damageSource = event.getDamageSource().getDirectEntity().getName();
            if(event.getDamageSource().getDirectEntity() instanceof Projectile){
                Projectile projectile = (Projectile) event.getDamageSource().getDirectEntity();
                damageSource = projectile.getOwnerUniqueId() != null? GPTGOD.SERVER.getEntity(projectile.getOwnerUniqueId()).getName(): damageSource;
            }
        }
        damageSource = event.getCause().toString();
        
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
        if(!(other instanceof DamageLoggable)) return false;

        DamageLoggable loggable = (DamageLoggable) other;

        if(loggable.entityName.equals(this.entityName) && loggable.damageSource.equals(this.damageSource)){
            this.damageAmount += loggable.damageAmount;
            return true;
        }
        return false;
    }
}
