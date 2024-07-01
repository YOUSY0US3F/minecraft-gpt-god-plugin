package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.bigyous.gptgodmc.GPT.Moderation;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class RenameItemLoggable extends BaseLoggable implements UserInputLoggable{
    private String item;
    protected String newName;
    protected String player;
    protected boolean isValid = false;
    private ItemStack actualItem;
    public RenameItemLoggable(InventoryClickEvent event){
        this.player = event.getView().getPlayer().getName();
        if(event.getInventory() instanceof AnvilInventory){
            if(event.getRawSlot() == 2){
                this.actualItem = event.getCurrentItem();
                this.newName = actualItem != null && actualItem.hasItemMeta() ? 
                PlainTextComponentSerializer.plainText().serialize(actualItem.getItemMeta().displayName())
                : null;
                if(newName != null){
                    this.item = actualItem.getType().toString();
                    this.isValid = true;
                    // Moderation.moderateUserInput(newName, this);
                }
            }
        }
    }

    @Override
    public String getLog() {
        if(!isValid) return null;

        return String.format("%s renamed their %s to \"%s\"", player, item, newName);
    }

    // @Override
    // public boolean combine(Loggable l) {
    //     if(!(l instanceof RenameItemLoggable)) return false;
    //     RenameItemLoggable other = (RenameItemLoggable) l;
    //     if(this.player.equals(other.player) && isValid){
    //         this.newName = other.newName;
    //         return true;
    //     }
    //     return false;
    // }

    @Override
    public void updateUserInput(String input) {
        this.newName = input;
        actualItem.setItemMeta(null);
    }
}
