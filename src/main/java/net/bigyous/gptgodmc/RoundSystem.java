package net.bigyous.gptgodmc;

import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.bigyous.gptgodmc.enums.GptGameMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
public class RoundSystem implements Listener {
    FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!config.getBoolean("Rounds") || config.getString("startingWorld").isBlank()) return;
        Player player = event.getPlayer();
        Server server = player.getServer();
        player.setGameMode(GameMode.SPECTATOR);

        if (GPTGOD.gameMode.equals(GptGameMode.DEATHMATCH)){
            long living_red =  GPTGOD.RED_TEAM.getEntries().stream().filter((String name) -> server.getPlayer(name) != null && !server.getPlayer(name).getGameMode().equals(GameMode.SPECTATOR)).count();
            long living_blue =  GPTGOD.BLUE_TEAM.getEntries().stream().filter((String name) -> server.getPlayer(name) != null && !server.getPlayer(name).getGameMode().equals(GameMode.SPECTATOR)).count();
            Title title = living_red < 0 ? Title.title(Component.text("BLUE WINS").color(NamedTextColor.BLUE), Component.text(String.format("%d players remaining", living_blue))): 
                living_blue < 0 ? Title.title(Component.text("RED WINS").color(NamedTextColor.RED), Component.text(String.format("%d players remaining", living_red))) : null;
            if(title != null) {
                server.showTitle(title);
                reset();
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
        WorldManager.teleportPlayer(event.getPlayer());
    }

    public static void reset(){
        GameLoop.stop();
        WorldManager.resetCurrentMap();
        StructureManager.reset();
        EventLogger.reset();
        for(Player p : GPTGOD.SERVER.getOnlinePlayers()){
            revivePlayer(p);
        }
        GameLoop.init();
    }

    public static void revivePlayer(Player player){
            WorldManager.teleportPlayer(player);
            player.setGameMode(GameMode.SURVIVAL);
    }
}
