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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CustomRecipesAPI {

    private static CustomRecipesAPI instance;

    private final HashMap<NamespacedKey, CustomRecipe> recipesWithRequirements;
    private final HashMap<String, ArrayList<CustomRecipe>> definedRecipes;

    private CustomRecipesAPI() {
        instance = this;

        recipesWithRequirements = new HashMap<>();
        definedRecipes = new HashMap<>();

        //Register serializer
        registerSerializer(new CraftingShapedRecipeSerializer());
        registerSerializer(new CraftingShapelessRecipeSerializer());
        registerSerializer(new SmeltingRecipeSerializer());
        registerSerializer(new BlastingRecipeSerializer());
        registerSerializer(new SmokingRecipeSerializer());
        registerSerializer(new CampfireCookingRecipeSerializer());
        registerSerializer(new StonecuttingRecipeSerializer());
        registerSerializer(new SmithingRecipeSerializer());

        //Register Requirements
        registerNewRequirement(new PermissionRequirement());
        registerNewRequirement(new GamemodeRequirement());
    }

    /**
     * This is how to access the API!
     **/
    public static CustomRecipesAPI getInstance() {
        if (instance == null) return new CustomRecipesAPI();
        return instance;
    }

    /**
     * Get the NamespaceKey from an Recipe. Does not work for {@link MerchantRecipe}
     *
     * @param recipe Recipe to get the key from
     * @return NamespacedKey of the Recipe of null if unknown recipe type
     */
    public static NamespacedKey getNamespacedKeyFromRecipe(Recipe recipe) {
        if (recipe instanceof ShapedRecipe) return ((ShapedRecipe) recipe).getKey();
        if (recipe instanceof ShapelessRecipe) return ((ShapelessRecipe) recipe).getKey();
        if (recipe instanceof CookingRecipe) return ((CookingRecipe<?>) recipe).getKey();
        if (recipe instanceof SmithingRecipe) return ((SmithingRecipe) recipe).getKey();
        if (recipe instanceof StonecuttingRecipe) return ((StonecuttingRecipe) recipe).getKey();
        return null;
    }

    /**
     * Create and register a recipe.
     *
     * @param plugin plugin that wants to register the recipe
     * @param file   file path to the recipe file based on the datafolder of the plugin
     * @return The created recipe of null if an error occurred
     * @see CustomRecipe#fromResource(JavaPlugin, File)
     * @see CustomRecipesAPI#registerRecipe(CustomRecipe)
     */
    public CustomRecipe createAndRegister(JavaPlugin plugin, String file) {
        return createAndRegister(plugin, new File(plugin.getDataFolder(), file));
    }

    /**
     * Create and register a recipe.
     *
     * @param plugin plugin that wants to register the recipe
     * @param file   File object that points to the location of the file
     * @return The created recipe of null if an error occurred
     * @see CustomRecipe#fromResource(JavaPlugin, File)
     * @see CustomRecipesAPI#registerRecipe(CustomRecipe)
     */
    public CustomRecipe createAndRegister(JavaPlugin plugin, File file) {
        CustomRecipe recipe = CustomRecipe.fromResource(plugin, file);
        if (recipe == null) return null;
        if (!registerRecipe(recipe)) return null;

        return recipe;
    }

    /**
     * Register a Custom Recipe from this plugin
     *
     * @param recipe Recipe to register
     * @return true if registration was successful, false otherwise
     */
    public boolean registerRecipe(CustomRecipe recipe) {
        unregisterRecipe(recipe);

        boolean value = Bukkit.addRecipe(recipe.getRecipe());
        String key = recipe.getNamespacedKey().getNamespace();

        if (!definedRecipes.containsKey(key)) definedRecipes.put(key, new ArrayList<>());
        definedRecipes.get(key).add(recipe);

        if (recipe.hasRequirements()) {
            recipesWithRequirements.put(recipe.getNamespacedKey(), recipe);
        }
        return value;
    }

    /**
     * Register a Bukkit recipe
     *
     * @param recipe Recipe to register
     * @return true if registration was successful, false otherwise
     */
    public boolean registerRecipe(Recipe recipe) {
        unregisterRecipe(recipe);

        return Bukkit.addRecipe(recipe);
    }

    /**
     * Unregister CustomRecipe from this plugin
     *
     * @param recipe Recipe to unregister
     */
    public void unregisterRecipe(CustomRecipe recipe) {
        Bukkit.removeRecipe(recipe.getNamespacedKey());
        recipesWithRequirements.remove(recipe.getNamespacedKey());
    }

    /**
     * Unregister Bukkit recipe
     *
     * @param recipe Recipe to unregister
     */
    public void unregisterRecipe(Recipe recipe) {
        Bukkit.removeRecipe(getNamespacedKeyFromRecipe(recipe));
    }

    /**
     * Register a parser that can parse custom recipe files
     *
     * @param recipeParser Parser to register
     * @see BaseRecipeSerializer
     */
    public void registerSerializer(BaseRecipeSerializer recipeParser) {
        RecipeSerializerFactory.getInstance().addParser(recipeParser);
    }

    /**
     * Register a new Requirement for the parser to use
     *
     * @param requirement Requirement to register
     * @see BaseRequirement
     */
    public void registerNewRequirement(BaseRequirement requirement) {
        RecipeSerializerFactory.getInstance().addRequirement(requirement);
    }

    /**
     * Somewhat internal method to check if a recipe registered as a custom recipe can be crafted.
     *
     * @param recipe Bukkit Recipe that should be checked
     * @param event  event to gather information about the player and the environment
     * @return true if the recipe can be crafted, false if any requirement fails
     */
    public boolean canCraftRecipe(Recipe recipe, CraftItemEvent event) {
        NamespacedKey key = getNamespacedKeyFromRecipe(recipe);
        if (recipesWithRequirements.containsKey(key)) {
            return recipesWithRequirements.get(key).canCraft(event);
        }
        return true;
    }

    /**
     * Returns all recipes defined in a specific namespace, input "" to get all defined recipes except the minecraft recipes
     *
     * @param key Valid namespace of ""
     * @return recipes defined under the namespace or all
     */
    public ArrayList<CustomRecipe> getDefinedRecipes(String key) {
        if (key.isEmpty()) {
            ArrayList<CustomRecipe> recipes = new ArrayList<>();
            for (ArrayList<CustomRecipe> rec : definedRecipes.values()) {
                recipes.addAll(rec);
            }
            return recipes;
        }

        return definedRecipes.getOrDefault(key, new ArrayList<>());
    }

    public Set<String> getRegisteredNamespaces() {
        return definedRecipes.keySet();
    }
}
