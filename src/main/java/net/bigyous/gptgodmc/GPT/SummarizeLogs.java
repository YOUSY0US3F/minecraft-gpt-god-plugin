package net.bigyous.gptgodmc.GPT;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.bigyous.gptgodmc.EventLogger;
import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.GptFunction;
import net.bigyous.gptgodmc.GPT.Json.GptFunctionReference;
import net.bigyous.gptgodmc.GPT.Json.GptResponse;
import net.bigyous.gptgodmc.GPT.Json.GptTool;
import net.bigyous.gptgodmc.GPT.Json.Parameter;
import net.bigyous.gptgodmc.interfaces.Function;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SummarizeLogs {
    private static Gson gson = new Gson();
    private static Function<String> submitSummary = (String args) ->{
        JsonObject argObject = JsonParser.parseString(args).getAsJsonObject();
        GPTGOD.LOGGER.info("summary submitted with args: ", args);
        EventLogger.setSummary(gson.fromJson(argObject.get("summary"), String.class));
    };
    private static Map<String, GptFunction> functionMap = Map.of("submitSummary", 
        new GptFunction("submitSummary", "input the summary, keep the summary below 1000 tokens", 
            Map.of("summary", new Parameter("string","the summary")), 
            submitSummary));
    private static GptTool[] tools = GptActions.wrapFunctions(functionMap);
    private static GptAPI gpt = new GptAPI(GPTModels.GPT_3, tools)
    .addContext("""
    You are a helpful assistant that will recieve a log of events from a minecraft server, \
    or a summary and a log of events. \
    You will create a short summary based on this information that preserves the plot. \
    If information in the logs isn't important to the plot omit it. Do not add any extra flourishes, just state the facts.
    """, "prompt").setToolChoice(new GptFunctionReference(functionMap.get("submitSummary")));

    public static void summarize(String log, String summary){
        String content = String.format("Write a short summary that summarizes the events of these logs: %s%s", log, 
            summary != null? String.format(":and this Summary %s", summary): "");
        gpt.addLogs(content, "logs").send(functionMap);
    }
}
