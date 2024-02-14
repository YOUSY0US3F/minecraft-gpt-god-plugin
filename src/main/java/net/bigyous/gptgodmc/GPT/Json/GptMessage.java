package net.bigyous.gptgodmc.GPT.Json;

public class GptMessage {
    private String role;
    private String content;
    public GptMessage(String role, String content){
        this.role = role;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getRole() {
        return role;
    }


}
