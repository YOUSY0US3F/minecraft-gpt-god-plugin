package net.bigyous.gptgodmc;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.annotation.Nullable;
import net.bigyous.gptgodmc.utils.DebugCommand;

public final class GPTGOD extends JavaPlugin {

    public static final String PLUGIN_ID = "example_plugin";
    public static final Logger LOGGER = LogManager.getLogger(PLUGIN_ID);
    public static Server SERVER;

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
        Path worlds = getDataFolder().toPath().resolve("worlds");
        if(getConfig().getString("startingWorld").isBlank()|| !getConfig().getBoolean("Rounds")){
            String message = getConfig().getBoolean("Rounds")?
            "can't use Round system since startingWorld is not set. Go to %s to fix this.":
            "Round System disabled be warned, this is not the intended way to use gptgodmc. Go to %s to fix this";
            LOGGER.warn(String.format(message, this.getDataFolder().getPath() + "\\config.yml"));
        }
        else{
            if(WorldManager.loadMap(getConfig().getString("startingWorld"))){
                SERVER.getPluginManager().registerEvents(new RoundSystem(), this);
            }
            
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
}
