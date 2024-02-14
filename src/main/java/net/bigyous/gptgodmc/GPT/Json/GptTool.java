package net.bigyous.gptgodmc.GPT.Json;

public class GptTool {
    private String type;
    private GptFunction function;

    public GptTool(GptFunction function){
        this.type = "function";
        this.function = function;
    }
    public String getType() {
        return type;
    }

    public GptFunction getFunction() {
        return function;
    }
}
