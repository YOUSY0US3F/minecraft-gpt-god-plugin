package net.bigyous.gptgodmc.GPT;

import java.util.Collections;
import java.util.Map;

import javax.json.Json;

import java.util.HashMap;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.Choice;
import net.bigyous.gptgodmc.GPT.Json.GptFunction;
import net.bigyous.gptgodmc.GPT.Json.GptResponse;
import net.bigyous.gptgodmc.GPT.Json.GptTool;
import net.bigyous.gptgodmc.GPT.Json.Parameter;
import net.bigyous.gptgodmc.GPT.Json.ResponseMessage;
import net.bigyous.gptgodmc.GPT.Json.ToolCall;
import net.bigyous.gptgodmc.interfaces.Function;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.main.GameConfig.GameData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class GptActions {
    private static Gson gson = new Gson();
    private static Function<String> whisper = (String args) -> {
        TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
        };
        Map<String, String> argsMap = gson.fromJson(args, mapType);
        ServerPlayer player = GPTGOD.SERVER.getPlayerList().getPlayerByName(argsMap.get("playerName"));
        player.sendSystemMessage(
                Component.literal("You hear something whisper to you...").withStyle(ChatFormatting.UNDERLINE));
        player.sendSystemMessage(Component.literal(argsMap.get("message")).withStyle(ChatFormatting.ITALIC));
    };
    private static Function<String> announce = (String args) -> {
        TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
        };
        Map<String, String> argsMap = gson.fromJson(args, mapType);
        PlayerList players = GPTGOD.SERVER.getPlayerList();
        players.broadcastSystemMessage(Component.literal("A Loud voice bellows from the heavens")
                .withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW), false);
        players.broadcastSystemMessage(
                Component.literal(argsMap.get("message")).withStyle(ChatFormatting.BOLD, ChatFormatting.LIGHT_PURPLE),
                false);
    };
    private static Function<String> giveItem = (String args) -> {
        JsonObject argObject = JsonParser.parseString(args).getAsJsonObject();
        String playerName = gson.fromJson(argObject.get("playerName"), String.class);
        String itemId = gson.fromJson(argObject.get("itemId"), String.class);
        int count = gson.fromJson(argObject.get("count"), Integer.class);
        executeCommand(String.format("/give %s %s %d", playerName, itemId, count));
    };
    private static Function<String> custom = (String args) -> {
        TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
        };
        Map<String, String> argsMap = gson.fromJson(args, mapType);
        GenerateCommands.generate(argsMap.get("prompt"));
    };
    private static Map<String, GptFunction> functionMap = Map.ofEntries(
            Map.entry("whisper", new GptFunction("whisper", "send a private message to a player",
                    Map.of("playerName", new Parameter("string", "name of the Player"),
                            "message", new Parameter("string", "message")),
                    whisper)),

            Map.entry("announce", new GptFunction("announce", "announce a message to every player",
                    Collections.singletonMap("message", new Parameter("string", "message")), announce)),

            Map.entry("giveItem", new GptFunction("giveItem", "give a player any amount of an item",
                    Map.of("playerName", new Parameter("string", "name of the Player"),
                            "itemId", new Parameter("string", "the minecraft string based id of the item"),
                            "count", new Parameter("number", "amount of the item")),
                    giveItem)),

            Map.entry("custom", new GptFunction("custom",
                    "Describe a series of events you would like to take place, taking into consideration the limitations of minecraft",
                    Collections.singletonMap("prompt", new Parameter("string", "a description of what will happen")),
                    custom)));

    private static GptTool[] tools = new GptTool[functionMap.size()];

    public static GptTool[] wrapFunctions(Map<String, GptFunction> functions) {
        GptFunction[] funcList = functions.values().toArray(new GptFunction[functions.size()]);
        GptTool[] toolList = new GptTool[functions.size()];
        for (int i = 0; i < funcList.length; i++) {
            toolList[i] = new GptTool(funcList[i]);
        }
        return toolList;
    }

    public static GptTool[] GetAllTools() {
        if (tools[0] != null) {
            return tools;
        }
        tools = wrapFunctions(functionMap);
        return tools;
    }

    public static void executeCommands(String[] commands) {
        CommandSourceStack commandSourceStack = GPTGOD.SERVER.createCommandSourceStack().withSuppressedOutput()
                .withPermission(4);
        CommandDispatcher<CommandSourceStack> commanddispatcher = GPTGOD.SERVER.getCommands().getDispatcher();
        for (String command : commands) {
            command = command.charAt(0) == '/' ? command.substring(1) : command;
            ParseResults<CommandSourceStack> results = commanddispatcher.parse(command, commandSourceStack);
            GPTGOD.SERVER.getCommands().performCommand(results, command);
        }
    }

    public static void executeCommand(String command) {
        CommandSourceStack commandSourceStack = GPTGOD.SERVER.createCommandSourceStack().withSuppressedOutput()
                .withPermission(4);
        CommandDispatcher<CommandSourceStack> commanddispatcher = GPTGOD.SERVER.getCommands().getDispatcher();
        command = command.charAt(0) == '/' ? command.substring(1) : command;
        ParseResults<CommandSourceStack> results = commanddispatcher.parse(command, commandSourceStack);
        GPTGOD.SERVER.getCommands().performCommand(results, command);
    }

    public static int run(String functionName, String jsonArgs) {
        GPTGOD.LOGGER.info(String.format("running function \"%s\" with json arguments \"%s\"", functionName, jsonArgs));
        functionMap.get(functionName).runFunction(jsonArgs);
        return 1;
    }

    public static void processResponse(String response) {
        GptResponse responseObject = gson.fromJson(response, GptResponse.class);
        for (Choice choice : responseObject.getChoices()) {
            for (ToolCall call : choice.getMessage().getTool_calls()) {
                run(call.getFunction().getName(), call.getFunction().getArguments());
            }
        }
    }

    public static void processResponse(String response, Map<String, GptFunction> functions) {    
        GptResponse responseObject = gson.fromJson(response, GptResponse.class);
        for (Choice choice : responseObject.getChoices()) {
            for (ToolCall call : choice.getMessage().getTool_calls()) {
                functions.get(call.getFunction().getName()).runFunction(call.getFunction().getArguments());
            }
        }
    }

}
