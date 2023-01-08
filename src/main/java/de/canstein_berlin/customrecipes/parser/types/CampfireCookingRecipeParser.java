package de.canstein_berlin.customrecipes.parser.types;

import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.parser.RecipeParser;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.File;

public class CampfireCookingRecipeParser extends SmeltingRecipeParser {

    public CampfireCookingRecipeParser(JavaPlugin plugin, File file, JSONObject jsonObject) {
        super(plugin, file, jsonObject);
    }

    @Override
    public Recipe parseRecipe() throws InvalidRecipeValueException, MalformedRecipeFileException {
        FurnaceRecipe recipe = (FurnaceRecipe) super.parseRecipe();

        CampfireRecipe recipe1 = new CampfireRecipe(recipe.getKey(), recipe.getResult(), recipe.getInputChoice(), recipe.getExperience(), recipe.getCookingTime());
        recipe1.setGroup(recipe.getGroup());
        return recipe1;
    }
}
