package net.bigyous.gptgodmc.GPT;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.github.mizosoft.methanol.Methanol;
import com.github.mizosoft.methanol.MultipartBodyPublisher;
import com.github.mizosoft.methanol.MutableRequest;

import java.nio.file.Path;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.TranscriptionResponse;

public class Transcription {
    private static Gson gson = new Gson();
    private static Methanol client = Methanol.create();
    private static FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
    public static String Transcribe(Path audioPath) {
        if (config.getString("openAiKey").isBlank()) {
            GPTGOD.LOGGER.warn("No API Key");
            return "something";
        }

        try {
            MultipartBodyPublisher body = MultipartBodyPublisher.newBuilder()
                .filePart("file", audioPath)
                .textPart("model", "whisper-1")
                .textPart("language", config.getString("language"))
                .build();
            MutableRequest request = MutableRequest.POST("https://api.openai.com/v1/audio/transcriptions", body)
                .header("Authorization", "Bearer " + config.getString("openAiKey"))
                .header("Content-Type", "multipart/form-data");
            HttpResponse<String> response =  client.send(request, BodyHandlers.ofString());
            return gson.fromJson(response.body(), TranscriptionResponse.class).getText();
        } catch (FileNotFoundException e) {
            GPTGOD.LOGGER.error("Attempted to Transcribe non-existant file", e);
            e.printStackTrace();
        } catch (IOException e) {
            GPTGOD.LOGGER.error("An error occured during Transcription", e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            GPTGOD.LOGGER.error("Transcription Interrupted", e);
        }
        return "something";
    }
}
