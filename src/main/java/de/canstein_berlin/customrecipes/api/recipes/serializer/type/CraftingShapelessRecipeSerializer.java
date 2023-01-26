package de.canstein_berlin.customrecipes.api.recipes.serializer.type;

import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.api.recipes.serializer.BaseRecipeSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

public class CraftingShapelessRecipeSerializer extends BaseRecipeSerializer {

    public CraftingShapelessRecipeSerializer() {
        super("minecraft:crafting_shapeless", ShapelessRecipe.class);
    }

    @Override
    public CustomRecipe parse(JSONObject jsonObject, JavaPlugin plugin, String filename) throws MalformedRecipeFileException, InvalidRecipeValueException {
        //RecipeName / NamespacedKey
        String nameKey = jsonObject.optString("name", filename.split("\\.")[0]);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, nameKey);

        //Result Itemstack
        if (!jsonObject.has("result")) throw new MalformedRecipeFileException("Missing element \"result\"");
        ItemStack result = getItemStack(jsonObject.get("result"));

        //Recipe creation
        ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, result);

        //Ingredients
        if (!jsonObject.has("ingredients")) throw new MalformedRecipeFileException("Missing element \"ingredients\"");
        JSONArray jsonArray = jsonObject.getJSONArray("ingredients");
        for (int i = 0; i < jsonArray.length(); i++) {
            RecipeChoice choice = getRecipeChoice(jsonArray.get(i));
            recipe.addIngredient(choice);
        }

        //Group
        if (jsonObject.has("group")) recipe.setGroup(jsonObject.getString("group"));

        return new CustomRecipe(namespacedKey, recipe);
    }

    @Override
    public JSONObject serialize(Recipe r) {
        ShapelessRecipe recipe = ((ShapelessRecipe) r);

        JSONObject master = new JSONObject();
        //Type
        master.put("type", getId());

        //Result
        ItemStack result = recipe.getResult();
        JSONObject resultJson = serializeItemStack(result, false);
        master.put("result", resultJson);

        //Ingredients
        JSONArray ingredients = new JSONArray();
        for (RecipeChoice c : recipe.getChoiceList()) {
            ingredients.put(serializeRecipeChoice(c));
        }
        master.put("ingredients", ingredients);

        return master;
    }
}
