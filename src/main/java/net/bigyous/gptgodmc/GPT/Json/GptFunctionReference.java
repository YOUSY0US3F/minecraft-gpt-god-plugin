package net.bigyous.gptgodmc.GPT.Json;

import java.util.Map;

public class GptFunctionReference {
    private String type;
    private Map<String,String> function;

    public GptFunctionReference(GptFunction function){
        this.type = "function";
        this.function = Map.of("name", function.getName());
    }
}
