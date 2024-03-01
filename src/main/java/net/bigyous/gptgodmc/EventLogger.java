package net.bigyous.gptgodmc;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import net.bigyous.gptgodmc.utils.CompareLoggables;
import net.bigyous.gptgodmc.loggables.Loggable;

public class EventLogger {
    // private static List<Loggable> loggables = new ArrayList<>();
    private static TreeSet<Loggable> loggables = new TreeSet<>(new CompareLoggables());

    public static void addLoggable(Loggable event) {
        if (loggables.size() > 0) {
            Loggable last = loggables.last();
            // try combine
            if (!last.combine(event)) {
                // if not combined, add to list
                loggables.add(event);
            }
        } else {
            // If empty, just add
            loggables.add(event);
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
        // TODO: Summarize instead
        loggables.clear();

        return logs;
    }

    public static String dump() {
        return String.join("\n", flushLogs());
    }
}
