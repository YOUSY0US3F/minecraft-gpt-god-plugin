package net.bigyous.gptgodmc.loggables;

public class GPTActionLoggable extends BaseLoggable {
    private String text;

    public GPTActionLoggable(String text){
        this.text = text;
    }

    @Override
    public String getLog() {
        return text;
    }
}
