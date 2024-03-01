package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.player.PlayerInteractEvent;

import net.kyori.adventure.text.TextComponent;
public class InteractLoggable extends BaseLoggable {

    protected String playerName;
    protected String targetName;
    protected String itemName;
    private int times;

    public InteractLoggable(PlayerInteractEvent event){
        this.playerName = event.getPlayer().getName();
        this.targetName = event.getClickedBlock().getType().name();
        this.itemName = event.hasItem()? null : ((TextComponent)event.getItem().displayName()).content();
        this.times = 1;
    }

    @Override
    public String getLog() {
        StringBuilder sb = new StringBuilder(playerName);
        if(times > 2){
            sb.append(" repeatedly ");
        }
        if(itemName != null){
            sb.append(" tried to use " + itemName + " on ");
        }
        else{
            sb.append(" interacted with ");
        }
        sb.append(targetName);
        return sb.toString();

    }

    public boolean equals(InteractLoggable other){
        // just found out the null object doesn't have .equals
        if(this.itemName == null){
            return playerName.equals(other.playerName) && other.itemName == null
            && targetName.equals(other.targetName);
        }
        return playerName.equals(other.playerName) && itemName.equals(other.itemName)
            && targetName.equals(other.targetName);
    }

    @Override
    public boolean combine(Loggable other) {
        if (!(other instanceof InteractLoggable)) return false;

        InteractLoggable otherInteraction = (InteractLoggable) other;

        if (this.equals(otherInteraction)){
            this.times += 1;
            return true;
        }
        return false;
    }
    
}
