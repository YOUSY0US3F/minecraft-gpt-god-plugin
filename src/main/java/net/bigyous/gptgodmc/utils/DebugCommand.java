package net.bigyous.gptgodmc.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.bigyous.gptgodmc.GPT.GptActions;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DebugCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
            Commands.literal("try").requires(source -> source.hasPermission(2))
                .then(Commands.argument("action", StringArgumentType.string())
                    .then(Commands.argument("json", StringArgumentType.string())
                        .executes(context -> GptActions.run(StringArgumentType.getString(context, "action"), 
                            StringArgumentType.getString(context, "json")))
                    )
                )
        );      
    }
}
