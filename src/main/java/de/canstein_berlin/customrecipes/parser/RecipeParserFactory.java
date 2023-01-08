package de.canstein_berlin.customrecipes.parser;

import de.canstein_berlin.customrecipes.CustomRecipes;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.parser.types.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RecipeParserFactory {

    public static RecipeParser loadFromFile(JavaPlugin plugin, String path) throws MalformedRecipeFileException {
        return loadFromFile(plugin, new File(CustomRecipes.getInstance().getDataFolder(), path));
    }

    public static RecipeParser loadFromFile(JavaPlugin plugin, File file) throws MalformedRecipeFileException {
        JSONObject jsonObject;
        try {
            jsonObject = toJsonObject(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if(!jsonObject.has("type")){
            throw new MalformedRecipeFileException("Missing element \"type\"");
        }
        String type = jsonObject.getString("type");
        return switch (type) {
            case "minecraft:crafting_shaped" -> new CraftingShapedRecipeParser(plugin, file, jsonObject);
            case "minecraft:crafting_shapeless" -> new CraftingShapelessRecipeParser(plugin, file, jsonObject);
            case "minecraft:smelting" -> new SmeltingRecipeParser(plugin, file, jsonObject);
            case "minecraft:blasting" -> new BlastingRecipeParser(plugin, file, jsonObject);
            case "minecraft:smoking" -> new SmokingRecipeParser(plugin, file, jsonObject);
            case "minecraft:campfire_cooking" -> new CampfireCookingRecipeParser(plugin, file, jsonObject);
            case "minecraft:stonecutting" -> new StonecuttingRecipeParser(plugin, file, jsonObject);
            case "minecraft:smithing" -> new SmithingRecipeParser(plugin, file, jsonObject);

            default -> throw new MalformedRecipeFileException("Unknown recipe type \"" + type + "\" whilst registering recipe for plugin " + plugin.getName());
        };
    }

    private static JSONObject toJsonObject(File file) throws IOException {
        return new JSONObject(Files.readString(file.toPath()));
    }

}
