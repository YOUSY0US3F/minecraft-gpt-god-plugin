package net.bigyous.gptgodmc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.calledmethods.qual.EnsuresCalledMethods.List;

import net.bigyous.gptgodmc.GPT.GPTModels;
import net.bigyous.gptgodmc.GPT.GptAPI;
import net.bigyous.gptgodmc.GPT.GptActions;
import net.bigyous.gptgodmc.GPT.Json.GptModel;
import net.bigyous.gptgodmc.utils.GPTUtils;

import java.util.ArrayList;

public class GameLoop {
    private static JavaPlugin plugin = JavaPlugin.getPlugin(GPTGOD.class);
    private static FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
    private static GptAPI Action_GPT_API = new GptAPI(GPTModels.getMainModel(), GptActions.GetActionTools());
    private static GptAPI Speech_GPT_API = new GptAPI(GPTModels.getMainModel(), GptActions.GetSpeechTools());
    private static int staticTokens = 0;
    private static int taskId;
    public static boolean isRunning = false;
    private static String SPEECH_PROMPT_TEPLATE = "%s%s, You can now communicate with the players. You must use the Tool calls";
    private static String ACTION_PROMPT_TEMPLATE = "%s Use this information and the tools provided to reward or punish the players.";
    private static ArrayList<String> previousActions = new ArrayList<String>();
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
            Action_GPT_API.addContext(String.format(ACTION_PROMPT_TEMPLATE, prompt), "prompt");
            Speech_GPT_API.addContext(String.format(SPEECH_PROMPT_TEPLATE, prompt, getPreviousActions()), "prompt");
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
        Action_GPT_API = new GptAPI(GPTModels.getMainModel());
        Speech_GPT_API = new GptAPI(GPTModels.getMainModel());
        isRunning = false;
        GPTGOD.LOGGER.info("GameLoop Stoppped");
    }

    public static void logAction(String actionLog){
        previousActions.add(actionLog);
    }

    private static String getPreviousActions(){
        if(previousActions.isEmpty()){
            return "";
        }
        String out = " You Just: " + String.join(",", previousActions);
        previousActions = new ArrayList<String>();
        return out;
    }

    private static class GPTTask implements Runnable{

        @Override
        public void run() {
            GPT_API.addLogs(EventLogger.dump(), "logs");
            Thread worker = new Thread(()->{
                while(EventLogger.isGeneratingSummary() && !EventLogger.hasSummary()){
                    Thread.onSpinWait();
                }
                int nonLogTokens = staticTokens;
                if(EventLogger.hasSummary()) {
                    GPT_API.addLogs(EventLogger.getSummary(), "summary", 1);
                    nonLogTokens += GPTUtils.countTokens(EventLogger.getSummary()) + 1;
                }
                EventLogger.cull(GPT_API.getMaxTokens() - nonLogTokens);
                GPT_API.send();
                Thread.currentThread().interrupt();
            });
            worker.start();

        }
        
    }
}
