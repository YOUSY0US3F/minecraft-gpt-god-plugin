package net.bigyous.gptgodmc.loggables;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockExplodeEvent;
import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.StructureManager;
import net.bigyous.gptgodmc.StructureManager.StructureProximityData;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosionLoggable extends BaseLoggable{
    private String nearbyPlayers;
    private String nearbyStructure;

    public ExplosionLoggable(BlockExplodeEvent event){
        StringBuilder sb = new StringBuilder();
        event.getBlock().getLocation().getNearbyPlayers(5).stream().forEach((Player player)-> {
            sb.append(player.getName() + ", ");
        });
        this.nearbyPlayers = sb.toString();
        
        StructureProximityData data  = StructureManager.getStructureProximityData(event.getBlock().getLocation());

        this.nearbyStructure = data.getDistance() <= 5 ? data.getStructure() : "";

        GPTGOD.LOGGER.info(nearbyPlayers, nearbyStructure);
    }

    public ExplosionLoggable(EntityExplodeEvent event){
        StringBuilder sb = new StringBuilder();
        event.getLocation().getNearbyPlayers(5).stream().forEach((Player player)-> {
            sb.append(player.getName() + ", ");
        });
        this.nearbyPlayers = sb.toString();
        
        StructureProximityData data  = StructureManager.getStructureProximityData(event.getLocation());

        this.nearbyStructure = data != null && data.getDistance() <= 5 ? data.getStructure() : "";

        GPTGOD.LOGGER.info(nearbyPlayers, nearbyStructure);
    }

    @Override
    public String getLog() {
        if(nearbyPlayers.isEmpty() && nearbyStructure.isEmpty()) return null;
        return String.format("Something exploded%s%s", !nearbyPlayers.isEmpty()? " near " + nearbyPlayers : "", nearbyStructure.isEmpty()? " at " + nearbyStructure : "");
    }
}
