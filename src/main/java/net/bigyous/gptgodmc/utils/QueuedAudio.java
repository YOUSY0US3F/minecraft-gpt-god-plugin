package net.bigyous.gptgodmc.utils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sound.sampled.AudioFormat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer;
import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.WorldManager;

public class QueuedAudio {
    private static JavaPlugin plugin = JavaPlugin.getPlugin(GPTGOD.class);
    private static VoicechatServerApi api = (VoicechatServerApi)GPTGOD.VC_SERVER;
    private static ConcurrentLinkedQueue<audioEvent> playQueue = new ConcurrentLinkedQueue<audioEvent>();
    // private static float SAMPLE_RATE = 48000f;
    private static AtomicInteger AtomicTaskId = new AtomicInteger(-1);
    private static ConcurrentHashMap<UUID, AudioChannel> channels = new ConcurrentHashMap<UUID, AudioChannel>();
    

    public static void playAudio(short[] samples, Player[] players){
        short[] resampled = doubleSampleRate(samples);
        playQueue.add(new audioEvent(resampled, players));
        if(AtomicTaskId.get() == -1 || !GPTGOD.SERVER.getScheduler().isCurrentlyRunning(AtomicTaskId.get())){
            BukkitTask task = GPTGOD.SERVER.getScheduler().runTaskAsynchronously(plugin, playQueue.poll());
            AtomicTaskId.set(task.getTaskId());
        }
    }
    // private static long getLengthSeconds(short[] audio) {
    //     return (long) (audio.length / SAMPLE_RATE);
    // }
    private static AudioChannel getplayerAudioChannel(UUID uuid){
        if(!channels.containsKey(uuid)){
            channels.put(uuid, api.createStaticAudioChannel(UUID.randomUUID(), api.fromServerLevel(WorldManager.getCurrentWorld()) , api.getConnectionOf(uuid)));
        }
        return channels.get(uuid);
    } 
    private static short[] doubleSampleRate(short[] source){
        short[] result = new short[source.length * 2];
        for(int i = 0; i < source.length; i++) {
            result[i * 2] = source[i];
            if(i != source.length - 1){
                result[i * 2 + 1] = (short) ((source[i] + source[i + 1]) / 2);
            }
        }
        return result;
    }
    static class audioEvent implements Runnable {
        private short[] samples;
        private Player[] players;

        public audioEvent(short[] samples, Player[] players){
            this.samples = samples;
            this.players = players;
        }

        public void run(){
            AudioPlayer currentAudioPlayer = null;
            for(Player player : players) {
                GPTGOD.LOGGER.info("playing audio for player: ", player.getName());
                currentAudioPlayer = api.createAudioPlayer(getplayerAudioChannel(player.getUniqueId()), api.createEncoder(), samples);
                currentAudioPlayer.startPlaying();
            }
            while(currentAudioPlayer != null && currentAudioPlayer.isPlaying()){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    GPTGOD.LOGGER.warn("TTS interrupted", e);
                }
            }
            if(playQueue.peek() != null){
                BukkitTask task = GPTGOD.SERVER.getScheduler().runTaskAsynchronously(plugin, playQueue.poll());
                AtomicTaskId.set(task.getTaskId());
            }
        }
    }
}
