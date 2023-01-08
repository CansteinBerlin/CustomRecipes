package de.canstein_berlin.customrecipes.parser.types;

import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.parser.RecipeParser;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.File;

public class SmithingRecipeParser extends RecipeParser {

    public SmithingRecipeParser(JavaPlugin plugin, File file, JSONObject jsonObject) {
        super(plugin, file, jsonObject);
    }

    @Override
    public Recipe parseRecipe() throws MalformedRecipeFileException, InvalidRecipeValueException {
        String nameKey = recipeJson.optString("name", file.getName().split("\\.")[0]);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, nameKey);

        //Result Itemstack
        if(!recipeJson.has("result")) throw new MalformedRecipeFileException("Missing element \"result\"");
        ItemStack result = getItemStack(recipeJson.get("result"));

        //Base Item
        if(!recipeJson.has("base")) throw new MalformedRecipeFileException("Missing element \"base\"");
        RecipeChoice base = getRecipeChoice(recipeJson.get("base"));

        //Addition Item
        if(!recipeJson.has("addition")) throw new MalformedRecipeFileException("Missing element \"addition\"");
        RecipeChoice addition = getRecipeChoice(recipeJson.get("addition"));

        //Copy Nbt
        boolean copyNBT = true;
        if(recipeJson.has("copyNbt")) copyNBT = recipeJson.getBoolean("copyNbt");

        return new SmithingRecipe(namespacedKey, result, base, addition, copyNBT);
    }
}
