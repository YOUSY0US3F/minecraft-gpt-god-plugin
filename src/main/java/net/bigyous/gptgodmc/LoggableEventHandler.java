package net.bigyous.gptgodmc;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.Listener;
import net.bigyous.gptgodmc.loggables.AttackLoggable;
import net.bigyous.gptgodmc.loggables.DamageLoggable;
import net.bigyous.gptgodmc.loggables.ItemPickupLoggable;
import net.bigyous.gptgodmc.loggables.Loggable;
import net.kyori.adventure.text.TextComponent;
import net.bigyous.gptgodmc.loggables.FishingLoggable;
import net.bigyous.gptgodmc.loggables.DropItemLoggable;
import net.bigyous.gptgodmc.loggables.DeathLoggable;
import net.bigyous.gptgodmc.loggables.EatingLoggable;
import net.bigyous.gptgodmc.loggables.ChatLoggable;
import net.bigyous.gptgodmc.loggables.InteractLoggable;
import io.papermc.paper.event.player.AsyncChatEvent;

import java.util.Comparator;

import org.bukkit.event.EventHandler;
import net.bigyous.gptgodmc.EventLogger;

public class LoggableEventHandler implements Listener {
    //private EventLogger eventLogger;

    @EventHandler
    public static void pickupItem(PlayerAttemptPickupItemEvent event) {
        //EventLogger.addEvent(event);
        EventLogger.addLoggable(
            new ItemPickupLoggable(event)
        );
    }

    @EventHandler
    public static void onChat(AsyncChatEvent event) {

        EventLogger.addLoggable(new ChatLoggable(event.getPlayer().getName(), ((TextComponent)event.message()).content()));
        // dbg: dump logs
        GPTGOD.LOGGER.info("=== DUMPED LOGS: ===");
        GPTGOD.LOGGER.info(EventLogger.dump());
        GPTGOD.LOGGER.info("====================");
    }

    @EventHandler
    public static void onAttackEntity(EntityDamageByEntityEvent event) {
        EventLogger.addLoggable(
            new AttackLoggable(event)
        );
    }

    @EventHandler
    public static void onDamage(EntityDamageEvent event) {
        EventLogger.addLoggable(
            new DamageLoggable(event)
        );
    }

    @EventHandler
    public static void onDrop(PlayerDropItemEvent event) {
        EventLogger.addLoggable(
            new DropItemLoggable(event)
        );
    }
    @EventHandler
    public static void onEat(PlayerItemConsumeEvent event){
        EventLogger.addLoggable(
            new EatingLoggable(event)
        );
    }
    // @EventHandler
    // public static void onEntityInteract(PlayerInteractEvent event){
    //     EventLogger.addLoggable(
    //         new InteractLoggable(event)
    //     );
    // }
    @EventHandler
    public static void onDeath(PlayerDeathEvent event){
        EventLogger.addLoggable(
            new DeathLoggable(event)
        );
    }
    @EventHandler
    public static void onItemFished(PlayerFishEvent event){
        EventLogger.addLoggable(
            new FishingLoggable(event)
        );
    }
}
