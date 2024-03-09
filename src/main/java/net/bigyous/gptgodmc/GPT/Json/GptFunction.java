package net.bigyous.gptgodmc.GPT.Json;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.interfaces.Function;
import net.bigyous.gptgodmc.utils.GPTUtils;
import java.util.Map;

public class GptFunction {
    private String name;
    private String description;
    private FunctionParameters parameters;
    private transient Function<String> function;

    public GptFunction (String name, String description, Map<String, Parameter> params, Function<String> function){
        this.name = name;
        this.description = description;
        this.parameters = new FunctionParameters("object", params);
        this.function = function;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public FunctionParameters getParameters() {
        return parameters;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParameters(FunctionParameters parameters) {
        this.parameters = parameters;
    }

    public Function<String> getFunction() {
        return function;
    }

    public void runFunction(String jsonArgs){
        GPTGOD.LOGGER.info(String.format("%s invoked", this.name));
        function.run(jsonArgs);
    }

    public int calculateFunctionTokens(){
        return GPTUtils.countTokens(name) + GPTUtils.countTokens(description) + parameters.calculateParameterTokens();

    }
}

class FunctionParameters {
    private String type;
    private Map<String, Parameter> properties;

    public FunctionParameters(String type, Map<String, Parameter> properties){
        this.type = type;
        this.properties = properties;
    }

    public Map<String, Parameter> getProperties() {
        return properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProperties(Map<String, Parameter> properties) {
        this.properties = properties;
    }

    public int calculateParameterTokens(){
        int sum = 0;
        for(Parameter param: properties.values()){
            sum+= GPTUtils.countTokens(param.getType()) + GPTUtils.countTokens(param.getDescription());
        }
        return sum;
    }
}

