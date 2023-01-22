package de.canstein_berlin.customrecipes.api.recipes.parser;

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

public class RecipeParserFactory {

    private static RecipeParserFactory instance;

    private final HashMap<String, BaseRecipeParser> parsers;
    private final HashMap<String, BaseRequirement> requirements;

    private RecipeParserFactory() {
        instance = this;
        parsers = new HashMap<>();
        requirements = new HashMap<>();
    }

    public static RecipeParserFactory getInstance() {
        if (instance == null) return new RecipeParserFactory();
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
        for (Map.Entry<String, BaseRecipeParser> entry : parsers.entrySet()) {
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

            System.out.println(currentRequirements);
            recipe.setRequirements(currentRequirements);
        }

        return recipe;
    }

    public HashMap<String, BaseRecipeParser> getParsers() {
        return parsers;
    }

    public void addParser(BaseRecipeParser recipeParser) {
        parsers.put(recipeParser.getId(), recipeParser);
    }

    public void addRequirement(BaseRequirement requirement) {
        requirements.put(requirement.getId(), requirement);
    }
}
