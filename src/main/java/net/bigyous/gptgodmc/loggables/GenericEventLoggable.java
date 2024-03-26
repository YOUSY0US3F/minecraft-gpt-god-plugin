package net.bigyous.gptgodmc.loggables;

public class GenericEventLoggable extends BaseLoggable{
    private String text;

    public GenericEventLoggable(String text){
        this.text = text;
    }

    @Override
    public String getLog() {
        return text;
    }
}
