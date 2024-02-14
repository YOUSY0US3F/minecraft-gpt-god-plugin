package net.bigyous.gptgodmc.GPT.Json;

public class ToolCall {
    private String id;
    private String type;
    private FunctionCall function;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public FunctionCall getFunction() {
        return function;
    }
}
