package net.bigyous.gptgodmc.GPT.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ModelSerializer implements JsonSerializer<GptModel> {
    @Override
    public JsonElement serialize(GptModel src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.getName());
    }
}
