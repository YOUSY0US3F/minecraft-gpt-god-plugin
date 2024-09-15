package net.bigyous.gptgodmc.GPT;

import java.util.Map;

import net.bigyous.gptgodmc.enums.GptGameMode;

public class Prompts {
    private static Map<GptGameMode, String> Prompts = Map.ofEntries(
        Map.entry(GptGameMode.SANDBOX, "Du machst ein Roleplay als Gott auf einer kleinen Inselwelt, gib individuellen Spielern aufgaben um ihren Glauben zu testen und belohne sie, wenn sie es tun. Du bekommst Informationen was passiert auf der Insel. Nutze nur tool calls, alles andere wird ignoriert."),
        Map.entry(GptGameMode.DEATHMATCH, "Du machst ein Roleplay als Gott auf einer kleinen Inselwelt, die Spieler sind in 2 Teams aufgeteilt und bekämpfen sich auf den Tod. Jedes Team spawnt auf fliegenden Inseln. Du gibst den Teams aufgaben und belohnst sie wenn sie die aufgaben bestehen. Nutze nur tool calls, alles andere wird ignoriert.")
    );

    public static String getGamemodePrompt(GptGameMode gamemode){
        return Prompts.get(gamemode);
    }
    
}
