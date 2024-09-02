package net.bigyous.gptgodmc.utils;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioFormat;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import net.bigyous.gptgodmc.GPTGOD;

public class QueuedAudio {
    private static JavaPlugin plugin = JavaPlugin.getPlugin(GPTGOD.class);
    private static VoicechatServerApi api;
    private static ConcurrentLinkedQueue<audioEvent> playQueue;
    private static AudioFormat FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000F, 16, 1, 2, 48000F, false);
    private static int taskId = -1;

    public static void playAudio(short[] samples, Entity[] players){
        playQueue.add(new audioEvent(samples, players));
        if(taskId == -1 || !GPTGOD.SERVER.getScheduler().isCurrentlyRunning(taskId)){
            BukkitTask task = GPTGOD.SERVER.getScheduler().runTaskLater(plugin, playQueue.poll(), getLengthSeconds(samples)*20);
            taskId = task.getTaskId();
        }
    }
    private static long getLengthSeconds(short[] audio) {
        return (long) (audio.length / FORMAT.getSampleRate());
    }
    static class audioEvent implements Runnable {
        private short[] samples;
        private Entity[] players;

        public audioEvent(short[] samples, Entity[] players){
            this.samples = samples;
            this.players = players;
        }

        public void run(){
            for(Entity player : players) {
                api.createAudioPlayer(api.createEntityAudioChannel(UUID.randomUUID(), api.fromEntity(player)), api.createEncoder(), samples).startPlaying();
            }
            if(playQueue.peek() != null){
                BukkitTask task = GPTGOD.SERVER.getScheduler().runTaskLater(plugin, playQueue.poll(), getLengthSeconds(samples)*20);
                taskId = task.getTaskId();
            }
        }
    }
}
