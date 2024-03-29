package net.bigyous.gptgodmc;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import net.bigyous.gptgodmc.utils.CompareLoggables;
import net.bigyous.gptgodmc.utils.GPTUtils;
import net.bigyous.gptgodmc.GPT.SummarizeLogs;
import net.bigyous.gptgodmc.loggables.Loggable;

public class EventLogger {
    // private static List<Loggable> loggables = new ArrayList<>();
    private static TreeSet<Loggable> loggables = new TreeSet<>(new CompareLoggables());
    private static int totalTokens = 0;
    private static String summary = null;
    private static String overflow = "";
    private static boolean generatingSummary = false;

    public static void addLoggable(Loggable event) {
        if (loggables.size() > 0) {
            Loggable last = loggables.last();
            // try combine
            if (!last.combine(event)) {
                // if not combined, add to list
                loggables.add(event);
                // calculate the tokens ahead of time
                totalTokens += event.getTokens();
            }
            else{
                // adjust total tokens after combining logs
                totalTokens -= last.getTokens();
                last.resetTokens();
                totalTokens += last.getTokens();
            }
        } else {
            // If empty, just add
            loggables.add(event);
            totalTokens += event.getTokens();
        }
    }

    // remove logs until the total tokens fits within the limit of 
    public static void cull(int tokenLimit){
        int serverInfoTokens = GPTUtils.countTokens(ServerInfoSummarizer.getStatusSummary());
        while(totalTokens + serverInfoTokens > tokenLimit){
            Loggable oldest = loggables.first();
            overflow = overflow + "\n" + oldest;
            totalTokens -= oldest.getTokens();
            loggables.remove(oldest);
        }
    }

    public static List<String> flushLogs() {
        List<String> logs = new ArrayList<>();

        // Include status summary at beginning
        logs.add(
            ServerInfoSummarizer.getStatusSummary()
        );
        
        for (Loggable event: loggables) {
            String log = event.getLog();
            if(log != null){
                logs.add(log);
            }
        }

        // Clear events
        
        loggables.clear();

        return logs;
    }

    public static String dump() {
        String out = String.join("\n", flushLogs());
        summarize(out);
        return out;
    }
    public static boolean hasSummary(){
        return summary != null;
    }
    public static String getSummary(){
        return summary;
    }

    public static void setSummary(String newSummary){
        GPTGOD.LOGGER.info("new summary: ", newSummary);
        summary = newSummary;
        generatingSummary = false;
    }

    private static void summarize(String logs){
        String tempSummary = summary;
        summary = null;
        SummarizeLogs.summarize(overflow + "\n" + logs, tempSummary);
        generatingSummary = true;
        overflow = "";
    }

    public static boolean isGeneratingSummary() {
        return generatingSummary;
    }
}
