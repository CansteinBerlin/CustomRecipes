package de.canstein_berlin.customrecipes.api.recipes.parser.type;

import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.api.recipes.parser.BaseRecipeParser;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

public class StonecuttingRecipeParser extends BaseRecipeParser {

    public StonecuttingRecipeParser() {
        super("minecraft:stonecutting");
    }

    @Override
    public CustomRecipe parse(JSONObject jsonObject, JavaPlugin plugin, String filename) throws MalformedRecipeFileException, InvalidRecipeValueException {
        String nameKey = jsonObject.optString("name", filename.split("\\.")[0]);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, nameKey);

        //Result Itemstack
        if (!jsonObject.has("result")) throw new MalformedRecipeFileException("Missing element \"result\"");
        ItemStack result = getItemStack(jsonObject.get("result"));

        //Set count
        if (jsonObject.has("count")) result.setAmount(jsonObject.getInt("count"));

        //Ingredients
        if (!jsonObject.has("ingredient")) throw new MalformedRecipeFileException("Missing element \"ingredient\"");
        RecipeChoice c = getRecipeChoice(jsonObject.get("ingredient"));
        StonecuttingRecipe recipe = new StonecuttingRecipe(namespacedKey, result, c);

        //Group
        if (jsonObject.has("group")) recipe.setGroup(jsonObject.getString("group"));

        return new CustomRecipe(namespacedKey, recipe);
    }
}
