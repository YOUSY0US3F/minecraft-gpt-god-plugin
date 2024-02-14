package net.bigyous.gptgodmc.GPT.Json;

import java.util.Map;

public class Parameter {
    private String type;
    private String description;
    private Map<String,String> items;

    public Parameter(String type, String description){
        this.type = type;
        this.description = description;
        this.items = null;
    }
    public Parameter(String type, String description, String itemType ){
        this.type = type;
        this.description = description;
        this.items = Map.of("type", itemType);
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getItems() {
        return items;
    }
}
