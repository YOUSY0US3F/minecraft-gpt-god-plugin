package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.inventory.PrepareAnvilEvent;

public class RenameItemLoggable extends BaseLoggable{
    private String item;
    private String newName;
    private String player;
    private boolean isValid;
    public RenameItemLoggable(PrepareAnvilEvent event){
        this.player = event.getView().getPlayer().getName();
        this.item = event.getResult().getType().name();
        this.isValid = event.getInventory().getRenameText() != "";
        this.newName = event.getInventory().getRenameText();
    }

    @Override
    public String getLog() {
        if(!isValid) return null;

        return String.format("%s renamed their %s to \"%s\"", player, item, newName);
    }
}
