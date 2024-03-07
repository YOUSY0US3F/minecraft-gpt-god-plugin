package net.bigyous.gptgodmc.loggables;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import net.bigyous.gptgodmc.utils.GPTUtils;

public class BaseLoggable implements Loggable {
    private Instant timestamp;
    private int tokens = -1;

    public BaseLoggable() {
        timestamp = Instant.now();
    }

    public BaseLoggable(Instant timestamp) {
        this.timestamp = timestamp;
    }

    protected String getFormattedTimestamp() {
        ZonedDateTime zdt = timestamp.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = zdt.format(formatter);
        return "[" + formattedTime + "]: ";
    }

    public String getLog() {
        return getFormattedTimestamp();
    }

    public Instant getRawInstant(){
        return timestamp;
    }

    public void setTimestamp(Instant time){
        this.timestamp = time;
    }

    public boolean combine(Loggable l) {
        return false;
    }

    public int getTokens(){
        if(tokens<0){
            this.tokens = GPTUtils.countTokens(getLog());
        }
        return tokens;
    }
    public void resetTokens(){
        tokens = -1;
    }
}
