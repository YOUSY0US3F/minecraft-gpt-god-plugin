package net.bigyous.gptgodmc;

import javax.sound.sampled.AudioFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AudioFileManager {
    // bitrate in kbps (48000hz * 16 bits)
    public static final int BIT_RATE = 320;
    public static AudioFormat FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000F, 16, 1, 
        2, 48000F, false);
    private static AtomicInteger currentId = new AtomicInteger();
    public static Path VOICE_DATA = JavaPlugin.getPlugin(GPTGOD.class).getDataFolder().toPath().resolve("player_voice_data");

    public static Path getPlayerMp3(Player player, int fileNumber){
        String uuidString = player.getUniqueId().toString();
        
        return VOICE_DATA.resolve(String.format("%s/%d.mp3", uuidString, fileNumber));
    }

    public static OutputStream getPlayerOutputStream(Player player, int fileNumber){
        Path soundFile = getPlayerMp3(player, fileNumber);
        try {
            Files.createDirectories(soundFile.getParent());
            return Files.newOutputStream(soundFile);
        } catch (IOException e) {
            GPTGOD.LOGGER.warn(String.format("An IO Exception occured getting output stream for player: %s", player.getName()));
            return null;
        }
    }

    public static void deletePlayerData(Player player){   
        String uuidString = player.getUniqueId().toString();
        try {
            // Files.delete(getPlayerMp3(player));
            FileUtils.deleteDirectory(VOICE_DATA.resolve(uuidString).toFile());
        } catch (IOException e) {
            GPTGOD.LOGGER.warn("tried to delete nonexistant file");
        }
    }

    public static void deleteFile(Path path){
        try {
            Files.delete(path);
        } catch (IOException e) {
            GPTGOD.LOGGER.error("tried to delete non-existant file", e);
        }
    }

    public static void deleteFile(Player player, int fileNumber){
        try {
            Files.delete(getPlayerMp3(player, fileNumber));
        } catch (IOException e) {
            GPTGOD.LOGGER.error("tried to delete non-existant file", e);
        }
    }

    public static int getCurrentId(){
        // overflowing to min int would be fine but I like having positive numbers
        int id = currentId.getAndIncrement();
        if(id == Integer.MIN_VALUE){
            currentId = new AtomicInteger();
            id = currentId.get();
        }
        return id;
    }

    public static void reset(){
        currentId = new AtomicInteger();
    }

}
