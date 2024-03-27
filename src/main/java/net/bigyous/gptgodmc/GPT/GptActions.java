package net.bigyous.gptgodmc.GPT;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.bigyous.gptgodmc.EventLogger;
import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.StructureManager;
import net.bigyous.gptgodmc.WorldManager;
import net.bigyous.gptgodmc.GPT.Json.Choice;
import net.bigyous.gptgodmc.GPT.Json.GptFunction;
import net.bigyous.gptgodmc.GPT.Json.GptResponse;
import net.bigyous.gptgodmc.GPT.Json.GptTool;
import net.bigyous.gptgodmc.GPT.Json.Parameter;
import net.bigyous.gptgodmc.GPT.Json.ToolCall;
import net.bigyous.gptgodmc.interfaces.Function;
import net.bigyous.gptgodmc.loggables.GPTActionLoggable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class GptActions {
    private int tokens = -1;
    private static Gson gson = new Gson();

    private static Function<String> whisper = (String args) -> {
        TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
        };
        Map<String, String> argsMap = gson.fromJson(args, mapType);
        Player player = GPTGOD.SERVER.getPlayerExact(argsMap.get("playerName"));
        player.sendRichMessage("<i>You hear something whisper to you...</i>");
        player.sendMessage(argsMap.get("message"));
        EventLogger.addLoggable(new GPTActionLoggable(
                String.format("whispered \"%s\" to %s", argsMap.get("message"), argsMap.get("playerName"))));
    };
    private static Function<String> announce = (String args) -> {
        TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
        };
        Map<String, String> argsMap = gson.fromJson(args, mapType);
        GPTGOD.SERVER.broadcast(Component.text("A Loud voice bellows from the heavens", NamedTextColor.YELLOW)
                .decoration(TextDecoration.BOLD, true));
        GPTGOD.SERVER.broadcast(Component.text(argsMap.get("message"), NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.BOLD, true));
        EventLogger.addLoggable(new GPTActionLoggable(String.format("announced \"%s\"", argsMap.get("message"))));
    };
    private static Function<String> giveItem = (String args) -> {
        JsonObject argObject = JsonParser.parseString(args).getAsJsonObject();
        String playerName = gson.fromJson(argObject.get("playerName"), String.class);
        String itemId = gson.fromJson(argObject.get("itemId"), String.class);
        int count = gson.fromJson(argObject.get("count"), Integer.class);
        // executeCommand(String.format("/give %s %s %d", playerName, itemId, count));
        if (Material.matchMaterial(itemId) == null)
            return;
        Player player = GPTGOD.SERVER.getPlayer(playerName);
        player.getInventory().addItem(new ItemStack(Material.matchMaterial(itemId), count));
        player.sendRichMessage(String.format("<i>A %s appeared in your inventory</i>", itemId));
        EventLogger.addLoggable(new GPTActionLoggable(String.format("gave %d %s to %s", count, itemId, playerName)));
    };
    private static Function<String> command = (String args) -> {
        TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
        };
        Map<String, String> argsMap = gson.fromJson(args, mapType);
        GenerateCommands.generate(argsMap.get("prompt"));
        EventLogger
                .addLoggable(new GPTActionLoggable(String.format("commanded \"%s\" to happen", argsMap.get("prompt"))));
    };
    private static Function<String> smite = (String args) -> {
        TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
        };
        Map<String, String> argsMap = gson.fromJson(args, mapType);
        String playerName = argsMap.get("playerName");
        Player player = GPTGOD.SERVER.getPlayer(playerName);
        WorldManager.getCurrentWorld().strikeLightning(player.getLocation());
        EventLogger.addLoggable(new GPTActionLoggable(String.format("smited %s", playerName)));
    };
    // this one's fucked
    private static Function<String> summonSupplyChest = (String args) -> {
        JsonObject argObject = JsonParser.parseString(args).getAsJsonObject();
        String playerName = gson.fromJson(argObject.get("playerName"), String.class);
        @SuppressWarnings("unchecked")
        Map<String, Integer> itemNames = gson.fromJson(argObject.get("items"), Map.class);
        List<ItemStack> items = itemNames.keySet().stream().map((String itemName) -> {
            return new ItemStack(Material.matchMaterial(itemName), itemNames.get(itemName));
        }).toList();
        Location playerLoc = GPTGOD.SERVER.getPlayer(playerName).getLocation();
        Block currentBlock = WorldManager.getCurrentWorld().getBlockAt(playerLoc.blockX() + 1, playerLoc.blockY(),
                playerLoc.blockZ() + 1);
        currentBlock.setType(Material.CHEST);
        Chest chest = (Chest) currentBlock;
        chest.getBlockInventory().addItem(items.toArray(new ItemStack[itemNames.size()]));
    };
    private static Function<String> transformStructure = (String args) -> {
        JsonObject argObject = JsonParser.parseString(args).getAsJsonObject();
        String structure = gson.fromJson(argObject.get("structure"), String.class);
        String blockType = gson.fromJson(argObject.get("block"), String.class);

        StructureManager.getStructure(structure).getBlocks()
                .forEach((Block b) -> b.setType(Material.matchMaterial(blockType)));
        EventLogger.addLoggable(
                new GPTActionLoggable(String.format("turned all the blocks in Structure %s to %s", structure)));
    };
    private static Function<String> detonateStructure = (String args) -> {
        JsonObject argObject = JsonParser.parseString(args).getAsJsonObject();
        String structure = gson.fromJson(argObject.get("structure"), String.class);
        boolean setFire = gson.fromJson(argObject.get("setFire"), Boolean.class);
        int power = gson.fromJson(argObject.get("power"), Integer.class);
        StructureManager.getStructure(structure).getLocation().createExplosion(power, setFire, true);
        EventLogger.addLoggable(new GPTActionLoggable(String.format("detonated Structure: %s", structure)));
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
                            "itemId", new Parameter("string", "the name of the minecraft item"),
                            "count", new Parameter("number", "amount of the item")),
                    giveItem)),
            Map.entry("command", new GptFunction("command",
                    "Describe a series of events you would like to take place, taking into consideration the limitations of minecraft",
                    Collections.singletonMap("prompt", new Parameter("string", "a description of what will happen")),
                    command)),
            Map.entry("smite", new GptFunction("smite", "Strike a player down with lightning",
                    Collections.singletonMap("playerName", new Parameter("string", "the player's name")), smite)),
            Map.entry("transformStructure",
                    new GptFunction("transformStructure", "replace all the blocks in a structure with any block",
                            Map.of("structure", new Parameter("string", "name of the structure"),
                                    "block", new Parameter("string", "The name of the minecraft block")),
                            transformStructure)),
            Map.entry("detonateStructure", new GptFunction("detonateStructure", "cause an explosion at a Structure",
                    Map.of("structure", new Parameter("string", "name of the structure"),
                            "setFire", new Parameter("boolean", "will this explosion cause fires?"),
                            "power",
                            new Parameter("number", "the strength of this explosion where 4 is the strength of TNT")),
                    detonateStructure)));
    private static Map<String, GptFunction> speechFunctionMap = new HashMap<>(functionMap);
    private static Map<String, GptFunction> actionFunctionMap = new HashMap<>(functionMap);

    private static GptTool[] tools;
    private static GptTool[] actionTools;
    private static GptTool[] speechTools;
    private static final List<String> speechActionKeys = Arrays.asList("announce", "whisper");

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

    public static GptTool[] GetActionTools() {
        if (actionTools != null && actionTools[0] != null) {
            return actionTools;
        }
        actionFunctionMap.keySet().removeAll(speechActionKeys);
        actionTools = wrapFunctions(actionFunctionMap);
        return actionTools;
    }

    public static GptTool[] GetSpeechTools() {
        if (speechTools != null && speechTools[0] != null) {
            return speechTools;
        }
        speechFunctionMap.keySet().retainAll(speechActionKeys);
        speechTools = wrapFunctions(speechFunctionMap);
        return speechTools;
    }

    private static void dispatch(String command, CommandSender console) {
        if (command.matches(".*\\bgive\\b.*")) {
            GPTGOD.SERVER.dispatchCommand(console, command);
        } else {
            command = command.replaceAll("\\/|(execute )", "");
            GPTGOD.SERVER.dispatchCommand(console,
                    String.format("execute in %s %s", WorldManager.getDimensionName(), command));
        }
    }

    public static void executeCommands(String[] commands) {
        CommandSender console = GPTGOD.SERVER.getConsoleSender();
        for (String command : commands) {
            dispatch(command, console);
        }
    }

    public static void executeCommand(String command) {
        CommandSender console = GPTGOD.SERVER.getConsoleSender();
        dispatch(command, console);
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

    private int calculateFunctionTokens() {
        int sum = 0;
        for (GptFunction function : functionMap.values()) {
            sum += function.calculateFunctionTokens();
        }
        return sum;
    }

    public int getTokens() {
        if (tokens >= 0) {
            return tokens;
        }
        return calculateFunctionTokens();
    }

}
