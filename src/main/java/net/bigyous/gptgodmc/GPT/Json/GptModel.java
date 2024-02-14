package net.bigyous.gptgodmc.GPT.Json;

public class GptModel {
    private String name;
    private int tokenLimit;

    public GptModel(String name, int tokenLimit){
        this.name = name;
        this.tokenLimit = tokenLimit;
    }

    public String getName() {
        return name;
    }

    public int getTokenLimit() {
        return tokenLimit;
    }
}
