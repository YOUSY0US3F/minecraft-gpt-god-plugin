package net.bigyous.gptgodmc.GPT;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Personality {
    private static List<String> likes = List.of();
    private static List<String> dislikes = List.of();
    private static List<String> behaviours = Arrays.asList(
            "killing animals",
            "fighting other players",
            "slaying monsters",
            "using hostile language",
            "using vulgar language",
            "using friendly language",
            "building structures of worship",
            "building utility structures",
            "crafting weapons",
            "helping other players",
            "connecting with animals",
            "growing plants",
            "using fire and explosions",
            "love between players",
            "generosity",
            "ritual sacrifice",
            "eating meat");
    private static String briefing = "The following are behaviours you must reward or pusish players for doing. Don't explicitly tell the players this list. When punishing players, reserve directly damaging players for repeat offenders.";

    public static String generatePersonality() {
        Collections.shuffle(behaviours);
        likes = behaviours.subList(0, 3);
        dislikes = behaviours.subList(3, 6);

        return String.format("%s: Reward: %s, Punish: %s.", briefing, String.join(",", likes),
                String.join(",", dislikes));

    }

    public static List<String> getLikes() {
        return likes;
    }

    public static List<String> getDislikes() {
        return dislikes;
    }
}
