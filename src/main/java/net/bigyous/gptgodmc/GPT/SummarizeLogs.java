package net.bigyous.gptgodmc.GPT;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.GptResponse;
import net.bigyous.gptgodmc.GPT.Json.GptTool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SummarizeLogs {
    private static GptTool[] tools = new GptTool[0];
    private static GptAPI gpt = new GptAPI(GPTModels.GPT_3, tools)
    .addContext("""
    You are a helpful assistant that will recieve a log of events from a minecraft server, \
    or a summary and a log of events. \
    the logs start with a summary of Server Information that details the state of all the players and some basic info about the server.
    You will create a short summary based on this information that preserves the plot. \
    If information in the logs isn't important to the plot omit it.
    """, "prompt");

    public static String summarize(String log, String summary){
        String content = String.format("Write a short summary that summarizes the events of these logs: %s%s", log, 
            summary != null? String.format(":and this Summary %s", summary): "");
        gpt.addLogs(content, "logs");
        CompletableFuture<GptResponse> futureResponse = gpt.sendAsync();
        try {
            GptResponse response = futureResponse.get();
            return response.getChoices()[0].getMessage().getContent();
        } catch (InterruptedException | ExecutionException e) {
            GPTGOD.LOGGER.error("Summarizing failed!", e);
            return "";
        }
    }
}
