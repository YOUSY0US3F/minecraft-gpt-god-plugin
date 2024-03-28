package net.bigyous.gptgodmc.loggables;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftLoggable extends BaseLoggable {
    protected String playerName;
    protected String item;
    protected int count;
    public CraftLoggable(CraftItemEvent event){
        HumanEntity crafter = event.getView().getPlayer();
        this.playerName = crafter.getName();
        this.count = event.getCurrentItem().getAmount();
        this.item = event.getCurrentItem().getType().name();
    }

    @Override
    public String getLog() {
        return String.format("%s crafted %d %s", playerName, count, item);
    }

    @Override
    public boolean combine(Loggable l) {
        if(!(l instanceof CraftLoggable)) return false;
        CraftLoggable other = (CraftLoggable) l;
        if(other.playerName.equals(this.playerName) && other.item.equals(this.item)){
            this.count += other.count;
            return true;
        }
        return false;
    }
}
