package net.bigyous.gptgodmc;

import net.bigyous.gptgodmc.utils.TaskQueue;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import net.bigyous.gptgodmc.GPT.Transcription;
import net.bigyous.gptgodmc.loggables.ChatLoggable;
import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.PlayerDisconnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStoppedEvent;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.time.Instant;

@ForgeVoicechatPlugin
public class VoiceMonitorPlugin implements VoicechatPlugin {

    private static ConcurrentHashMap<UUID, PlayerAudioBuffer> buffers;
    private static ConcurrentHashMap<UUID, OpusDecoder> decoders;
    private static TaskQueue<PlayerAudioBuffer> encodingQueue;

    /**
     * @return the unique ID for this voice chat plugin
     */
    @Override
    public String getPluginId() {
        return GPTGOD.PLUGIN_ID;
    }

    /**
     * Called when the voice chat initializes the plugin.
     *
     * @param api the voice chat API
     */
    @Override
    public void initialize(VoicechatApi api) {
        GPTGOD.LOGGER.info("voice monitor initialized");
        buffers = new ConcurrentHashMap<UUID, PlayerAudioBuffer>();
        decoders = new ConcurrentHashMap<UUID, OpusDecoder>();
        encodingQueue = new TaskQueue<PlayerAudioBuffer>((PlayerAudioBuffer buffer) -> {
            Instant timestamp = Instant.now();
            String speech = Transcription.Transcribe(AudioFileManager.getPlayerFile(buffer.getPlayer(), buffer.getBufferId()));
            AudioFileManager.deleteFile(buffer.getPlayer(), buffer.getBufferId());
            GPTGOD.LOGGER.info(String.format("%s said: %s", buffer.getPlayer().getName(), speech));
            if(speech == null){
                return;
            }
            EventLogger.addLoggable(new ChatLoggable(buffer.getPlayer().getName(), speech, timestamp));
        });
    }

    /**
     * Called once by the voice chat to register all events.
     *
     * @param registration the event registration
     */
    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicPacket);
        registration.registerEvent(PlayerDisconnectedEvent.class, this::onPlayerDisconnect);
        registration.registerEvent(VoicechatServerStoppedEvent.class,this::onServerStopped);
    }

    private void onMicPacket(MicrophonePacketEvent event){
        VoicechatConnection senderConnection = event.getSenderConnection();
        byte[] encodedData = event.getPacket().getOpusEncodedData();
        if (senderConnection == null) {
            return;
        }
        if (!(senderConnection.getPlayer().getPlayer() instanceof Player player)) {
            // GPTGOD.LOGGER.warn("Received microphone packets from non-player");
            return;
        }
        if(player.getGameMode() == GameMode.SPECTATOR){
            return;
        }
        // GPTGOD.LOGGER.info(String.format("Player: %s Sent packet of length: %d", player.getName(), encodedData.length));
        if (!decoders.containsKey(player.getUniqueId())) {
            decoders.put(player.getUniqueId(), event.getVoicechat().createDecoder());
            // GPTGOD.LOGGER.info(String.format("opusDecoder created for UUID: %s", player.getUniqueId().toString()));
        }
        OpusDecoder decoder = decoders.get(player.getUniqueId());
        short[] decoded = decoder.decode(event.getPacket().getOpusEncodedData());

        if(encodedData.length > 0){
            if(!buffers.containsKey(player.getUniqueId())){
                PlayerAudioBuffer buffer = new PlayerAudioBuffer(decoded, player, event.getVoicechat());
                buffers.put(player.getUniqueId(), buffer);
                // GPTGOD.LOGGER.info(String.format("AudioBuffer #%d created for UUID: %s", buffer.getBufferId(), player.getUniqueId().toString()));
            }
            else{
                buffers.get(player.getUniqueId()).addSamples(decoded);
            }
        }
        else{
            // GPTGOD.LOGGER.info(String.format("decoders: %s, buffers: %s", decoders.toString(), buffers.toString()));
            PlayerAudioBuffer toBeProcessed = buffers.get(player.getUniqueId());
            toBeProcessed.createWAV();
            encodingQueue.insert(toBeProcessed);
            buffers.remove(player.getUniqueId());
            decoder.resetState();
        }
    }

    private void onPlayerDisconnect(PlayerDisconnectedEvent event){
        cleanUpPlayer(event.getPlayerUuid(), event.getVoicechat());
    }

    private void onServerStopped(VoicechatServerStoppedEvent event){
        decoders.forEach((key, value) -> cleanUpPlayer(key, event.getVoicechat()));
    }

    private void cleanUpPlayer(UUID uuid, VoicechatServerApi vc){

        AudioFileManager.deletePlayerData(uuid);
        if(!decoders.containsKey(uuid)){
            GPTGOD.LOGGER.info(String.format("Cleaned up data for UUID: %s, there was no decoder to clean", uuid.toString()));
            return;
        }
        decoders.get(uuid).close();
        decoders.remove(uuid);
        GPTGOD.LOGGER.info(String.format("Cleaned up data for UUID: %s", uuid.toString()));
    }

}
