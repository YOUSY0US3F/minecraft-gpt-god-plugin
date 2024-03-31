package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class RenameItemLoggable extends BaseLoggable implements UserInputLoggable{
    private String item;
    private String newName;
    protected String player;
    protected boolean isValid;
    private ItemStack actualItem;
    public RenameItemLoggable(PrepareAnvilEvent event){
        this.player = event.getView().getPlayer().getName();
        this.item = event.getResult() != null ? event.getResult().getType().toString() : null;
        this.actualItem = event.getResult();
        this.isValid = event.getInventory().getRenameText() != "" && item != null;
        this.newName = event.getInventory().getRenameText();
    }

    @Override
    public String getLog() {
        if(!isValid) return null;

        return String.format("%s renamed their %s to \"%s\"", player, item, newName);
    }

    @Override
    public boolean combine(Loggable l) {
        if(!(l instanceof RenameItemLoggable)) return false;
        RenameItemLoggable other = (RenameItemLoggable) l;
        return this.player.equals(other.player) && isValid;
    }

    @Override
    public void updateUserInput(String input) {
        this.newName = input;
        actualItem.getItemMeta().displayName(null);
    }
}
