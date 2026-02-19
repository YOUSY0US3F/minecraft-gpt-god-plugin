package net.bigyous.gptgodmc.GPT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors; 

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.GptFunction;
import net.bigyous.gptgodmc.GPT.Json.GptModel;
import net.bigyous.gptgodmc.GPT.Json.GptRequest;
import net.bigyous.gptgodmc.GPT.Json.GptTool;
import net.bigyous.gptgodmc.GPT.Json.ModelSerializer;
import net.bigyous.gptgodmc.GPT.Json.ParameterExclusion;
import net.bigyous.gptgodmc.enums.InferenceProvider;

public class GptAPI {
    private GsonBuilder gson = new GsonBuilder();
    private GptRequest body;
    private Map<String, Integer> messageMap = new HashMap<String, Integer>();
    private volatile boolean isSending = false;
    private static ExecutorService pool = Executors.newCachedThreadPool();

    public GptAPI(GptModel model) {
        this.body = new GptRequest(model, GptActions.GetAllTools());
        gson.registerTypeAdapter(GptModel.class, new ModelSerializer());
        gson.setExclusionStrategies(new ParameterExclusion());
    }

    public GptAPI(GptModel model, GptTool[] customTools) {
        this.body = new GptRequest(model, customTools);
        gson.registerTypeAdapter(GptModel.class, new ModelSerializer());
        gson.setExclusionStrategies(new ParameterExclusion());
    }

    public GptAPI(GptRequest request) {
        this.body = request;
        gson.registerTypeAdapter(GptModel.class, new ModelSerializer());
        gson.setExclusionStrategies(new ParameterExclusion());
    }

    public GptAPI addContext(String context, String name) {
        if (this.messageMap.containsKey(name)) {
            this.body.replaceMessage(messageMap.get(name), context);
            return this;
        }
        this.body.addMessage("system", context);
        this.messageMap.put(name, this.body.getMessagesSize() - 1);
        return this;
    }

    public GptAPI addContext(String context, String name, int index) {
        if (this.messageMap.containsKey(name)) {
            this.body.replaceMessage(messageMap.get(name), context);
            return this;
        }
        this.body.addMessage("system", context);
        for (String key : messageMap.keySet()) {
            if (messageMap.get(key) == index) {
                messageMap.replace(key, index + 1);
            }
        }
        this.messageMap.put(name, index);
        return this;
    }

    public GptAPI setTools(GptTool[] tools) {
        this.body.setTools(tools);
        return this;
    }

    public GptAPI addLogs(String Logs, String name) {
        if (this.messageMap.containsKey(name)) {
            this.body.replaceMessage(messageMap.get(name), Logs);
            return this;
        }
        this.body.addMessage("user", Logs);
        this.messageMap.put(name, this.body.getMessagesSize() - 1);
        return this;
    }

    public GptAPI addLogs(String Logs, String name, int index) {
        if(this.body.getMessagesSize() <= index){
            addLogs(Logs, name);
            return this;
        }
        if (this.messageMap.containsKey(name)) {
            this.body.replaceMessage(messageMap.get(name), Logs);
            return this;
        }
        this.body.addMessage("user", Logs, index);
        for (String key : messageMap.keySet()) {
            if (messageMap.get(key) == index) {
                messageMap.replace(key, index + 1);
            }
        }
        this.messageMap.put(name, index);
        return this;
    }

    public GptAPI setToolChoice(Object tool_choice) {
        this.body.setTool_choice(tool_choice);
        return this;
    }

    public void removeLastMessage() {
        this.body.removeLastMessage();
    }

    public int getMaxTokens() {
        return body.getModel().getTokenLimit();
    }

    public String getModelName() {
        return body.getModel().getName();
    }

    private InferenceProvider providerName(FileConfiguration config){
        String configured = config.getString("inference-provider", "openai");
        if (configured == null) {
            return InferenceProvider.OPENAI;
        }
        try {
            return InferenceProvider.valueOf(configured.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            GPTGOD.LOGGER.warn("Invalid inference-provider '" + configured + "', defaulting to OPENAI");
            return InferenceProvider.OPENAI;
        }
    }

    private boolean providerIsOpenAICompatible(InferenceProvider provider){
        return switch (provider) {
            case OPENAI, OPENROUTER, LMSTUDIO, GENERIC, NIM -> true;
            case OLLAMA -> false;
        };
    }

    private String stripTrailingSlash(String input) {
        String out = input == null ? "" : input.trim();
        while (out.endsWith("/")) {
            out = out.substring(0, out.length() - 1);
        }
        return out;
    }

    private String resolveOpenAiCompatibleChatUrl(String baseUrl) {
        String base = stripTrailingSlash(baseUrl);
        if (base.endsWith("/chat/completions")) {
            return base;
        }
        if (base.endsWith("/v1")) {
            return base + "/chat/completions";
        }
        return base + "/v1/chat/completions";
    }

    private String resolveChatUrl(FileConfiguration config, InferenceProvider provider){
        return switch(provider){
            case OPENROUTER -> resolveOpenAiCompatibleChatUrl(config.getString("openRouterUrl", "https://api.openrouter.ai/v1"));
            case OLLAMA -> stripTrailingSlash(config.getString("ollamaUrl", "http://localhost:11434")) + "/api/generate";
            case LMSTUDIO -> resolveOpenAiCompatibleChatUrl(config.getString("lmstudioUrl", "http://localhost:1234"));
            case GENERIC -> resolveOpenAiCompatibleChatUrl(config.getString("genericUrl", "https://api.your-provider.example"));
            case NIM -> resolveOpenAiCompatibleChatUrl(config.getString("nimUrl", "https://integrate.api.nvidia.com/v1"));
            case OPENAI -> "https://api.openai.com/v1/chat/completions";
        };
    }

    private String resolveAuthHeader(FileConfiguration config, InferenceProvider provider){
        return switch(provider){
            case OPENROUTER -> "Bearer " + config.getString("openRouterKey", "");
            case OPENAI -> "Bearer " + config.getString("openAiKey", "");
            case GENERIC -> "Bearer " + config.getString("genericKey", "");
            case NIM -> "Bearer " + config.getString("nimKey", "");
            case OLLAMA, LMSTUDIO -> null;
        };
    }

    private String messagesToPrompt(){
        // convert chat messages to a single textual prompt for providers that expect a prompt string
        StringBuilder prompt = new StringBuilder();
        try{
            for (var m : this.body.getMessages()){
                String role = m.getRole() == null ? "user" : m.getRole();
                String content = m.getContent() == null ? "" : m.getContent();
                prompt.append(role).append(": ").append(content).append("\n");
            }
        } catch(Exception e){
            // fallback to serializing the whole body
            prompt.append(gson.create().toJson(this.body));
        }
        return prompt.toString();
    }

    private String extractTextFromProviderResponse(String raw){
        try{
            var json = gson.fromJson(raw, com.google.gson.JsonElement.class);
            if(json.isJsonObject()){
                var obj = json.getAsJsonObject();
                // common fields used by various local servers
                if(obj.has("text")) return obj.get("text").getAsString();
                if(obj.has("response")) return obj.get("response").getAsString();
                if(obj.has("result")) return obj.get("result").getAsString();
                // try a nested 'choices[0].message.content' like OpenAI
                if(obj.has("choices")){
                    var choices = obj.getAsJsonArray("choices");
                    if(choices.size() > 0){
                        var first = choices.get(0).getAsJsonObject();
                        if(first.has("message")){
                            var msg = first.getAsJsonObject("message");
                            if(msg.has("content")) return msg.get("content").getAsString();
                        }
                        if(first.has("text")) return first.get("text").getAsString();
                    }
                }
            }
        } catch(Exception e){
            GPTGOD.LOGGER.warn("Failed to parse provider response body. Error: " + e.getMessage() + " | Raw response: " + raw);
        }
        // if we couldn't parse, return raw trimmed
        return raw == null ? "" : raw.trim();
    }

    private StringEntity buildRequestPayload(FileConfiguration config, InferenceProvider provider) {
        if(providerIsOpenAICompatible(provider)){
            var payload = new com.google.gson.JsonObject();
            payload.add("messages", gson.create().toJsonTree(body.getMessages()));
            payload.addProperty("model", body.getModel().getName());
            if(body.getTools() != null && body.getTools().length > 0){
                payload.add("tools", gson.create().toJsonTree(body.getTools()));
                if(body.getTool_choice() != null){
                    payload.add("tool_choice", gson.create().toJsonTree(body.getTool_choice()));
                }
            }
            if(provider == InferenceProvider.NIM){
                String extra = config.getString("nimExtraBody", "{}");
                try{
                    var extraJson = com.google.gson.JsonParser.parseString(extra).getAsJsonObject();
                    for(var entry : extraJson.entrySet()){
                        payload.add(entry.getKey(), entry.getValue());
                    }
                } catch(Exception e){
                    GPTGOD.LOGGER.warn("Invalid nimExtraBody JSON, ignoring. Error: " + e.getMessage() + " | Value: " + extra);
                }
            }
            return new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);
        }

        var map = new java.util.HashMap<String, Object>();
        map.put("model", body.getModel().getName());
        map.put("prompt", messagesToPrompt());
        return new StringEntity(gson.create().toJson(map), ContentType.APPLICATION_JSON);
    }

    private String normalizeResponseForActions(String raw, InferenceProvider provider) {
        if(providerIsOpenAICompatible(provider)){
            return raw;
        }

        String text = extractTextFromProviderResponse(raw);
        var synthetic = new com.google.gson.JsonObject();
        synthetic.addProperty("id", provider.name().toLowerCase() + "-" + System.currentTimeMillis());
        synthetic.addProperty("object", "chat.completion");
        synthetic.addProperty("created", (int) (System.currentTimeMillis() / 1000));
        synthetic.addProperty("model", body.getModel().getName());
        var choices = new com.google.gson.JsonArray();
        var choice = new com.google.gson.JsonObject();
        choice.addProperty("index", 0);
        var message = new com.google.gson.JsonObject();
        message.addProperty("role", "assistant");
        message.addProperty("content", text);
        choice.add("message", message);
        choice.addProperty("finish_reason", "stop");
        choices.add(choice);
        synthetic.add("choices", choices);
        String out = gson.create().toJson(synthetic);
        GPTGOD.LOGGER.info("mapped " + provider.name().toLowerCase() + " response to OpenAI format: " + out);
        return out;
    }

    private void internalSend(Map<String, GptFunction> functions, int clearAfterTicks) {
        pool.execute(() -> {
            this.isSending = true;
            FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
            InferenceProvider provider = providerName(config);

            try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                String url = resolveChatUrl(config, provider);
                StringEntity data = buildRequestPayload(config, provider);
                HttpPost post = new HttpPost(url);
                post.setEntity(data);

                String auth = resolveAuthHeader(config, provider);
                if(auth != null && !auth.isBlank()) {
                    post.setHeader(HttpHeaders.AUTHORIZATION, auth);
                }

                GPTGOD.LOGGER.info("POSTING (" + provider.name().toLowerCase() + ") " + gson.setPrettyPrinting().create().toJson(body));
                GPTGOD.LOGGER.info("Making POST request to " + url);

                try (CloseableHttpResponse response = client.execute(post)) {
                    String raw = EntityUtils.toString(response.getEntity());
                    GPTGOD.LOGGER.info("received response from " + provider.name().toLowerCase() + ": " + raw);

                    if (response.getStatusLine().getStatusCode() != 200) {
                        GPTGOD.LOGGER.warn("API call failed with status " + response.getStatusLine().getStatusCode());
                        this.isSending = false;
                        return;
                    }

                    try {
                        String normalized = normalizeResponseForActions(raw, provider);
                        if (functions == null) {
                            GptActions.processResponse(normalized);
                        } else {
                            GptActions.processResponse(normalized, functions);
                        }
                    } catch (RuntimeException e) {
                        GPTGOD.LOGGER.error("Failed to process model response", e);
                    }
                }
            } catch (IOException e) {
                GPTGOD.LOGGER.error("There was an error making a request to GPT", e);
                this.isSending = false;
            }

            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(GPTGOD.class), () -> {
                this.isSending = false;
            }, clearAfterTicks);
        });
    }

    public void send() {
        internalSend(null, 10);
    }

    public void send(Map<String, GptFunction> functions) {
        internalSend(functions, 20);
    }

    public boolean isSending() {
        return isSending;
    }

    // DEBUG method
    public void checkRequestBody() {
        GPTGOD.LOGGER.info("POSTING " + gson.setPrettyPrinting().create().toJson(body));
    }
}
