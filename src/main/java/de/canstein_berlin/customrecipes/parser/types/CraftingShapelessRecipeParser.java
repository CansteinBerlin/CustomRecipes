package de.canstein_berlin.customrecipes.parser.types;

import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.parser.RecipeParser;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class CraftingShapelessRecipeParser extends RecipeParser {

    public CraftingShapelessRecipeParser(JavaPlugin plugin, File file, JSONObject jsonObject) {
        super(plugin, file, jsonObject);
    }

    @Override
    public Recipe parseRecipe() throws MalformedRecipeFileException, InvalidRecipeValueException {
        //RecipeName / NamespacedKey
        String nameKey = recipeJson.optString("name", file.getName().split("\\.")[0]);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, nameKey);

        //Result Itemstack
        if(!recipeJson.has("result")) throw new MalformedRecipeFileException("Missing element \"result\"");
        ItemStack result = getItemStack(recipeJson.get("result"));

        //Recipe creation
        ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, result);

        //Ingredients
        if(!recipeJson.has("ingredients")) throw new MalformedRecipeFileException("Missing element \"ingredients\"");
        JSONArray jsonArray = recipeJson.getJSONArray("ingredients");
        for(int i = 0; i < jsonArray.length(); i++){
            RecipeChoice choice = getRecipeChoice(jsonArray.get(i));
            recipe.addIngredient(choice);
        }

        if(recipeJson.has("group")) recipe.setGroup(recipeJson.getString("group"));

        return recipe;
    }
}
