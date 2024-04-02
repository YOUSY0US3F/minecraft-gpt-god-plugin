package net.bigyous.gptgodmc.loggables;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;

import net.bigyous.gptgodmc.utils.Targeter;

public class ShootLoggable extends BaseLoggable {
    private String shooter;
    private String target;
    private boolean isValid;
    @SuppressWarnings("null")
    public ShootLoggable(EntityShootBowEvent event){
        
        isValid = event.getEntityType().equals(EntityType.PLAYER);
        if(isValid){
            LivingEntity ent = event.getEntity();
            shooter = ent.getName();
            Entity targetEnt = Targeter.getTarget(ent);
            isValid = targetEnt != null;
            this.target = isValid? targetEnt.getName() : null;
        }

    }

    @Override
    public String getLog() {
        if(!isValid) return null;
        return String.format("%s shot an arrow at %s", shooter, target);
    }
}
