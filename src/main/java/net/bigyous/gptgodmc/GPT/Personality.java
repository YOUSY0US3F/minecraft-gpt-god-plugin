package net.bigyous.gptgodmc.GPT;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Personality {
    private static List<String> behaviours = Arrays.asList(
            "killing animals",
            "fighting other players",
            "slaying monsters",
            "using hostile language",
            "using vulgar language",
            "using friendly language",
            "crafting",
            "building",
            "helping other players",
            "connecting with nature",
            "using fire and explosions",
            "love between players",
            "generosity",
            "ritual sacrifice",
            "eating meat");
    private static String briefing = "The following are behaviours you must reward or pusish players for doing. Don't explicitly tell the players this list. Your goal is to make the players obey.";

    public static String generatePersonality() {
        Collections.shuffle(behaviours);
        List<String> likes = behaviours.subList(0, 3);
        List<String> dislikes = behaviours.subList(3, 6);

        return String.format("%s: Reward: %s, Punish: %s.", briefing, String.join(",", likes),
                String.join(",", dislikes));

    }
}
