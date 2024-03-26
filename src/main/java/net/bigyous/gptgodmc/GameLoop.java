package net.bigyous.gptgodmc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
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
    private static String PROMPT;
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
            PROMPT= config.getString("prompt");
            Action_GPT_API.addContext(String.format(ACTION_PROMPT_TEMPLATE, PROMPT), "prompt");
            
            // the roles system and user are each one token so we add two to this number
            staticTokens = GPTUtils.countTokens(PROMPT) + 2;
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
        String out = " You Just did these actions: " + String.join(",", previousActions);
        previousActions = new ArrayList<String>();
        return out;
    }

    private static void sendSpeechActions(){
        Speech_GPT_API.addContext(String.format(SPEECH_PROMPT_TEPLATE, PROMPT, getPreviousActions()), "prompt");
        Speech_GPT_API.send();
    }

    private static class GPTTask implements Runnable{

        @Override
        public void run() {
            Thread worker = new Thread(()->{
                while(EventLogger.isGeneratingSummary() && !EventLogger.hasSummary()){
                    Thread.onSpinWait();
                }
                int nonLogTokens = staticTokens;
                if(EventLogger.hasSummary()) {
                    Action_GPT_API.addLogs(EventLogger.getSummary(), "summary", 1);
                    Speech_GPT_API.addLogs(EventLogger.getSummary(), "summary", 1);
                    nonLogTokens += GPTUtils.countTokens(EventLogger.getSummary()) + 1;
                }
                EventLogger.cull(Action_GPT_API.getMaxTokens() - nonLogTokens);
                String log = EventLogger.dump();
                Action_GPT_API.addLogs(log, "log");
                Speech_GPT_API.addLogs(log, "log");
                previousActions = new ArrayList<>();
                Action_GPT_API.send();
                while(Action_GPT_API.isSending()){
                    Thread.onSpinWait();
                }
                sendSpeechActions();
                Thread.currentThread().interrupt();
            });
            worker.start();

        }
        
    }
}
