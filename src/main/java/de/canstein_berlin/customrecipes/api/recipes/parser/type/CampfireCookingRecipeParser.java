package de.canstein_berlin.customrecipes.api.recipes.parser.type;

import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

public class CampfireCookingRecipeParser extends SmeltingRecipeParser {

    public CampfireCookingRecipeParser() {
        super("minecraft:campfire_cooking");
    }

    @Override
    public CustomRecipe parse(JSONObject jsonObject, JavaPlugin plugin, String filename) throws MalformedRecipeFileException, InvalidRecipeValueException {
        CustomRecipe recipe = super.parse(jsonObject, plugin, filename);

        FurnaceRecipe r = ((FurnaceRecipe) recipe.getRecipe());
        CampfireRecipe blastingRecipe = new CampfireRecipe(r.getKey(), r.getResult(), r.getInputChoice(), r.getExperience(), r.getCookingTime());
        blastingRecipe.setGroup(r.getGroup());

        recipe.setRecipe(blastingRecipe);

        return recipe;
    }
}
