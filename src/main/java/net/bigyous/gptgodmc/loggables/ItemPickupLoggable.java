package net.bigyous.gptgodmc.loggables;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

public class ItemPickupLoggable extends BaseLoggable {
    private class Pickup {
        String itemName;
        int amount;

        public Pickup(String itemName, int amount) {
            this.itemName = itemName;
            this.amount = amount;
        }
        public boolean equals(Object o){
            if(!(o instanceof Pickup)) return false;
            Pickup p = (Pickup) o;
            return this.itemName.equals(p.itemName);
        }
        public void incrementAmount(int i){
            this.amount +=i;
        }
        public int getAmount() {
            return amount;
        }
    }

    protected String playerName;
    protected List<Pickup> pickups = new ArrayList<>();

    public ItemPickupLoggable(PlayerAttemptPickupItemEvent event) {
        playerName = event.getPlayer().getName();

        pickups.add(new Pickup(
            event.getItem().getName(),
            event.getItem().getItemStack().getAmount()
        ));
    }

    @Override
    public String getLog() {
        StringBuilder sb = new StringBuilder(playerName + " picked up: ");
        
        for (Pickup p : pickups) {
            sb.append(p.itemName + " x" + p.amount + ", ");
        }

        // Remove trailing comma
        sb.setLength(sb.length() - 2);

        return sb.toString();
    }

    @Override
    public boolean combine(Loggable other) {
        if (!(other instanceof ItemPickupLoggable)) return false;

        ItemPickupLoggable otherPickup = (ItemPickupLoggable) other;

        if (!otherPickup.playerName.equals(this.playerName)) {
            return false;
        }
        Pickup otherItem = otherPickup.pickups.get(0);
        if (pickups.contains(otherItem)){
            pickups.get(pickups.indexOf(otherItem)).incrementAmount(otherItem.getAmount());
        }
        else{
            pickups.add(otherItem);
        }
        return true;
    }
    
}
