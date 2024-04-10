package net.bigyous.gptgodmc.loggables;

import org.bukkit.block.Container;
import org.bukkit.event.block.InventoryBlockStartEvent;

public class SmeltLoggable extends BaseLoggable{
    private String item;
    private String player = "";
    private String block;

    public SmeltLoggable(InventoryBlockStartEvent event){
        item = event.getSource().getType().toString();
        block = event.getBlock().getType().toString();
        if (event.getBlock() instanceof Container){
            Container container = (Container) event.getBlock();
            player = !container.getInventory().getViewers().isEmpty()? 
            " by " + container.getInventory().getViewers().get(0).getName() : "";
        }
    }
    
    @Override
    public String getLog() {
        return String.format("%s was placed in a %s%s", item, block, player);
    }
}
