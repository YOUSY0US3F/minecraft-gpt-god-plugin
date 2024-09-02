package net.bigyous.gptgodmc.utils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioFormat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.WorldManager;

public class QueuedAudio {
    private static JavaPlugin plugin = JavaPlugin.getPlugin(GPTGOD.class);
    private static VoicechatServerApi api = (VoicechatServerApi)GPTGOD.VC_SERVER;
    private static ConcurrentLinkedQueue<audioEvent> playQueue = new ConcurrentLinkedQueue<audioEvent>();
    private static float SAMPLE_RATE = 24000f;
    private static int taskId = -1;
    private static ConcurrentHashMap<UUID, AudioChannel> channels = new ConcurrentHashMap<UUID, AudioChannel>();

    public static void playAudio(short[] samples, Player[] players){
        playQueue.add(new audioEvent(samples, players));
        if(taskId == -1 || !GPTGOD.SERVER.getScheduler().isCurrentlyRunning(taskId)){
            BukkitTask task = GPTGOD.SERVER.getScheduler().runTaskLater(plugin, playQueue.poll(), getLengthSeconds(samples)*20);
            taskId = task.getTaskId();
        }
    }
    private static long getLengthSeconds(short[] audio) {
        return (long) (audio.length / SAMPLE_RATE);
    }
    private static AudioChannel getplayerAudioChannel(UUID uuid){
        if(!channels.containsKey(uuid)){
            channels.put(uuid, api.createStaticAudioChannel(UUID.randomUUID(), api.fromServerLevel(WorldManager.getCurrentWorld()) , api.getConnectionOf(uuid)));
        }
        return channels.get(uuid);
    } 
    static class audioEvent implements Runnable {
        private short[] samples;
        private Player[] players;

        public audioEvent(short[] samples, Player[] players){
            this.samples = samples;
            this.players = players;
        }

        public void run(){
            for(Player player : players) {
                GPTGOD.LOGGER.info("playing audio for player: ", player.getName());
                api.createAudioPlayer(getplayerAudioChannel(player.getUniqueId()), api.createEncoder(), samples).startPlaying();
            }
            if(playQueue.peek() != null){
                BukkitTask task = GPTGOD.SERVER.getScheduler().runTaskLater(plugin, playQueue.poll(), getLengthSeconds(samples)*20);
                taskId = task.getTaskId();
            }
        }
    }
}
