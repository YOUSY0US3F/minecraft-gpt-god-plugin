package net.bigyous.gptgodmc.loggables;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
public class SleepTogetherLoggable extends BaseLoggable {
    private String partners;
    private String playerName;
    private boolean isValid;
    public SleepTogetherLoggable(PlayerBedEnterEvent event){
        this.isValid = event.getBedEnterResult().equals(BedEnterResult.OK);
        StringBuilder sb = new StringBuilder();
        event.getBed().getLocation().getNearbyPlayers(1, 0, player -> player.isSleeping()).stream().forEach(player -> sb.append(player + ", ") );
        this.partners = sb.toString();
        this.playerName = event.getPlayer().getName();
    }

    @Override
    public String getLog() {
        if(!isValid || partners.isEmpty()) return null;
        return String.format("%s and %s got in bed together", partners, playerName);
    }
}
