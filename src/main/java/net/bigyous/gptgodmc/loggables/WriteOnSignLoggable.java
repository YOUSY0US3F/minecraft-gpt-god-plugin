package net.bigyous.gptgodmc.loggables;
import org.bukkit.event.block.SignChangeEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
public class WriteOnSignLoggable extends BaseLoggable {
    private String playerName;
    private String text;
    public WriteOnSignLoggable(SignChangeEvent event){
        this.playerName = event.getPlayer().getName();
        StringBuilder sb = new StringBuilder();
        event.lines().forEach( (Component component)-> {
            String text = ((TextComponent)component).content();
            if(!text.isBlank()){
                sb.append(((TextComponent)component).content() + " ");
            }    
        });
        this.text = sb.toString();
    }

    @Override
    public String getLog() {
        return String.format("%s wrote %son a sign", playerName, text);
    }
}
