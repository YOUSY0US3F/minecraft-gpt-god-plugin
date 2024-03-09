package net.bigyous.gptgodmc.GPT;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.GptModel;

public class GPTModels {
    private static FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
    public static final GptModel GPT_4 = new GptModel("gpt-4-turbo-preview", 32768);
    public static final GptModel GPT_3 = new GptModel("gpt-3.5-turbo", 4096);

    public static GptModel getMainModel(){
        return config.getBoolean("use-gpt-4")? GPT_4: GPT_3;
    }
}
