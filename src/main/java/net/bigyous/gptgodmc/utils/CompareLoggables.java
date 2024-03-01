package net.bigyous.gptgodmc.utils;
import java.util.Comparator;
import net.bigyous.gptgodmc.loggables.Loggable;

public class CompareLoggables implements Comparator<Loggable>{

    @Override
    public int compare(Loggable a, Loggable b) {
        return a.getRawInstant().compareTo(b.getRawInstant());
    }
    
}