package net.bigyous.gptgodmc.utils;

import java.util.Arrays;
import java.util.Random;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;

import net.bigyous.gptgodmc.GPT.Json.GptTool;
public class GPTUtils {
    private static EncodingRegistry registry = Encodings.newLazyEncodingRegistry();
    private static Encoding encoding = registry.getEncoding(EncodingType.CL100K_BASE);

    public static int countTokens(String message){
        if(message == null){
            return 0;
        }
        return encoding.countTokens(message);
    }

    public static GptTool[] randomToolSubset(GptTool[] tools, int size){
        Random r = new Random();

        for(int i = tools.length - 1; i > 0; i--){
            int j = r.nextInt(i+1);

            GptTool temp = tools[i];
            tools[i] = tools[j];
            tools[j] = temp;
        }
        return Arrays.copyOfRange(tools, 0, size);
    }

    public static int calculateToolTokens(GptTool[] tools) {
        int sum = 0;
        for(GptTool tool : tools){
            sum += tool.getFunction().calculateFunctionTokens();
        }
        return sum;
    }

    public static <T> T[] concatWithArrayCopy(T[] array1, T[] array2) {
        T[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
}
