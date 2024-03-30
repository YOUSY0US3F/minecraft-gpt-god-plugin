package net.bigyous.gptgodmc.loggables;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class RenameEntityEvent extends BaseLoggable {
    private String name;
    private String entity;
    private String player;
    private boolean isValid;

    public RenameEntityEvent(PlayerInteractEntityEvent event){
        this.player = event.getPlayer().getName();
        ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        this.isValid = item.getType().equals(Material.NAME_TAG) && item.hasItemMeta() && item.getItemMeta().hasDisplayName();
        this.name = isValid ? PlainTextComponentSerializer.plainText().serialize(item.getItemMeta().displayName()) : null;
        this.entity = event.getRightClicked().getName();
    }

    @Override
    public String getLog() {
        if(!isValid) return null;
        return String.format("%s named a %s \"%s\"", player, entity, name);
    }   
    
}
