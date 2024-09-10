package net.bigyous.gptgodmc.GPT;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.bigyous.gptgodmc.GPTGOD;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Personality {
    private static FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
    private static List<String> likes = List.of();
    private static List<String> dislikes = List.of();
    private static List<String> behaviours = config.getStringList("potentialBehaviors");

    private static String briefing = "The following are behaviours you must reward or punish players for doing. Don't explicitly tell the players this list. When punishing players, reserve directly damaging players for repeat offenders. If most of the players disobey you punish everyone";

    public static String generatePersonality() {
        Collections.shuffle(behaviours);
        int dislikeCount = config.getInt("dislikedBehaviors");
        int likeCount = config.getInt("likedBehaviors");
        // Have to make sure we don't request more behaviors than actually exist.
        if (behaviours.size() >= likeCount + dislikeCount) {
            likes = behaviours.subList(0, likeCount);
            dislikes = behaviours.subList(likeCount, likeCount + dislikeCount);
        }
        // If we would have, use these fallbacks and inform the user.
        else {
            JavaPlugin.getPlugin(GPTGOD.class).getLogger().warning("Tried to get more behaviors than actually existed, your configuration file is probably incorrect. Make sure likedBehaviors + dislikedBehaviors is less than your total amount of potential behaviors");
            likes = List.of("Functioning config files");
            dislikes = List.of("Borked config files");
        }

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
