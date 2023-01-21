package de.canstein_berlin.customrecipes.api.recipes.parser.type;

import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.api.recipes.parser.BaseRecipeParser;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

public class SmeltingRecipeParser extends BaseRecipeParser {

    public SmeltingRecipeParser() {
        super("minecraft:smelting");
    }

    public SmeltingRecipeParser(String id) {
        super(id);
    }

    @Override
    public CustomRecipe parse(JSONObject jsonObject, JavaPlugin plugin, String filename) throws MalformedRecipeFileException, InvalidRecipeValueException {
        String nameKey = jsonObject.optString("name", filename.split("\\.")[0]);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, nameKey);

        //Result Itemstack
        if (!jsonObject.has("result")) throw new MalformedRecipeFileException("Missing element \"result\"");
        ItemStack result = getItemStack(jsonObject.get("result"));

        if (!jsonObject.has("cookingtime")) throw new MalformedRecipeFileException("Missing element \"cookingtime\"");
        int cookingTime = jsonObject.getInt("cookingtime");

        if (!jsonObject.has("experience")) throw new MalformedRecipeFileException("Missing element \"experience\"");
        float experience = jsonObject.getFloat("experience");

        if (!jsonObject.has("ingredient")) throw new MalformedRecipeFileException("Missing element \"ingredient\"");
        RecipeChoice source = getRecipeChoice(jsonObject.get("ingredient"));

        FurnaceRecipe recipe = new FurnaceRecipe(namespacedKey, result, source, experience, cookingTime);

        if (jsonObject.has("group")) recipe.setGroup(jsonObject.getString("group"));

        return new CustomRecipe(namespacedKey, recipe);
    }
}
