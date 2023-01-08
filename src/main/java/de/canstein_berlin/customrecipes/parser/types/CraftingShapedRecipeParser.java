package de.canstein_berlin.customrecipes.parser.types;

import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.parser.RecipeParser;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class CraftingShapedRecipeParser extends RecipeParser {

    public CraftingShapedRecipeParser(JavaPlugin plugin, File file, JSONObject jsonObject) {
        super(plugin, file, jsonObject);
    }

    @Override
    public Recipe parseRecipe() throws MalformedRecipeFileException, InvalidRecipeValueException {
        String nameKey = recipeJson.optString("name", file.getName().split("\\.")[0]);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, nameKey);

        //Result Itemstack
        if(!recipeJson.has("result")) throw new MalformedRecipeFileException("Missing element \"result\"");
        ItemStack result = getItemStack(recipeJson.get("result"));

        //Recipe creation
        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result);

        //Shape
        if(!recipeJson.has("pattern")) throw new MalformedRecipeFileException("Missing element \"pattern\"");
        JSONArray patternArray = recipeJson.getJSONArray("pattern");
        ArrayList<String> pattern = new ArrayList<>();
        for(int i = 0; i < patternArray.length(); i++){
            pattern.add(patternArray.getString(i));
        }
        recipe.shape(pattern.toArray(new String[patternArray.length()]));

        //Items
        if(!recipeJson.has("key")) throw new MalformedRecipeFileException("Missing element \"key\"");
        JSONObject key = recipeJson.getJSONObject("key");
        for(String k : key.keySet()){
            char c = k.charAt(0);
            RecipeChoice choice = getRecipeChoice(key.get(k));
            recipe.setIngredient(c, choice);
        }

        if(recipeJson.has("group")) recipe.setGroup(recipeJson.getString("group"));

        return recipe;
    }
}
