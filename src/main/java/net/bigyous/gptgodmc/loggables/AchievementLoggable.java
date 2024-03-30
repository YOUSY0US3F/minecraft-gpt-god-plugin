package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class AchievementLoggable extends BaseLoggable {
    private String message;

    public AchievementLoggable(PlayerAdvancementDoneEvent event){
        this.message = PlainTextComponentSerializer.plainText().serialize(event.message());
    }

    @Override
    public String getLog() {
        return message;
    }
}
