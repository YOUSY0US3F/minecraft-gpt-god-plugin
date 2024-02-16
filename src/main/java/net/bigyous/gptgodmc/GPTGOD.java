package net.bigyous.gptgodmc;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

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
            LOGGER.info("Successfully registered monitor plugin");
        } else {
            LOGGER.info("Failed to register monitor plugin");
        }
        SERVER = this.getServer();
        this.getConfig().addDefault("openAiKey", "");
        this.getConfig().addDefault("language", "en");
        this.saveConfig();

        this.getCommand("try").setExecutor(new DebugCommand());

    }

    @Override
    public void onDisable() {
        if (voicechatPlugin != null) {
            getServer().getServicesManager().unregister(voicechatPlugin);
            LOGGER.info("Successfully unregistered monitor plugin");
        }
    }
}
