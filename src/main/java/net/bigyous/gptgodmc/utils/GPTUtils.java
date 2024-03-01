package net.bigyous.gptgodmc.utils;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
public class GPTUtils {
    private static EncodingRegistry registry = Encodings.newLazyEncodingRegistry();
    private static Encoding encoding = registry.getEncoding(EncodingType.CL100K_BASE);

    public static int countTokens(String message){
        if(message == null){
            return 0;
        }
        return encoding.countTokens(message);
    }
}
