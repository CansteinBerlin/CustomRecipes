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

    /**
     * This is how you get an instance of this class!!!
     */
    public static RecipeSerializerFactory getInstance() {
        if (instance == null) return new RecipeSerializerFactory();
        return instance;
    }

    /**
     * Loads a custom recipe from a file. Parses both recipe and requirements
     *
     * @param plugin the plugin that wants to parse to file
     * @param file   The file that contains the recipe
     * @return Custom recipe object with the namespace, recipe and requirements parsed fron the file
     * @throws IOException                  Thrown if an error occurs while reading the file
     * @throws MalformedRecipeFileException Recipe file malformed
     * @throws InvalidRecipeValueException  Recipe contains invalid values
     */
    public CustomRecipe loadFromFile(JavaPlugin plugin, File file) throws IOException, MalformedRecipeFileException, InvalidRecipeValueException {
        JSONObject jsonObject;
        jsonObject = new JSONObject(Files.readString(file.toPath()));
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

    /**
     * Serializes a custom Recipe to a json object.
     *
     * @param recipe The recipe to be serialized
     * @return Serialized json object
     */
    public JSONObject customRecipeToJsonObject(CustomRecipe recipe) {
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

    /**
     * Adds a new parser to be used while reading recipes
     *
     * @param recipeParser
     */
    public void addParser(BaseRecipeSerializer recipeParser) {
        parsers.put(recipeParser.getId(), recipeParser);
    }

    /**
     * Adds a new requirement to be used while parsing requirements
     *
     * @param requirement
     */
    public void addRequirement(BaseRequirement requirement) {
        requirements.put(requirement.getId(), requirement);
    }
}
