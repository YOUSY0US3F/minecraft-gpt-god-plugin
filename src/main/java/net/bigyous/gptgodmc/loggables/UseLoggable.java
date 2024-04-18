package net.bigyous.gptgodmc.loggables;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class UseLoggable extends BaseLoggable{

    protected String blockName;
    protected String playerName;
    protected String item;
    protected Action action;

    private Set<Material> toolItems = Set.of(
        Material.FLINT_AND_STEEL, 
        Material.FIRE_CHARGE, 
        Material.SHEARS, 
        Material.BRUSH, 
        Material.STONE_HOE,
        Material.STONE_AXE,
        Material.STONE_SHOVEL,
        Material.IRON_HOE,
        Material.IRON_AXE,
        Material.IRON_SHOVEL,
        Material.WOODEN_HOE,
        Material.WOODEN_AXE,
        Material.WOODEN_SHOVEL,
        Material.GOLDEN_HOE,
        Material.GOLDEN_AXE,
        Material.GOLDEN_SHOVEL,
        Material.DIAMOND_HOE,
        Material.DIAMOND_AXE,
        Material.DIAMOND_SHOVEL,
        Material.NETHERITE_HOE,
        Material.NETHERITE_AXE,
        Material.NETHERITE_SHOVEL,
        Material.BONE_MEAL
        );
    
    public UseLoggable(PlayerInteractEvent event){
        this.blockName = event.hasBlock()? event.getClickedBlock().getType().toString() : null;
        this.playerName = event.getPlayer().getName();
        this.item = event.hasItem() && toolItems.contains(event.getItem().getType())? event.getItem().getType().toString() : null;
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
