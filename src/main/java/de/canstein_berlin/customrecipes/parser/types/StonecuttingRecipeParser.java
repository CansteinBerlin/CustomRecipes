package de.canstein_berlin.customrecipes.parser.types;

import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.parser.RecipeParser;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.File;

public class StonecuttingRecipeParser extends RecipeParser {

    public StonecuttingRecipeParser(JavaPlugin plugin, File file, JSONObject jsonObject) {
        super(plugin, file, jsonObject);
    }

    @Override
    public Recipe parseRecipe() throws MalformedRecipeFileException, InvalidRecipeValueException {
        String nameKey = recipeJson.optString("name", file.getName().split("\\.")[0]);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, nameKey);

        //Result Itemstack
        if(!recipeJson.has("result")) throw new MalformedRecipeFileException("Missing element \"result\"");
        ItemStack result = getItemStack(recipeJson.get("result"));

        //Set count
        if(recipeJson.has("count")) result.setAmount(recipeJson.getInt("count"));

        if(!recipeJson.has("ingredient")) throw new MalformedRecipeFileException("Missing element \"ingredient\"");
        RecipeChoice c = getRecipeChoice(recipeJson.get("ingredient"));
        StonecuttingRecipe recipe = new StonecuttingRecipe(namespacedKey, result, c);

        if(recipeJson.has("group")) recipe.setGroup(recipeJson.getString("group"));
        return recipe;
    }
}
