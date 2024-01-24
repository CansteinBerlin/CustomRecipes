package de.canstein_berlin.customrecipes.api.recipes.serializer.type;

import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.api.recipes.serializer.BaseRecipeSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

public class SmithingRecipeSerializer extends BaseRecipeSerializer {

    public SmithingRecipeSerializer() {
        super("minecraft:smithing_transform", SmithingTransformRecipe.class);
    }

    @Override
    public CustomRecipe parse(JSONObject jsonObject, JavaPlugin plugin, String filename) throws MalformedRecipeFileException, InvalidRecipeValueException {
        String nameKey = jsonObject.optString("name", filename.split("\\.")[0]);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, nameKey);

        //Result Itemstack
        if (!jsonObject.has("result")) throw new MalformedRecipeFileException("Missing element \"result\"");
        ItemStack result = getItemStack(jsonObject.get("result"));

        //Base Item
        if (!jsonObject.has("base")) throw new MalformedRecipeFileException("Missing element \"base\"");
        RecipeChoice base = getRecipeChoice(jsonObject.get("base"));

        //Addition Item
        if (!jsonObject.has("addition")) throw new MalformedRecipeFileException("Missing element \"addition\"");
        RecipeChoice addition = getRecipeChoice(jsonObject.get("addition"));

        //Template Item
        if (!jsonObject.has("template")) throw new MalformedRecipeFileException("Missing element \"template\"");
        RecipeChoice template = getRecipeChoice(jsonObject.get("template"));


        //Copy Nbt
        boolean copyNBT = true;
        if (jsonObject.has("copyNbt")) copyNBT = jsonObject.getBoolean("copyNbt");

        SmithingRecipe recipe = new SmithingTransformRecipe(namespacedKey, result, template, base, addition, copyNBT);

        return new CustomRecipe(namespacedKey, recipe);
    }

    @Override
    public JSONObject serialize(Recipe r) {
        SmithingRecipe recipe = ((SmithingRecipe) r);

        JSONObject master = new JSONObject();
        //Type
        master.put("type", getId());

        //Result
        ItemStack result = recipe.getResult();
        JSONObject resultJson = serializeItemStack(result, false);
        master.put("result", resultJson);

        //Addition
        master.put("addition", serializeRecipeChoice(recipe.getAddition()));

        //Base
        master.put("base", serializeRecipeChoice(recipe.getBase()));
        return master;
    }
}
