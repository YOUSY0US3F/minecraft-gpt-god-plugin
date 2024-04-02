package net.bigyous.gptgodmc.utils;

import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class Targeter {

    public static Entity getTarget(LivingEntity entity) {
        if (entity == null)
            return null;
        Vector direction = entity.getLocation().getDirection();
        BlockIterator iter = new BlockIterator(entity.getWorld(), entity.getLocation().toVector(),
                direction, entity.getEyeHeight(), 24);
        while(iter.hasNext()){
            Block block = iter.next();
            if(!block.getType().equals(Material.AIR)){
                break;
            }
            Collection<Entity> nearby = block.getLocation().getNearbyEntities(1, 1, 1);
            if(!nearby.isEmpty()){
                if(nearby.size() > 1 || !nearby.contains(entity)){
                    return nearby.stream().filter(e -> !entity.name().equals(e.name())).findFirst().get();
                }
                
            }

        }
        return null;
    }

}