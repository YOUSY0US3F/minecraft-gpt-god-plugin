package net.bigyous.gptgodmc;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.nio.file.Path;

import javax.annotation.Nullable;

import net.bigyous.gptgodmc.enums.GptGameMode;
import net.bigyous.gptgodmc.utils.DebugCommand;
import net.bigyous.gptgodmc.utils.NicknameCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class GPTGOD extends JavaPlugin {

    public static final String PLUGIN_ID = "example_plugin";
    public static final Logger LOGGER = LogManager.getLogger(PLUGIN_ID);
    public static Server SERVER;
    public static GptGameMode gameMode;
    public static Scoreboard SCOREBOARD;
    public static Team RED_TEAM;
    public static Team BLUE_TEAM;
    public static Objective GPT_OBJECTIVES;

    @Nullable
    private VoiceMonitorPlugin voicechatPlugin;

    @Override
    public void onEnable() {
        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            voicechatPlugin = new VoiceMonitorPlugin();
            service.registerPlugin(voicechatPlugin);
            LOGGER.info("Successfully registered gpt monitor plugin");
        } else {
            LOGGER.info("Failed to register gpt monitor plugin");
        }
        SERVER = getServer();
        // getConfig().addDefault("openAiKey", "");
        // getConfig().addDefault("language", "en");
        this.saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        getCommand("try").setExecutor(new DebugCommand());
        getCommand("nickname").setExecutor(new NicknameCommand());
        Path worlds = getDataFolder().toPath().resolve("worlds");
        if (getConfig().getString("startingWorld").isBlank() || !getConfig().getBoolean("Rounds")) {
            String message = getConfig().getBoolean("Rounds")
                    ? "can't use Round system since startingWorld is not set. Go to %s to fix this."
                    : "Round System disabled be warned, this is not the intended way to use gptgodmc. Go to %s to fix this";
            LOGGER.warn(String.format(message, this.getDataFolder().getPath() + "\\config.yml"));
        } else {
            if (WorldManager.loadMap(getConfig().getString("startingWorld"))) {
                SERVER.getPluginManager().registerEvents(new RoundSystem(), this);
            }

        }
        gameMode = GptGameMode.valueOf(getConfig().getString("gamemode"));
        SERVER.getPluginManager().registerEvents(new LoggableEventHandler(), this);
        SERVER.getPluginManager().registerEvents(new StartGameLoop(), this);
        SERVER.getPluginManager().registerEvents(new StructureManager(), this);

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        SCOREBOARD = manager.getNewScoreboard();
        GPT_OBJECTIVES = SCOREBOARD.registerNewObjective("gpt", Criteria.DUMMY, Component.text("Holy Objectives:").color(NamedTextColor.YELLOW));
        if(gameMode.equals(GptGameMode.DEATHMATCH)){
            RED_TEAM = SCOREBOARD.registerNewTeam("Red");
            BLUE_TEAM = SCOREBOARD.registerNewTeam("Blue");
            RED_TEAM.color(NamedTextColor.RED);
            BLUE_TEAM.color(NamedTextColor.BLUE);
            Component redDisplay = Component.text("Red Team").color(NamedTextColor.RED);
            Component blueDisplay = Component.text("Blue Team").color(NamedTextColor.BLUE);
            RED_TEAM.prefix(redDisplay);
            BLUE_TEAM.prefix(blueDisplay);
            RED_TEAM.displayName(redDisplay);
            BLUE_TEAM.displayName(blueDisplay);
        }
    }

    @Override
    public void onDisable() {
        WorldManager.unload();
        if (voicechatPlugin != null) {
            getServer().getServicesManager().unregister(voicechatPlugin);
            LOGGER.info("Successfully unregistered monitor plugin");
        }
    }

    private static class StartGameLoop implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            GameLoop.init();
            event.getPlayer().setScoreboard(SCOREBOARD);

        }

        @EventHandler
        public void onPlayerDisconnect(PlayerQuitEvent event) {
            GPTGOD.SERVER.getScheduler().runTaskLater(JavaPlugin.getPlugin(GPTGOD.class), new StopGPT(), 20);
        }

        private static class StopGPT implements Runnable {

            @Override
            public void run() {
                GPTGOD.LOGGER.info("All players Left, stopping gameLoop");
                GameLoop.stop();
            }

        }
    }
}
