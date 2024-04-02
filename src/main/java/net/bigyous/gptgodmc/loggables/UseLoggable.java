package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class UseLoggable extends BaseLoggable{

    protected String blockName;
    protected String playerName;
    protected String item;
    protected Action action;
    
    public UseLoggable(PlayerInteractEvent event){
        this.blockName = event.hasBlock()? event.getClickedBlock().getType().toString() : null;
        this.playerName = event.getPlayer().getName();
        this.item = event.hasItem() && !event.getItem().getType().isBlock()? event.getItem().getType().toString() : null;
        this.action = event.getAction(); 
    }

    @Override
    public String getLog() {
        if(action.equals(Action.RIGHT_CLICK_BLOCK) && blockName != null && item != null){
            return String.format("%s used %s on %s", playerName, item, blockName);
        }
        if(action.equals(Action.PHYSICAL) && blockName != null){
            return String.format("%s triggered %s", playerName, blockName);
        }
        return null;
    }

    public boolean equals(UseLoggable loggable){
        return this.playerName.equals(loggable.playerName) && 
        this.blockName.equals(loggable.blockName) && 
        this.action.equals(loggable.action) && 
        this.item.equals(loggable.item);
    }

    @Override
    public boolean combine(Loggable l) {
        return this.equals(l);
    }
}
