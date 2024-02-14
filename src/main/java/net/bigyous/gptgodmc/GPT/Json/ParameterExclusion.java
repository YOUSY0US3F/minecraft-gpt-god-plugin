package net.bigyous.gptgodmc.GPT.Json;

import com.google.common.reflect.Parameter;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ParameterExclusion implements ExclusionStrategy  {
    public boolean shouldSkipField(FieldAttributes f) {
        return f.equals(null);
  }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.equals(null);
    }
}
