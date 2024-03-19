package net.bigyous.gptgodmc.loggables;

import java.util.Locale;
import org.bukkit.event.entity.PlayerDeathEvent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
public class DeathLoggable extends BaseLoggable {
    
    private String deathMessage;

    public DeathLoggable(PlayerDeathEvent event){
        this.deathMessage = PlainTextComponentSerializer.plainText().serialize(event.deathMessage());
        
    }

    public String getLog(){
        return deathMessage;
    }

}
