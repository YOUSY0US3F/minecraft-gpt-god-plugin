package net.bigyous.gptgodmc.GPT.Json;

import java.util.Map;

public class GptResponse {
    private String id;
    private String object;
    private int created;
    private String model;
    private Choice[] choices;

    public Choice[] getChoices() {
        return choices;
    }
    public int getCreated() {
        return created;
    }
    public String getId() {
        return id;
    }
    public String getModel() {
        return model;
    }

    public String getObject() {
        return object;
    }
}
