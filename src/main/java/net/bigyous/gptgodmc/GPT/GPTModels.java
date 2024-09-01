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
        return config.getBoolean("use-gpt-4")? GPT_4o: GPT_4o_mini;
    }
}
