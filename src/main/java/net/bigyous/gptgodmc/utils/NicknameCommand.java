package net.bigyous.gptgodmc.utils;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NicknameCommand implements CommandExecutor {
    private static ConcurrentHashMap<UUID, String> NICK_NAMES = new ConcurrentHashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player)){
            return false;
        }

        Player player = (Player) sender;

        NICK_NAMES.put(player.getUniqueId(), args[0]);

        return true;
    }

    public static String getNickname(Player player){
        if(NICK_NAMES.containsKey(player.getUniqueId())){
            return NICK_NAMES.get(player.getUniqueId());
        }
        return "";
    }

}
