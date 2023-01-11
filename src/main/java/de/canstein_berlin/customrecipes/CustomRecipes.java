package de.canstein_berlin.customrecipes;

import de.canstein_berlin.customrecipes.config.DefaultConfig;
import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.listener.CraftListener;
import de.canstein_berlin.customrecipes.parser.RecipeParser;
import de.canstein_berlin.customrecipes.parser.RecipeParserFactory;
import de.canstein_berlin.customrecipes.parser.RequirementParser;
import de.canstein_berlin.customrecipes.requirements.BaseRequirement;
import de.canstein_berlin.customrecipes.util.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public final class CustomRecipes extends JavaPlugin {

    private static CustomRecipes instance;
    public static String PREFIX = "";

    private HashMap<NamespacedKey, ArrayList<BaseRequirement>> recipeRequirements;
    private ArrayList<Recipe> registeredRecipes;
    private DefaultConfig config;

    @Override
    public void onEnable() {
        instance = this;
        recipeRequirements = new HashMap<>();
        registeredRecipes = new ArrayList<>();

        config = new DefaultConfig(this, "config.yml");

        Bukkit.getPluginManager().registerEvents(new CraftListener(), this);

        //String[] files = new String[]{"blasting.json", "campfire_cooking.json", "shaped.json", "shapeless.json", "smelting.json", "smoking.json", "stonecutting.json", "smithing.json", "test_shapeless.json"};

        String[] files = new String[]{"shapeless.json"};

        for(String file : files){
            if(getResource(file) != null) saveResource(file, true);

            try {
                registerRecipe(this, file);

            } catch (MalformedRecipeFileException | InvalidRecipeValueException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerRecipe(JavaPlugin plugin, String file) throws MalformedRecipeFileException, InvalidRecipeValueException {
        RecipeParser parser = RecipeParserFactory.loadFromFile(plugin, file);
        Recipe recipe = parser.parseRecipe();
        Bukkit.removeRecipe(RecipeUtil.getKeyFromRecipe(recipe));
        Bukkit.addRecipe(recipe);
        registeredRecipes.add(recipe);

        ArrayList<BaseRequirement> requirements = RequirementParser.parseRequirements(parser.getRecipeJson());
        System.out.println(requirements);
        if(requirements.size() == 0) return;

        recipeRequirements.put(RecipeUtil.getKeyFromRecipe(recipe), requirements);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static String getLang(String key, String... args) {
        String lang = CustomRecipes.getInstance().getDefaultConfig().getConfig().getString(key, "&cUnknown or empty language key please check the config &6" + key);
        for (int i = 0; i + 1 < args.length; i += 2) {
            lang = lang.replace("%" + args[i] + "%", args[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', lang).replace("\\n", "\n");
    }

    public static CustomRecipes getInstance() {
        return instance;
    }

    public HashMap<NamespacedKey, ArrayList<BaseRequirement>> getRecipeRequirements() {
        return recipeRequirements;
    }

    public ArrayList<BaseRequirement> getRequirements(Recipe recipe) {
        return recipeRequirements.getOrDefault(RecipeUtil.getKeyFromRecipe(recipe), new ArrayList<>());
    }

    public ArrayList<Recipe> getRegisteredRecipes() {
        return registeredRecipes;
    }

    public DefaultConfig getDefaultConfig() {
        return config;
    }
}
