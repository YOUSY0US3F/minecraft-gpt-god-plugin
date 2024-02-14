package net.bigyous.gptgodmc.GPT.Json;

public class ResponseMessage {
    private String role;
    private String content;
    private ToolCall[] tool_calls;

    public String getContent() {
        return content;
    }
    public String getRole() {
        return role;
    }

    public ToolCall[] getTool_calls() {
        return tool_calls;
    }
}
