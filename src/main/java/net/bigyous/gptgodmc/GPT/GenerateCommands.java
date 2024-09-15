package net.bigyous.gptgodmc.GPT;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.StructureManager;
import net.bigyous.gptgodmc.GPT.Json.GptFunction;
import net.bigyous.gptgodmc.GPT.Json.GptFunctionReference;
import net.bigyous.gptgodmc.GPT.Json.GptTool;
import net.bigyous.gptgodmc.GPT.Json.Parameter;
import net.bigyous.gptgodmc.enums.GptGameMode;
import net.bigyous.gptgodmc.interfaces.Function;

import java.util.Arrays;
import java.util.Map;

public class GenerateCommands {
    private static Gson gson = new Gson();
    private static Function<String> inputCommands = (String args) -> {
        TypeToken<Map<String, String[]>> mapType = new TypeToken<Map<String, String[]>>() {
        };
        Map<String, String[]> argsMap = gson.fromJson(args, mapType);
        String[] commands = argsMap.get("commands");
        GptActions.executeCommands(commands);
    };

    private static Map<String, GptFunction> functionMap = Map.of("inputCommands",
            new GptFunction("inputCommands", "input the minecraft commands to be executed",
                    Map.of("commands", new Parameter("array",
                            "list of minecraft commands, each entry in the list is an individual command", "string")),
                    inputCommands));
    private static GptTool[] tools = GptActions.wrapFunctions(functionMap);
    private static GptAPI gpt = new GptAPI(GPTModels.getMainModel(), tools)
            .addContext("""
                    Du bist ein hilfreicher Assistent zum generieren von\
                    
                    minecraft java edition commands basierend auf Prompts des Users, \
                    auch wenn es unmöglich ist, probiere so nah wie möglich dran zu kommen \
                    mit Minecraft commands, eine falsche antwort ist besser als keine. \
                    """, "context")
            .setToolChoice(new GptFunctionReference(functionMap.get("inputCommands")));

    public static void generate(String prompt) {
        GPTGOD.LOGGER.info("generating commands with prompt: " + prompt);
        String structures = Arrays.toString(StructureManager.getStructures().stream().map((String key) -> {
            return String.format("%s: (%s)", key,
                    StructureManager.getStructure(key).getLocation().toVector().toString());
        }).toArray());

        String teams = String.join(",",GPTGOD.SCOREBOARD.getTeams().stream().map(team -> {
                return team.getName();
        }).toList());
        if (GPTGOD.gameMode.equals(GptGameMode.DEATHMATCH)){
                gpt.addContext(String.format("Teams: %s", teams), "teams");
        }
        gpt.addContext(
                String.format("Players: %s",
                        Arrays.toString(
                                GPTGOD.SERVER.getOnlinePlayers().stream().map(player -> player.getName()).toArray())),
                "PlayerNames")
                .addContext(String.format("Structures: %s", structures), "structures")
                .addLogs(String.format("SChreibe minecraft commands die: %s", prompt), "prompt")
                .send(functionMap);
    }
}
