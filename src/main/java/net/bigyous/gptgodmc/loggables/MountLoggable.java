package net.bigyous.gptgodmc.loggables;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityMountEvent;

public class MountLoggable extends BaseLoggable {
    private boolean isValid;
    private String mounter;
    private String mountee;
    public MountLoggable(EntityMountEvent event){
        this.isValid = event.getEntity().getType().equals(EntityType.PLAYER) || event.getMount().getType().equals(EntityType.PLAYER);
        this.mounter = event.getEntity().getName();
        this.mountee = event.getMount().getName();
    }

    @Override
    public String getLog() {
        return String.format("%s mounted a %s", mounter, mountee);
    }
}
