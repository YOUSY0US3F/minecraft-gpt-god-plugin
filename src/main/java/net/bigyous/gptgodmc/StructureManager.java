package net.bigyous.gptgodmc;


import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.entity.Player;

public class StructureManager implements Listener {
    private static HashMap<String,Structure> structures = new HashMap<String,Structure>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Location newBlock = event.getBlock().getLocation();
        addBlockToStructures(newBlock, event.getPlayer());
        GPTGOD.LOGGER.info(structures.toString());
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event){
        removeBlockFromAllStructures(event.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        removeBlockFromAllStructures(event.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockExploded(BlockExplodeEvent event){
        for(Block block : event.blockList()){
            Location location = block.getLocation();
            removeBlockFromAllStructures(location);
        }
    }

    private void addBlockToStructures(Location block, Player builder){
        Structure parentStructure = null;
        for(String structurekey: structures.keySet()){
            Structure structure = structures.get(structurekey);
            if(structure.isBlockConnected(block)){
                if(parentStructure != null){
                    parentStructure.merge(structure);
                    structures.remove(structurekey);
                }
                else{
                    structure.addBlock(block);
                    parentStructure = structure;
                }
            }
        }
        if(parentStructure == null){
            structures.put(nameStructure(block), new Structure(block, builder));
        }
    }

    private String nameStructure(Location block){
        // Look we are just going to assume the first block placed will make up the majority of blocks in the structure
        return String.format("%s Structure %d", block.getBlock().getType().name(),structures.size() );
    }

    public static List<String> getStructures() {
        return structures.keySet().stream().filter(key -> structures.get(key).getSize() > 20).toList();
    }

    public static Structure getStructure(String name){
        return structures.get(name);
    }

    public static void reset(){
        structures = new HashMap<String,Structure>();
    }

    private static void removeBlockFromAllStructures(Location block){
        for(String key: structures.keySet()){
            Structure structure = structures.get(key);
            if(structure.containsBlock(block)){
                structure.removeBlock(block);
                if(structures.size() < 1) structures.remove(key);
            }
        }
    }

    public static String getClosestStructureToPlayer(Player player){
        if(getStructures().isEmpty()) return "";
        int distance = Integer.MAX_VALUE;
        String closest = "";
        Location playerLocation = player.getLocation();
        for(String key : getStructures()){
            int temp = Math.toIntExact(Math.round(playerLocation.distance(structures.get(key).getLocation())));
            if(temp< distance){
                distance = temp;
                closest = key;
            }
        }
        if(distance < 10){
            return String.format("Location: near %s", closest);
        }
        else if(distance < 50){
            return String.format("Location: %d blocks away from %s", closest);
        }
        else{
            return "";
        }
    }
}
