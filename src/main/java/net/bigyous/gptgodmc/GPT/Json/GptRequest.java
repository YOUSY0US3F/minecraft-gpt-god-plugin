package net.bigyous.gptgodmc.GPT.Json;

import java.util.ArrayList;

public class GptRequest {
    private GptModel model;
    private ArrayList<GptMessage> messages;
    private GptTool[] tools;
    private Object tool_choice = "auto";
    public GptRequest(GptModel model, GptTool[] tools){
        this.model = model;
        this.tools = tools;
        this.messages = new ArrayList<GptMessage>();
    }
    public GptModel getModel() {
        return model;
    }

    public GptTool[] getTools() {
        return tools;
    }

    public void setModel(GptModel model) {
        this.model = model;
    }

    public void addMessage(String role, String content){
        messages.add(new GptMessage(role, content));
    }

    public void clearMessages(){
        this.messages = new ArrayList<GptMessage>();
    }

    public void removeLastMessage(){
        this.messages.remove(messages.size()-1);
    }

    public void setTool_choice(Object tool_choice) {
        this.tool_choice = tool_choice;
    }

    public Object getTool_choice() {
        return tool_choice;
    }

    public int getMessagesSize(){
        return messages.size();
    }

    public void replaceMessage(int index, String message){
        this.messages.set(index, new GptMessage(this.messages.get(index).getRole(), message));
    }

}
