package de.canstein_berlin.customrecipes.api.recipes.serializer;

import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.api.requirements.BaseRequirement;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeSerializerFactory {

    private static RecipeSerializerFactory instance;

    private final HashMap<String, BaseRecipeSerializer> parsers;
    private final HashMap<String, BaseRequirement> requirements;

    private RecipeSerializerFactory() {
        instance = this;
        parsers = new HashMap<>();
        requirements = new HashMap<>();
    }

    public static RecipeSerializerFactory getInstance() {
        if (instance == null) return new RecipeSerializerFactory();
        return instance;
    }

    public CustomRecipe loadFromFile(JavaPlugin plugin, File file) throws IOException, MalformedRecipeFileException, InvalidRecipeValueException {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(Files.readString(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (!jsonObject.has("type")) {
            throw new MalformedRecipeFileException("Missing element \"type\"");
        }
        String type = jsonObject.getString("type");

        CustomRecipe recipe = null;
        for (Map.Entry<String, BaseRecipeSerializer> entry : parsers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(type)) {
                recipe = entry.getValue().parse(jsonObject, plugin, file.getName());
                break;
            }
        }

        if (recipe == null) throw new InvalidRecipeValueException("Unknown Recipe type " + type);

        if (jsonObject.has("requirements")) {
            JSONArray jsonArray = jsonObject.optJSONArray("requirements");
            if (jsonArray == null) throw new InvalidRecipeValueException("Requirements must be list");

            ArrayList<BaseRequirement> currentRequirements = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject requirementObject = jsonArray.optJSONObject(i);
                if (requirementObject == null)
                    throw new MalformedRecipeFileException("Requirement must be a json object");

                if (!requirementObject.has("type"))
                    throw new MalformedRecipeFileException("Requirement must contain type value");

                String requirementType = requirementObject.getString("type");
                if (requirements.containsKey(requirementType)) {
                    currentRequirements.add(requirements.get(requirementType).parse(requirementObject));
                }
            }

            recipe.setRequirements(currentRequirements);
        }

        return recipe;
    }

    public JSONObject customRecipeToJsonObject(CustomRecipe recipe) {

        System.out.println(recipe.getRecipe().getClass());

        //Recipe Json
        JSONObject recipeJson = null;
        for (BaseRecipeSerializer serializer : parsers.values()) {
            if (serializer.getRecipeClass().equals(recipe.getRecipe().getClass())) {
                recipeJson = serializer.serialize(recipe.getRecipe());
                break;
            }
        }

        if (recipeJson == null) return null;

        //Requirements
        JSONArray jsonArray = new JSONArray();
        for (BaseRequirement requirement : recipe.getRequirements()) {
            JSONObject jsonObject = requirement.serialize();
            jsonArray.put(jsonObject);
        }

        if (!jsonArray.isEmpty()) recipeJson.put("requirements", jsonArray);

        return recipeJson;
    }

    public HashMap<String, BaseRecipeSerializer> getParsers() {
        return parsers;
    }

    public void addParser(BaseRecipeSerializer recipeParser) {
        parsers.put(recipeParser.getId(), recipeParser);
    }

    public void addRequirement(BaseRequirement requirement) {
        requirements.put(requirement.getId(), requirement);
    }
}
