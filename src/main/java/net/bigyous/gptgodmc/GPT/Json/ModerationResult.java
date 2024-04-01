package net.bigyous.gptgodmc.GPT.Json;

import java.util.ArrayList;
import java.util.Map;

public class ModerationResult {
    private boolean flagged;
    private Map<String, Boolean> categories;

    public String getCategories(){
        ArrayList<String> flags = new ArrayList<String>();
        for(String category : categories.keySet()){
            if(categories.get(category)){
                flags.add(category);
            }
        }
        return String.join(", ", flags);
    }

    public boolean isFlagged() {
        return flagged;
    }
}
