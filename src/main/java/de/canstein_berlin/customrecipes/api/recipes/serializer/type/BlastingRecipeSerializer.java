package de.canstein_berlin.customrecipes.api.recipes.serializer.type;

import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

public class BlastingRecipeSerializer extends SmeltingRecipeSerializer {

    public BlastingRecipeSerializer() {
        super("minecraft:blasting", BlastingRecipe.class);
    }

    @Override
    public CustomRecipe parse(JSONObject jsonObject, JavaPlugin plugin, String filename) throws MalformedRecipeFileException, InvalidRecipeValueException {
        CustomRecipe recipe = super.parse(jsonObject, plugin, filename);

        FurnaceRecipe r = ((FurnaceRecipe) recipe.getRecipe());
        BlastingRecipe blastingRecipe = new BlastingRecipe(r.getKey(), r.getResult(), r.getInputChoice(), r.getExperience(), r.getCookingTime());
        blastingRecipe.setGroup(r.getGroup());

        recipe.setRecipe(blastingRecipe);

        return recipe;
    }

    @Override
    public JSONObject serialize(Recipe r) {
        JSONObject jsonObject = super.serialize(r);
        jsonObject.put("type", getId());

        return jsonObject;
    }
}
