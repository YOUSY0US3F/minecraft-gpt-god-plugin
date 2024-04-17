package net.bigyous.gptgodmc.loggables;

import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.event.block.BlockPlaceEvent;

import com.destroystokyo.paper.MaterialSetTag;

public class PlantLoggable extends BaseLoggable {
    protected String plant;
    protected String player;
    protected boolean isValid = false;
    protected int count = 1;

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
        String quantifier = count > 1? String.valueOf(count) : "a";
        return String.format("%s planted %s %s", player, quantifier, plant);
    }

    @Override
    public boolean combine(Loggable l) {
        if(!(l instanceof PlantLoggable)) return false;
        PlantLoggable other = (PlantLoggable) l;
        if(!other.isValid || !this.isValid) return false;

        if (other.plant.equals(this.plant) && other.player.equals(this.player)){
            this.count += other.count;
            return true;
        }
        return false;
    }
}
