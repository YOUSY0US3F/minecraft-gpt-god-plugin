package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class AchievementLoggable extends BaseLoggable {
    private String message;
    private boolean isValid;
    public AchievementLoggable(PlayerAdvancementDoneEvent event){
        this.isValid = event.message() != null;
        this.message = isValid ? PlainTextComponentSerializer.plainText().serialize(event.message()) : null;
    }

    @Override
    public String getLog() {
        return message;
    }
}
