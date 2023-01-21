package de.canstein_berlin.customrecipes.api.recipes.parser.type;

import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.api.recipes.parser.BaseRecipeParser;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CraftingShapedRecipeParser extends BaseRecipeParser {

    public CraftingShapedRecipeParser() {
        super("minecraft:crafting_shaped");
    }

    @Override
    public CustomRecipe parse(JSONObject jsonObject, JavaPlugin plugin, String filename) throws MalformedRecipeFileException, InvalidRecipeValueException {
        String nameKey = jsonObject.optString("name", filename.split("\\.")[0]);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, nameKey);

        //Result Itemstack
        if (!jsonObject.has("result")) throw new MalformedRecipeFileException("Missing element \"result\"");
        ItemStack result = getItemStack(jsonObject.get("result"));

        //Recipe creation
        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result);

        //Shape
        if (!jsonObject.has("pattern")) throw new MalformedRecipeFileException("Missing element \"pattern\"");
        JSONArray patternArray = jsonObject.getJSONArray("pattern");
        ArrayList<String> pattern = new ArrayList<>();
        for (int i = 0; i < patternArray.length(); i++) {
            pattern.add(patternArray.getString(i));
        }
        recipe.shape(pattern.toArray(new String[patternArray.length()]));

        //Items
        if (!jsonObject.has("key")) throw new MalformedRecipeFileException("Missing element \"key\"");
        JSONObject key = jsonObject.getJSONObject("key");
        for (String k : key.keySet()) {
            char c = k.charAt(0);
            RecipeChoice choice = getRecipeChoice(key.get(k));
            recipe.setIngredient(c, choice);
        }

        //Group
        if (jsonObject.has("group")) recipe.setGroup(jsonObject.getString("group"));

        //Create Custom Recipe
        CustomRecipe customRecipe = new CustomRecipe(namespacedKey, recipe);

        //Parse and add Requirements
        if (jsonObject.has("requirements")) {
            customRecipe.setRequirements(parseRequirements(jsonObject.optJSONArray("requirements")));
        }
        return customRecipe;
    }
}
