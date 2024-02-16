package net.bigyous.gptgodmc.utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.bigyous.gptgodmc.GPT.GptActions;

public class DebugCommand implements CommandExecutor{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(sender instanceof Player){
            sender.sendMessage("Use the Server console to use this command");
            return false;
        }

        String commandName = args[0];
        String jsonArgs = args[1];

        GptActions.run(commandName, jsonArgs);
        return true;
    }

}
