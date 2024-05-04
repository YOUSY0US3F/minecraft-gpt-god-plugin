package net.bigyous.gptgodmc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.bigyous.gptgodmc.GPT.GPTModels;
import net.bigyous.gptgodmc.GPT.GptAPI;
import net.bigyous.gptgodmc.GPT.GptActions;
import net.bigyous.gptgodmc.GPT.Personality;
import net.bigyous.gptgodmc.GPT.Prompts;
import net.bigyous.gptgodmc.GPT.Json.GptTool;
import net.bigyous.gptgodmc.utils.GPTUtils;

import java.util.ArrayList;

public class GameLoop {
    private static JavaPlugin plugin = JavaPlugin.getPlugin(GPTGOD.class);
    private static FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
    private static GptAPI Action_GPT_API;
    private static GptAPI Speech_GPT_API;
    private static int staticTokens = 0;
    private static int taskId;
    public static boolean isRunning = false;
    private static String PROMPT;
    private static String SPEECH_PROMPT_TEPLATE = "%s%s, You can now communicate with the players. Use the server history as a reference to note the change in player behavior %s";
    private static String ACTION_PROMPT_TEMPLATE = "%s Use this information and the tools provided to reward or punish the players. Only react to events listed under Current, Use the server history as a reference to note the change in player behavior %s";
    private static ArrayList<String> previousActions = new ArrayList<String>();
    private static String personality;

    // converts seconds into ticks
    private static long seconds(long seconds) {
        return seconds * 20;
    }

    public static void init() {
        if (isRunning || !config.getBoolean("enabled"))
            return;
        Action_GPT_API = new GptAPI(GPTModels.getMainModel(), GptActions.GetActionTools());
        Speech_GPT_API = new GptAPI(GPTModels.getMainModel(), GptActions.GetSpeechTools());
        BukkitTask task = GPTGOD.SERVER.getScheduler().runTaskTimerAsynchronously(plugin, new GPTTask(), seconds(30),
                seconds(40));
        taskId = task.getTaskId();
        personality = Personality.generatePersonality();
        PROMPT = Prompts.getGamemodePrompt(GPTGOD.gameMode);
        String actionPrompt = String.format(ACTION_PROMPT_TEMPLATE, PROMPT, personality);
        Action_GPT_API.addContext(actionPrompt, "prompt");

        // the roles system and user are each one token so we add two to this number
        staticTokens = GPTUtils.countTokens(actionPrompt) + 2;
        isRunning = true;
        GPTGOD.LOGGER.info("GameLoop Started, the minecraft god has awoken");
    }

    public static void stop() {
        if (!isRunning)
            return;
        GPTGOD.SERVER.getScheduler().cancelTask(taskId);
        EventLogger.reset();
        Action_GPT_API = null;
        Speech_GPT_API = null;
        isRunning = false;
        GPTGOD.LOGGER.info("GameLoop Stoppped");
    }

    public static void logAction(String actionLog) {
        previousActions.add(actionLog);
    }

    private static String getPreviousActions() {
        if (previousActions.isEmpty()) {
            return "";
        }
        String out = " You Just: " + String.join(",", previousActions);
        previousActions = new ArrayList<String>();
        return out;
    }

    private static void sendSpeechActions() {
        Speech_GPT_API.addContext(String.format(SPEECH_PROMPT_TEPLATE, PROMPT, getPreviousActions(), personality),
                "prompt", 0);
        if(EventLogger.hasSummary()){
            Speech_GPT_API.addLogs("Server History: " + EventLogger.getSummary(), "summary", 1);
        }
        Speech_GPT_API.send();
    }

    private static class GPTTask implements Runnable {

        @Override
        public void run() {
            while (EventLogger.isGeneratingSummary() && !EventLogger.hasSummary()) {
                Thread.onSpinWait();
            }
            int nonLogTokens = staticTokens;
            if (EventLogger.hasSummary()) {
                Action_GPT_API.addLogs("Server History: " + EventLogger.getSummary(), "summary", 1);
                nonLogTokens += GPTUtils.countTokens(EventLogger.getSummary()) + 1;
            }
            GptTool[] actionTools = GptActions.GetActionTools();
            Action_GPT_API.setTools(actionTools);
            nonLogTokens += GPTUtils.calculateToolTokens(actionTools);
            EventLogger.cull(Action_GPT_API.getMaxTokens() - nonLogTokens);
            String log = EventLogger.dump();
            Action_GPT_API.addLogs("Current: " + log, "log");
            Speech_GPT_API.addLogs("Current: " + log, "log");
            previousActions = new ArrayList<>();
            Action_GPT_API.send();
            while (Action_GPT_API.isSending()) {
                Thread.onSpinWait();
            }
            sendSpeechActions();
            Thread.currentThread().interrupt();

        }

    }
}
