package net.bigyous.gptgodmc;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.bigyous.gptgodmc.enums.GptGameMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
public class RoundSystem implements Listener {
    JavaPlugin plugin = JavaPlugin.getPlugin(GPTGOD.class);
    FileConfiguration config = plugin.getConfig();

    private static Vector RED_SPAWN = new Vector(-1.499, 63, 1.713);
    private static Vector BLUE_SPAWN = new Vector(32, 63, 1.713);

    public static void addPlayerToTeam(Player player){
        if(GPTGOD.RED_TEAM.getSize() < GPTGOD.BLUE_TEAM.getSize()){
            GPTGOD.RED_TEAM.addPlayer(player);
            player.setRespawnLocation(RED_SPAWN.toLocation(WorldManager.getCurrentWorld()), true);
            player.displayName(Component.text(player.getName()).color(NamedTextColor.RED));
        }
        else{
            GPTGOD.BLUE_TEAM.addPlayer(player);
            player.setRespawnLocation(BLUE_SPAWN.toLocation(WorldManager.getCurrentWorld()), true);
            player.displayName(Component.text(player.getName()).color(NamedTextColor.BLUE));
        }
        player.teleport(player.getRespawnLocation());
    }
    public static void removePlayerFromTeam(Player player){
        GPTGOD.SCOREBOARD.getPlayerTeam(player).removePlayer(player);
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!config.getBoolean("Rounds") || config.getString("startingWorld").isBlank()) return;
        Player player = event.getPlayer();
        Server server = player.getServer();
        player.setGameMode(GameMode.SPECTATOR);
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.getLocation().getBlock().setType(Material.PLAYER_HEAD);
            Skull playerSkull = (Skull) player.getLocation().getBlock().getState();
            playerSkull.setOwningPlayer(player);
            playerSkull.update(true);
        });

        if (GPTGOD.gameMode.equals(GptGameMode.DEATHMATCH)){
            long living_red =  GPTGOD.RED_TEAM.getEntries().stream().filter((String name) -> (server.getPlayer(name) != null && !server.getPlayer(name).getGameMode().equals(GameMode.SPECTATOR))).count();
            long living_blue =  GPTGOD.BLUE_TEAM.getEntries().stream().filter((String name) -> (server.getPlayer(name) != null && !server.getPlayer(name).getGameMode().equals(GameMode.SPECTATOR))).count();
            Title title = living_red < 1 && living_blue < 1 ? Title.title(Component.text("NO ONE WINS").color(NamedTextColor.YELLOW), Component.text("Your death was is vain.").color(NamedTextColor.RED)) :
                living_red < 1 ? Title.title(Component.text("BLUE WINS").color(NamedTextColor.BLUE), Component.text(String.format("%d players remaining", living_blue))): 
                living_blue < 1 ? Title.title(Component.text("RED WINS").color(NamedTextColor.RED), Component.text(String.format("%d players remaining", living_red))) : null;
            if(title != null) {
                server.showTitle(title);
                Bukkit.getScheduler().runTaskLater(plugin, () -> reset(), 5);
                return;
            }
        }

       for(Player p : server.getOnlinePlayers()){
            if(!p.getGameMode().equals(GameMode.SPECTATOR)){
                return;
            }
       }

       reset();

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.getPlayer().setGameMode(GameMode.SURVIVAL);
        WorldManager.teleportPlayer(event.getPlayer());
        if(GPTGOD.gameMode.equals(GptGameMode.DEATHMATCH)){
            addPlayerToTeam(event.getPlayer());
        }   
    }
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        removePlayerFromTeam(event.getPlayer());
    }

    public static void reset(){
        GameLoop.stop();
        WorldManager.resetCurrentMap();
        StructureManager.reset();
        EventLogger.reset();
        List<Player> reorderedPlayers = List.copyOf(GPTGOD.SERVER.getOnlinePlayers());
        Collections.shuffle(reorderedPlayers);
        GPTGOD.RED_TEAM.removeEntries(GPTGOD.RED_TEAM.getEntries());
        GPTGOD.BLUE_TEAM.removeEntries(GPTGOD.BLUE_TEAM.getEntries());
        for(Player p : GPTGOD.SERVER.getOnlinePlayers()){
            revivePlayer(p);
            if(GPTGOD.gameMode.equals(GptGameMode.DEATHMATCH)){
                addPlayerToTeam(p);
            }   
        }
        GameLoop.init();
    }

    public static void revivePlayer(Player player){
            WorldManager.teleportPlayer(player);
            player.setGameMode(GameMode.SURVIVAL);
    }
}
