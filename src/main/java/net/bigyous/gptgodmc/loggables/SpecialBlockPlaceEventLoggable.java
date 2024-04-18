package net.bigyous.gptgodmc.loggables;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import com.destroystokyo.paper.MaterialSetTag;

import net.bigyous.gptgodmc.StructureManager;

public class SpecialBlockPlaceEventLoggable extends BaseLoggable {
    private HashSet<Material> specialBlocks = new HashSet<Material>(){{
        add(Material.TNT);
        add(Material.LAVA);
        add(Material.WATER);
        add(Material.JUKEBOX);
        add(Material.CRAFTING_TABLE);
        add(Material.FURNACE);
        add(Material.BLAST_FURNACE);
        add(Material.CAMPFIRE);
        add(Material.SOUL_CAMPFIRE);
        add(Material.ANVIL);
        add(Material.CAKE);
        add(Material.ENCHANTING_TABLE);
        add(Material.ARMOR_STAND);
        add(Material.CHEST);
    }};

    private List<Tag<Material>> specialGroups = List.of(MaterialSetTag.ALL_SIGNS, MaterialSetTag.DOORS, MaterialSetTag.BEDS, MaterialSetTag.CANDLES, MaterialSetTag.ITEMS_BOATS);
    protected Block block;
    protected Player player;
    private String blockName;
    private String location;
    protected int quantity = 1;
    private boolean isValid = false;
    public SpecialBlockPlaceEventLoggable(BlockPlaceEvent event){
        if(specialBlocks.contains(event.getBlock().getType()) || isInSpecialGroup(event.getBlock().getType())){
            this.player = event.getPlayer();
            this.block = event.getBlock();
            this.blockName = block.getType().toString();
            this.location = getlocation(player);
            this.isValid = true;
        }
    }
    private String getlocation(Player player){
        
        if(StructureManager.getStructureProximityData(player.getLocation()) != null){
            if(StructureManager.getStructureProximityData(player.getLocation()).getDistance() < 10){
                return "at: " + StructureManager.getStructureProximityData(player.getLocation()).getStructure();
            }
            if(StructureManager.getStructureProximityData(player.getLocation()).getDistance() >= 10){
                return String.format("%d blocks away from %s",StructureManager.getStructureProximityData(player.getLocation()).getDistance(), StructureManager.getStructureProximityData(player.getLocation()).getStructure());
            }
        }
        return "";
    }
    private String getQuantity(){
        return quantity > 1 ? String.valueOf(quantity) : "a";
    }
    @Override
    public String getLog() {
        if(!isValid) return null;
        return getOutput();
    }

    private String getOutput(){
        return String.format("%s placed %s %s %s", player.getName(), getQuantity(), blockName, location);
    }

    private Boolean isInSpecialGroup(Material mat){
        for(Tag<Material> group : specialGroups){
            if(group.isTagged(mat)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean combine(Loggable l) {
        if(!(l instanceof SpecialBlockPlaceEventLoggable) || !isValid) return false;

        SpecialBlockPlaceEventLoggable loggable = (SpecialBlockPlaceEventLoggable) l;
        if (loggable.block.getType().equals(block.getType()) && loggable.player.getName().equals(player.getName())){
            this.quantity += loggable.quantity;
        }
        return loggable.block.getType().equals(block.getType()) && loggable.player.getName().equals(player.getName());
    }
}
