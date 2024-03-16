package net.bigyous.gptgodmc.loggables;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import net.bigyous.gptgodmc.StructureManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class SpecialBlockPlaceEventLoggable extends BaseLoggable {
    private HashSet<Material> specialBlocks = new HashSet<Material>(){{
        add(Material.TNT);
        add(Material.LAVA);
        add(Material.WATER);
        add(Material.JUKEBOX);
        add(Material.OAK_SIGN);
        add(Material.BIRCH_SIGN);
        add(Material.SPRUCE_SIGN);
        add(Material.ACACIA_SIGN);
        add(Material.BAMBOO_SIGN);
        add(Material.CHERRY_SIGN);
        add(Material.JUNGLE_SIGN);
        add(Material.WARPED_SIGN);
        add(Material.CRIMSON_SIGN);
        add(Material.OAK_WALL_SIGN);
        add(Material.BIRCH_WALL_SIGN);
        add(Material.SPRUCE_WALL_SIGN);
        add(Material.ACACIA_WALL_SIGN);
        add(Material.BAMBOO_WALL_SIGN);
        add(Material.CHERRY_WALL_SIGN);
        add(Material.JUNGLE_WALL_SIGN);
        add(Material.WARPED_WALL_SIGN);
        add(Material.CRIMSON_WALL_SIGN);
    }};
    protected Block block;
    protected Player player;
    private String blockName;
    private String location;
    private int quantity = 1;
    private boolean isValid = false;
    public SpecialBlockPlaceEventLoggable(BlockPlaceEvent event){
        if(specialBlocks.contains(event.getBlock().getType())){
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

    @Override
    public boolean combine(Loggable l) {
        if(!(l instanceof SpecialBlockPlaceEventLoggable) || !isValid) return false;

        SpecialBlockPlaceEventLoggable loggable = (SpecialBlockPlaceEventLoggable) l;
        return loggable.block.getType().equals(block.getType()) && loggable.player.getName().equals(player.getName());
    }
}
