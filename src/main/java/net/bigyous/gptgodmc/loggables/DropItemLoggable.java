package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.player.PlayerDropItemEvent;
import java.util.ArrayList;
import java.util.List;

public class DropItemLoggable extends BaseLoggable {
    private class Drop {
        String itemName;
        int amount;

        public Drop(String itemName, int amount){
            this.itemName = itemName;
            this.amount = amount;
        }
        public boolean equals(Object o){
            if(!(o instanceof Drop)) return false;
            Drop d = (Drop) o;
            return this.itemName.equals(d.itemName);
        }
        public void incrementAmount(int i){
            this.amount +=i;
        }
        public int getAmount() {
            return amount;
        }
    }
    protected String playerName;
    protected List<Drop> drops = new ArrayList<Drop>();

    public DropItemLoggable(PlayerDropItemEvent event){
        playerName = event.getPlayer().getName();
        drops.add(new Drop(
            event.getItemDrop().getName(), 
            event.getItemDrop().getItemStack().getAmount()
        ));   
    }

    @Override
    public String getLog(){
        StringBuilder log = new StringBuilder(playerName + " dropped: ");

        for (Drop d : drops){
            log.append(d.itemName + " x" + d.amount + ", ");
        }

        //Remove trailing comma
        log.setLength(log.length() - 2);

        return log.toString();
    }

    @Override
    public boolean combine(Loggable other) {
        if (!(other instanceof DropItemLoggable)) return false;

        DropItemLoggable otherDrop = (DropItemLoggable) other;

        if (!otherDrop.playerName.equals(this.playerName)) {
            return false;
        }
        Drop otherItem = otherDrop.drops.get(0);
        if (drops.contains(otherItem)){
            drops.get(drops.indexOf(otherItem)).incrementAmount(otherItem.getAmount());
        }
        else{
            drops.add(otherItem);
        }
        return true;
    }


}
