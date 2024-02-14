package net.bigyous.gptgodmc.GPT.Json;

public class Choice {
    private int index;
    private ResponseMessage message;
    private boolean logprobs;
    private String finish_reason;

    public int getIndex() {
        return index;
    }

    public String getFinish_reason() {
        return finish_reason;
    }

    public ResponseMessage getMessage() {
        return message;
    }
}
