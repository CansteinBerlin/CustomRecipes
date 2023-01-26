package de.canstein_berlin.customrecipes.api;

import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.api.recipes.serializer.BaseRecipeSerializer;
import de.canstein_berlin.customrecipes.api.recipes.serializer.RecipeSerializerFactory;
import de.canstein_berlin.customrecipes.api.recipes.serializer.type.*;
import de.canstein_berlin.customrecipes.api.requirements.BaseRequirement;
import de.canstein_berlin.customrecipes.api.requirements.type.GamemodeRequirement;
import de.canstein_berlin.customrecipes.api.requirements.type.PermissionRequirement;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public class CustomRecipesAPI {

    private static CustomRecipesAPI instance;

    private final HashMap<NamespacedKey, CustomRecipe> recipesWithRequirements;

    private CustomRecipesAPI() {
        instance = this;

        recipesWithRequirements = new HashMap<>();

        //Register serializer
        registerParser(new CraftingShapedRecipeSerializer());
        registerParser(new CraftingShapelessRecipeSerializer());
        registerParser(new SmeltingRecipeSerializer());
        registerParser(new BlastingRecipeSerializer());
        registerParser(new SmokingRecipeSerializer());
        registerParser(new CampfireCookingRecipeSerializer());
        registerParser(new StonecuttingRecipeSerializer());
        registerParser(new SmithingRecipeSerializer());

        //Register Requirements
        registerNewRequirement(new PermissionRequirement());
        registerNewRequirement(new GamemodeRequirement());
    }

    public static CustomRecipesAPI getInstance() {
        if (instance == null) return new CustomRecipesAPI();
        return instance;
    }

    public static NamespacedKey getNamespacedKeyFromRecipe(Recipe recipe) {
        if (recipe instanceof ShapedRecipe) return ((ShapedRecipe) recipe).getKey();
        if (recipe instanceof ShapelessRecipe) return ((ShapelessRecipe) recipe).getKey();
        if (recipe instanceof CookingRecipe) return ((CookingRecipe<?>) recipe).getKey();
        if (recipe instanceof SmithingRecipe) return ((SmithingRecipe) recipe).getKey();
        if (recipe instanceof StonecuttingRecipe) return ((StonecuttingRecipe) recipe).getKey();
        return null;
    }

    public CustomRecipe createAndRegister(JavaPlugin plugin, String file) {
        return createAndRegister(plugin, new File(plugin.getDataFolder(), file));
    }

    public CustomRecipe createAndRegister(JavaPlugin plugin, File file) {
        CustomRecipe recipe = CustomRecipe.fromResource(plugin, file);
        if (recipe == null) return null;
        if (!registerRecipes(recipe)) return null;
        
        return recipe;
    }

    public boolean registerRecipes(CustomRecipe recipe) {
        unregisterRecipe(recipe);

        boolean value = Bukkit.addRecipe(recipe.getRecipe());
        if (recipe.hasRequirements()) {
            recipesWithRequirements.put(recipe.getNamespacedKey(), recipe);
        }
        return value;
    }

    public boolean registerRecipes(Recipe recipe) {
        unregisterRecipe(recipe);

        return Bukkit.addRecipe(recipe);
    }

    public void unregisterRecipe(CustomRecipe recipe) {
        Bukkit.removeRecipe(recipe.getNamespacedKey());
        recipesWithRequirements.remove(recipe.getNamespacedKey());
    }

    public void unregisterRecipe(Recipe recipe) {
        Bukkit.removeRecipe(getNamespacedKeyFromRecipe(recipe));
    }

    public void registerParser(BaseRecipeSerializer recipeParser) {
        RecipeSerializerFactory.getInstance().addParser(recipeParser);
    }

    public void registerNewRequirement(BaseRequirement requirement) {
        RecipeSerializerFactory.getInstance().addRequirement(requirement);
    }

    public boolean canCraftRecipe(Recipe recipe, CraftItemEvent event) {
        NamespacedKey key = getNamespacedKeyFromRecipe(recipe);
        if (recipesWithRequirements.containsKey(key)) {
            return recipesWithRequirements.get(key).canCraft(event);
        }
        return true;
    }
}
