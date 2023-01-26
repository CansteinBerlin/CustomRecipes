package de.canstein_berlin.customrecipes.api.recipes.serializer.type;

import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.api.recipes.serializer.BaseRecipeSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

public class SmeltingRecipeSerializer extends BaseRecipeSerializer {

    public SmeltingRecipeSerializer() {
        super("minecraft:smelting", FurnaceRecipe.class);
    }

    public SmeltingRecipeSerializer(String id, Class<?> cls) {
        super(id, cls);
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

    @Override
    public JSONObject serialize(Recipe r) {
        CookingRecipe recipe = ((CookingRecipe) r);

        JSONObject master = new JSONObject();
        //Type
        master.put("type", getId());

        //Result
        ItemStack result = recipe.getResult();
        JSONObject resultJson = serializeItemStack(result, false);
        master.put("result", resultJson);

        //Cooking Time
        master.put("cookingtime", recipe.getCookingTime());

        //Experience
        master.put("experience", recipe.getExperience());

        //Ingredient
        master.put("ingredient", serializeRecipeChoice(recipe.getInputChoice()));

        return master;
    }
}
