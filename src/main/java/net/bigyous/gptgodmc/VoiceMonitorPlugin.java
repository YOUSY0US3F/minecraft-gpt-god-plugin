package net.bigyous.gptgodmc;

import net.bigyous.gptgodmc.utils.TaskQueue;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import net.bigyous.gptgodmc.GPT.Transcription;
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
            String speech = Transcription.Transcribe(AudioFileManager.getPlayerMp3(buffer.getPlayer(), buffer.getBufferId()));
            AudioFileManager.deleteFile(buffer.getPlayer(), buffer.getBufferId());
            GPTGOD.LOGGER.info(String.format("%s said: %s", buffer.getPlayer().getName(), speech));
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
            GPTGOD.LOGGER.warn("Received microphone packets from non-player");
            return;
        }
        if(player.getGameMode() == GameMode.SPECTATOR){
            return;
        }
        // GPTGOD.LOGGER.info(String.format("Player: %s Sent packet of length: %d", player.getDisplayName().getString(), encodedData.length));
        if (!decoders.containsKey(player.getUniqueId())) {
            decoders.put(player.getUniqueId(), event.getVoicechat().createDecoder());
        }
        OpusDecoder decoder = decoders.get(player.getUniqueId());
        short[] decoded = decoder.decode(event.getPacket().getOpusEncodedData());

        if(encodedData.length > 0){
            if(!buffers.containsKey(player.getUniqueId())){
                PlayerAudioBuffer buffer = new PlayerAudioBuffer(decoded, player, event.getVoicechat());
                buffers.put(player.getUniqueId(), buffer);
            }
            else{
                buffers.get(player.getUniqueId()).addSamples(decoded);
            }
        }
        else{
            PlayerAudioBuffer toBeProcessed = buffers.get(player.getUniqueId());
            toBeProcessed.encode();
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
        UUID playerUuid = uuid;
        if (vc.getConnectionOf(playerUuid).getPlayer().getPlayer() instanceof Player player){
            AudioFileManager.deletePlayerData(player);
        }
        if(!decoders.contains(playerUuid)){
            return;
        }
        decoders.get(playerUuid).close();
        decoders.remove(uuid);
        GPTGOD.LOGGER.info(String.format("Cleaned up data for UUID: %s", uuid.toString()));
    }

}
