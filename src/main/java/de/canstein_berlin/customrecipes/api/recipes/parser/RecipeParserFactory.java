package de.canstein_berlin.customrecipes.api.recipes.parser;

import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class RecipeParserFactory {

    private static RecipeParserFactory instance;

    private final HashMap<String, BaseRecipeParser> parsers;

    private RecipeParserFactory() {
        instance = this;
        parsers = new HashMap<>();
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
        for (Map.Entry<String, BaseRecipeParser> entry : parsers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(type))
                return entry.getValue().parse(jsonObject, plugin, file.getName());
        }
        throw new InvalidRecipeValueException("Unknown Recipe type " + type);
    }

    public HashMap<String, BaseRecipeParser> getParsers() {
        return parsers;
    }

    public void addParser(BaseRecipeParser recipeParser) {
        parsers.put(recipeParser.getId(), recipeParser);
    }
}
