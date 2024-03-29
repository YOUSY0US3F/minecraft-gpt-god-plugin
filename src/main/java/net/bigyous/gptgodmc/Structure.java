package net.bigyous.gptgodmc;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Structure {
    private HashSet<Vector> blocks;
    private Player builder;
    private Location location;
    private World world;
    public Structure(Location block, Player builder){
        this.blocks = new HashSet<Vector>();
        this.addBlock(block);
        this.builder = builder;
        this.location = null;
        this.world = block.getWorld();
    }
    public Player getBuilder() {
        return builder;
    }

    public boolean containsBlock(Location block){
       return blocks.contains(block.toVector());
    }
    
    public void removeBlock(Location block){
        blocks.remove(block.toVector());
    }

    public void addBlock(Location block){
        blocks.add(block.toVector());
        if(location != null) location = null;
        if(world == null) world = block.getWorld();
    }

    private Location calculateCentroid(){
        double x = 0;
        double y = 0;
        double z = 0;
        int size = blocks.size();
        for(Vector location: blocks){
            x += location.getX();
            y += location.getY();
            z += location.getZ();
        }
        return new Location(world, x/size, y/size, z/size);
    }

    public Location getLocation(){
        if(location == null) location = calculateCentroid();
        return location;
    }

    public boolean isBlockConnected(Location block){
        if(world == null) return false;
        int[] directions = {-1, 0, 1};
        for(int xDirection : directions){
            for(int yDirection: directions){
                for(int zDirection: directions){
                    Vector location = new Vector(block.getX() + xDirection, block.getY() + yDirection, block.getZ() + zDirection);
                    if(blocks.contains(location)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Block> getBlocks(){
        return blocks.stream().map((Vector vector) -> {return WorldManager.getCurrentWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());}).toList();
    }

    public HashSet<Vector> getVectors(){
        return this.blocks;
    }

    public void merge(Structure other){
        blocks.addAll(other.getVectors());
    }

    public int getSize(){
        return blocks.size();
    }

    @Override
    public String toString() {
        return String.format("Structure: Location: %s Size: %d Builder: %s", this.getLocation().toVector().toString(), this.getSize(), this.getBuilder().getName());
    }
}
