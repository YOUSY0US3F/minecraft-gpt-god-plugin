package net.bigyous.gptgodmc.loggables;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;
public class EntityLoveLoggable extends BaseLoggable {
    private String player;
    private String entityType;
    public EntityLoveLoggable(EntityEnterLoveModeEvent event){
        player = event.getHumanEntity().getName();
        entityType = event.getEntity().getName();
    }

    @Override
    public String getLog() {
        return String.format("%s bred two %ss together", player,entityType);
    }
}
