package net.bigyous.gptgodmc.GPT;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.GptModel;

public class GPTModels {
    private static FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
    public static final GptModel GPT_4o = new GptModel("gpt-4o", 100000);
    public static final GptModel GPT_4o_mini = new GptModel("gpt-4o-mini", 85000);

    public static GptModel getMainModel(){
        String modelName;
        if (config.isSet("gpt-model-name")) {
            modelName = config.getString("gpt-model-name");
        } else if (config.isSet("use-gpt-4")) {
            // for passivity
            modelName = config.getBoolean("use-gpt-4")? "gpt-4o": "gpt-4o-mini";
        } else {
            throw new RuntimeException("Please set a value for gpt-model-name.");
        }

        int tokenLimit;

        if (config.isSet("gpt-model-token-limit")) {
            tokenLimit = config.getInt("gpt-model-token-limit");
        } else {
            tokenLimit = switch (modelName) {
                case "gpt-4o", "gpt-4o-2024-08-06" -> 100000;
                case "gpt-4o-mini" -> 85000;
                default -> throw new RuntimeException(String.format("Could not automatically determine token limit for %s. Please set gpt-model-token-limit in the config.", modelName));
            };
        }
        return new GptModel(modelName, tokenLimit) ;
    }
}
