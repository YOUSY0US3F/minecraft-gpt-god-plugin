package net.bigyous.gptgodmc.loggables;

import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.event.block.BlockPlaceEvent;

import com.destroystokyo.paper.MaterialSetTag;

public class PlantLoggable extends BaseLoggable {
    private String plant;
    private String player;
    private boolean isValid = false;

    public PlantLoggable (BlockPlaceEvent event){
        if(event.getBlock().getBlockData() instanceof Sapling || event.getBlock().getBlockData() instanceof Ageable || MaterialSetTag.FLOWERS.isTagged(event.getBlock().getType())){
            this.isValid = true;
            this.player = event.getPlayer().getName();
            this.plant = event.getBlock().getType().name();
        }

    }

    @Override
    public String getLog() {
        if (!isValid){
            return null;
        }
        return String.format("%s planted a %s", player, plant);
    }
}
