package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.player.PlayerInteractEvent;

import net.kyori.adventure.text.TextComponent;
public class InteractLoggable extends BaseLoggable {

    protected String playerName;
    protected String targetName;
    protected String itemName;
    private int times;
    private boolean isValid = false;

    public InteractLoggable(PlayerInteractEvent event){
        this.playerName = event.getPlayer().getName();
        this.targetName = event.hasBlock()? event.getClickedBlock().getType().name(): null;
        this.itemName = event.hasItem()? ((TextComponent)event.getItem().displayName()).content(): null;
        this.times = 1;
        isValid = targetName == null;
    }

    @Override
    public String getLog() {
        if(!isValid){
            return null;
        }
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
        if(!this.isValid){
            return false;
        }
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
