package de.canstein_berlin.customrecipes.api;

import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.api.recipes.parser.BaseRecipeParser;
import de.canstein_berlin.customrecipes.api.recipes.parser.RecipeParserFactory;
import de.canstein_berlin.customrecipes.api.recipes.parser.type.*;
import de.canstein_berlin.customrecipes.api.requirements.BaseRequirement;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomRecipesAPI {

    private static CustomRecipesAPI instance;

    private final HashMap<String, BaseRequirement> requirements;
    private final ArrayList<CustomRecipe> recipesWithRequirements;

    private CustomRecipesAPI() {
        instance = this;

        requirements = new HashMap<>();
        recipesWithRequirements = new ArrayList<>();

        //Register parser
        RecipeParserFactory.getInstance().addParser(new CraftingShapedRecipeParser());
        RecipeParserFactory.getInstance().addParser(new CraftingShapelessRecipeParser());
        RecipeParserFactory.getInstance().addParser(new SmeltingRecipeParser());
        RecipeParserFactory.getInstance().addParser(new BlastingRecipeParser());
        RecipeParserFactory.getInstance().addParser(new SmokingRecipeParser());
        RecipeParserFactory.getInstance().addParser(new CampfireCookingRecipeParser());
        RecipeParserFactory.getInstance().addParser(new StonecuttingRecipeParser());
        RecipeParserFactory.getInstance().addParser(new SmithingRecipeParser());

        //Register Requirements

    }

    public static CustomRecipesAPI getInstance() {
        if (instance == null) return new CustomRecipesAPI();
        return instance;
    }

    public void registerRecipes(CustomRecipe recipe) {
        Bukkit.removeRecipe(recipe.getNamespacedKey());
        Bukkit.addRecipe(recipe.getRecipe());
    }

    public void registerRecipes(Recipe recipe) {

    }

    public void unregisterRecipe(CustomRecipe recipe) {

    }

    public void unregisterRecipe(Recipe recipe) {

    }

    public void registerParser(BaseRecipeParser recipeParser) {
        RecipeParserFactory.getInstance().addParser(recipeParser);
    }

    public void registerNewRequirement(BaseRequirement requirement) {
        requirements.put(requirement.getId(), requirement);
    }

    public HashMap<String, BaseRequirement> getRequirements() {
        return requirements;
    }
}
