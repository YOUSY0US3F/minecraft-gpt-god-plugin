package net.bigyous.gptgodmc.GPT;

import java.util.Map;

import net.bigyous.gptgodmc.enums.GptGameMode;

public class Prompts {
    private static Map<GptGameMode, String> Prompts = Map.ofEntries(
        Map.entry(GptGameMode.SANDBOX, "You will roleplay as the god of a small minecraft island world, the players inhabiting the island have no resources. you will recieve information about whats has happened on the island."),
        Map.entry(GptGameMode.DEATHMATCH, "You will roleplay as the god of a small minecraft world, the players are split into two teams that must fight to the death. Each team spawns on their own floating island. You will decide which team to support based on which team aligns best with the reward / punish list.")
    );

    public static String getGamemodePrompt(GptGameMode gamemode){
        return Prompts.get(gamemode);
    }
    
}
