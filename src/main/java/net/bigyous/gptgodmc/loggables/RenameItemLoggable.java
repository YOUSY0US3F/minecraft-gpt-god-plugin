package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.inventory.PrepareAnvilEvent;

public class RenameItemLoggable extends BaseLoggable{
    private String item;
    private String newName;
    protected String player;
    protected boolean isValid;
    public RenameItemLoggable(PrepareAnvilEvent event){
        this.player = event.getView().getPlayer().getName();
        this.item = event.getResult() != null ? event.getResult().getType().name() : null;
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
}
