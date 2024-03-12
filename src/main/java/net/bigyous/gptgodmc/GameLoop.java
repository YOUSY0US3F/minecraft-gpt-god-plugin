package net.bigyous.gptgodmc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.bigyous.gptgodmc.GPT.GPTModels;
import net.bigyous.gptgodmc.GPT.GptAPI;
import net.bigyous.gptgodmc.GPT.Json.GptModel;
import net.bigyous.gptgodmc.utils.GPTUtils;

public class GameLoop {
    private static JavaPlugin plugin = JavaPlugin.getPlugin(GPTGOD.class);
    private static FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
    private static GptAPI GPT_API = new GptAPI(GPTModels.getMainModel());
    private static int staticTokens = 0;
    private static int taskId;
    public static boolean isRunning = false;
    // converts seconds into ticks
    private static long seconds(long seconds){
        return seconds * 20;
    }
    public static void init(){
        if(isRunning || !config.getBoolean("enabled")) return;
        BukkitTask task = GPTGOD.SERVER.getScheduler().runTaskTimer(plugin, new GPTTask(), seconds(30), seconds(40));
        taskId = task.getTaskId();
        if(config.contains("prompt") && !config.getString("prompt").isBlank()){
            String prompt = config.getString("prompt");
            GPT_API.addContext(prompt, "prompt");
            // the roles system and user are each one token so we add two to this number
            staticTokens = GPTUtils.countTokens(prompt) + 2;
        }
        else{
            GPTGOD.LOGGER.error("no prompt set in config file, the plugin wont work as intended!");
        }
        isRunning = true;
        GPTGOD.LOGGER.info("GameLoop Started, the minecraft god has awoken");
    }
    public static void stop(){
        if(!isRunning) return;
        GPTGOD.SERVER.getScheduler().cancelTask(taskId);
        GPT_API = new GptAPI(GPTModels.getMainModel());
        isRunning = false;
        GPTGOD.LOGGER.info("GameLoop Stoppped");
    }

    private static class GPTTask implements Runnable{

        @Override
        public void run() {
            EventLogger.cull(GPT_API.getMaxTokens() - staticTokens);
            GPT_API.addLogs(EventLogger.dump(), "logs");
            GPT_API.send();
        }
        
    }
}
