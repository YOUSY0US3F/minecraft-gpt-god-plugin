package net.bigyous.gptgodmc.GPT;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.enums.GptGameMode;

public class Prompts {
    private static FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
    private static Map<GptGameMode, String> Prompts = Map.ofEntries(
        Map.entry(GptGameMode.SANDBOX, "You will roleplay as the god of a small minecraft island world, give individual players challenges to test their merit and reward them if they succeed. You will recieve information about what has happened on the island. Only use tool calls, other responses will be ignored."),
        Map.entry(GptGameMode.DEATHMATCH, "You will roleplay as the god of a small minecraft world, the players are split into two teams that must fight to the death. Each team spawns on their own floating island. You will give the teams challenges to complete and reward the teams that succeed. Only use tool calls, other responses will be ignored.")
    );

    public static String getGamemodePrompt(GptGameMode gamemode){
        if(config.isSet("promptOverride") && !config.getString("promptOverride").isBlank()){
            return config.getString("promptOverride");
        }
        return Prompts.get(gamemode);
    }
    
}
