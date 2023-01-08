package de.canstein_berlin.customrecipes.parser.types;

import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.parser.RecipeParser;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.File;

public class SmeltingRecipeParser extends RecipeParser {

    public SmeltingRecipeParser(JavaPlugin plugin, File file, JSONObject jsonObject) {
        super(plugin, file, jsonObject);
    }

    @Override
    public Recipe parseRecipe() throws MalformedRecipeFileException, InvalidRecipeValueException {
        String nameKey = recipeJson.optString("name", file.getName().split("\\.")[0]);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, nameKey);

        //Result Itemstack
        if(!recipeJson.has("result")) throw new MalformedRecipeFileException("Missing element \"result\"");
        ItemStack result = getItemStack(recipeJson.get("result"));

        if(!recipeJson.has("cookingtime")) throw new MalformedRecipeFileException("Missing element \"cookingtime\"");
        int cookingTime = recipeJson.getInt("cookingtime");

        if(!recipeJson.has("experience")) throw new MalformedRecipeFileException("Missing element \"experience\"");
        float experience = recipeJson.getFloat("experience");

        if(!recipeJson.has("ingredient")) throw new MalformedRecipeFileException("Missing element \"ingredient\"");
        RecipeChoice source = getRecipeChoice(recipeJson.get("ingredient"));

        FurnaceRecipe recipe = new FurnaceRecipe(namespacedKey, result, source, experience, cookingTime);

        if(recipeJson.has("group")) recipe.setGroup(recipeJson.getString("group"));

        return recipe;
    }
}
