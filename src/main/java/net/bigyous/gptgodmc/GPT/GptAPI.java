package net.bigyous.gptgodmc.GPT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.GsonBuilder;

import net.bigyous.gptgodmc.Config;
import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.GptFunction;
import net.bigyous.gptgodmc.GPT.Json.GptModel;
import net.bigyous.gptgodmc.GPT.Json.GptRequest;
import net.bigyous.gptgodmc.GPT.Json.GptTool;
import net.bigyous.gptgodmc.GPT.Json.ModelSerializer;
import net.bigyous.gptgodmc.GPT.Json.ParameterExclusion;

public class GptAPI {
    private GsonBuilder gson = new GsonBuilder();
    private CloseableHttpClient client;
    private GptRequest body;
    private String CHATGPTURL = "https://api.openai.com/v1/chat/completions";
    private Map<String, Integer> messageMap = new HashMap<String, Integer>();
    public GptAPI (GptModel model){
        this.body = new GptRequest(model, GptActions.GetAllTools());
        gson.registerTypeAdapter(GptModel.class, new ModelSerializer());
        gson.setExclusionStrategies(new ParameterExclusion());
        this.client = HttpClientBuilder.create().build();
    }
    public GptAPI (GptModel model, GptTool[] customTools){
        this.body = new GptRequest(model, customTools);
        gson.registerTypeAdapter(GptModel.class, new ModelSerializer());
        gson.setExclusionStrategies(new ParameterExclusion());
        this.client = HttpClientBuilder.create().build();
    }
    public GptAPI(GptRequest request){
        this.body = request;
        gson.registerTypeAdapter(GptModel.class, new ModelSerializer());
        gson.setExclusionStrategies(new ParameterExclusion());
        this.client = HttpClientBuilder.create().build();
    }
    public GptAPI addContext(String context, String name){
        if(this.messageMap.containsKey(name)){
            this.body.replaceMessage(messageMap.get(name), context);
            return this;
        }
        this.body.addMessage("system", context);
        this.messageMap.put(name, this.body.getMessagesSize()-1);
        return this;
    }
    public GptAPI addLogs(String Logs, String name){
        if(this.messageMap.containsKey(name)){
            this.body.replaceMessage(messageMap.get(name), Logs);
            return this;
        }
        this.body.addMessage("user", Logs);
        this.messageMap.put(name, this.body.getMessagesSize()-1);
        return this;
    }
    public GptAPI setToolChoice(Object tool_choice){
        this.body.setTool_choice(tool_choice);
        return this;
    }
    public void removeLastMessage(){
        this.body.removeLastMessage();
    }
    public void send(){
        Thread worker = new Thread(()->{
            StringEntity data =new StringEntity(gson.create().toJson(body),ContentType.APPLICATION_JSON);
            GPTGOD.LOGGER.info("POSTING " + gson.setPrettyPrinting().create().toJson(body));
            HttpPost post = new HttpPost(CHATGPTURL);
            post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + Config.openAiKey);
            GPTGOD.LOGGER.info("Making POST request");
            post.setEntity(data);
            try {
                HttpResponse response = client.execute(post);
                String out = new String(response.getEntity().getContent().readAllBytes());
                EntityUtils.consume(response.getEntity());
                GPTGOD.LOGGER.info("recieved response from OpenAI: " + out);
                if(response.getStatusLine().getStatusCode() != 200){
                    GPTGOD.LOGGER.warn("API call failed");
                    Thread.currentThread().interrupt();
                }
                GptActions.processResponse(out);
                client.close();
            } catch (IOException e) {
                GPTGOD.LOGGER.error("There was an error maing a request to GPT", e);
            }
            Thread.currentThread().interrupt();
        });
        worker.start();
    }
    public void send(Map<String,GptFunction> functions){
        Thread worker = new Thread(()->{
            StringEntity data =new StringEntity(gson.create().toJson(body),ContentType.APPLICATION_JSON);
            GPTGOD.LOGGER.info("POSTING " + gson.setPrettyPrinting().create().toJson(body));
            HttpPost post = new HttpPost(CHATGPTURL);
            post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + Config.openAiKey);
            GPTGOD.LOGGER.info("Making POST request");
            post.setEntity(data);
            try {
                HttpResponse response = client.execute(post);
                String out = new String(response.getEntity().getContent().readAllBytes());
                EntityUtils.consume(response.getEntity());
                GPTGOD.LOGGER.info("recieved response from OpenAI: " + out);
                if(response.getStatusLine().getStatusCode() != 200){
                    GPTGOD.LOGGER.warn("API call failed");
                    Thread.currentThread().interrupt();
                }
                GptActions.processResponse(out, functions);
                client.close();
            } catch (IOException e) {
                GPTGOD.LOGGER.error("There was an error maing a request to GPT", e);
            }
            Thread.currentThread().interrupt();
        });
        worker.start();
    }

    //DEBUG method
    public void checkRequestBody(){
        GPTGOD.LOGGER.info("POSTING " + gson.setPrettyPrinting().create().toJson(body));
    }
}
