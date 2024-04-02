package net.bigyous.gptgodmc;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.Listener;

import net.bigyous.gptgodmc.loggables.AchievementLoggable;
import net.bigyous.gptgodmc.loggables.AttackLoggable;
import net.bigyous.gptgodmc.loggables.DamageLoggable;
import net.bigyous.gptgodmc.loggables.ItemPickupLoggable;
import net.bigyous.gptgodmc.loggables.MountLoggable;
import net.bigyous.gptgodmc.loggables.RenameEntityEvent;
import net.bigyous.gptgodmc.loggables.RenameItemLoggable;
import net.bigyous.gptgodmc.loggables.ShootLoggable;
import net.bigyous.gptgodmc.loggables.SleepTogetherLoggable;
import net.bigyous.gptgodmc.loggables.SpecialBlockPlaceEventLoggable;
import net.bigyous.gptgodmc.loggables.TameAnimalLoggable;
import net.bigyous.gptgodmc.loggables.UseLoggable;
import net.bigyous.gptgodmc.loggables.WriteOnSignLoggable;
import net.kyori.adventure.text.TextComponent;
import net.bigyous.gptgodmc.loggables.FishingLoggable;
import net.bigyous.gptgodmc.loggables.DropItemLoggable;
import net.bigyous.gptgodmc.loggables.DeathLoggable;
import net.bigyous.gptgodmc.loggables.EatingLoggable;
import net.bigyous.gptgodmc.loggables.ExplosionLoggable;
import net.bigyous.gptgodmc.loggables.ChatLoggable;
import net.bigyous.gptgodmc.loggables.CombustLoggable;
import net.bigyous.gptgodmc.loggables.CraftLoggable;
import io.papermc.paper.event.player.AsyncChatEvent;

import org.bukkit.event.EventHandler;

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
        GPTGOD.LOGGER.info(EventLogger.debugOut());
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

    @EventHandler
    public static void onBlockPlaced(BlockPlaceEvent event){
        EventLogger.addLoggable(
            new SpecialBlockPlaceEventLoggable(event)
        );
    }

    @EventHandler
    public static void onSignChange(SignChangeEvent event){
        EventLogger.addLoggable(new WriteOnSignLoggable(event));
    }

    @EventHandler
    public static void onExplosion(BlockExplodeEvent event){
        EventLogger.addLoggable(new ExplosionLoggable(event));
    }

    @EventHandler
    public static void onSleep(PlayerBedEnterEvent event){
        EventLogger.addLoggable(new SleepTogetherLoggable(event));
    }

    @EventHandler
    public static void onUse(PlayerInteractEvent event){
        EventLogger.addLoggable(new UseLoggable(event));
    }

    @EventHandler
    public static void onBlockExplosion(EntityExplodeEvent event){
        EventLogger.addLoggable(new ExplosionLoggable(event));
    }

    @EventHandler
    public static void onCombust(EntityCombustByEntityEvent event){
        EventLogger.addLoggable(new CombustLoggable(event));
    }

    @EventHandler
    public static void onMount(EntityMountEvent event){
        EventLogger.addLoggable(new MountLoggable(event));
    }

    @EventHandler
    public static void onCraft(CraftItemEvent event){
        EventLogger.addLoggable(new CraftLoggable(event));
    }

    @EventHandler
    public static void onAchievement(PlayerAdvancementDoneEvent event){
        EventLogger.addLoggable(new AchievementLoggable(event));
    }

    @EventHandler
    public static void onItemNamed(InventoryClickEvent event){
        EventLogger.addLoggable(new RenameItemLoggable(event));
    }

    @EventHandler
    public static void onEntityRename(PlayerInteractEntityEvent event){
        EventLogger.addLoggable(new RenameEntityEvent(event));
    }

    @EventHandler
    public static void onShoot(EntityShootBowEvent event){
        EventLogger.addLoggable(new ShootLoggable(event));
    }

    @EventHandler
    public static void onTame(EntityTameEvent event){
        EventLogger.addLoggable(new TameAnimalLoggable(event));
    }
}
