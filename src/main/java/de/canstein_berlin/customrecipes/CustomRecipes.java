package de.canstein_berlin.customrecipes;

import de.canstein_berlin.customrecipes.debug.ClassDebug;
import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.listener.CraftListener;
import de.canstein_berlin.customrecipes.parser.RecipeParser;
import de.canstein_berlin.customrecipes.parser.RecipeParserFactory;
import de.canstein_berlin.customrecipes.parser.RequirementParserFactory;
import de.canstein_berlin.customrecipes.requirements.BaseRequirement;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public final class CustomRecipes extends JavaPlugin {

    private static CustomRecipes instance;

    private HashMap<Recipe, ArrayList<BaseRequirement>> recipeRequirements;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(new CraftListener(), this);

        //String[] files = new String[]{"blasting.json", "campfire_cooking.json", "shaped.json", "shapeless.json", "smelting.json", "smoking.json", "stonecutting.json", "smithing.json", "test_shapeless.json"};

        String[] files = new String[]{"shapeless.json"};

        for(String file : files){
            if(getResource(file) != null) saveResource(file, true);

            try {
                RecipeParser parser = RecipeParserFactory.loadFromFile(this, file);
                System.out.println("###################### " + file + " ######################");
                Recipe recipe = parser.parseRecipe();
                System.out.println(new ClassDebug(recipe));
                Bukkit.removeRecipe(((ShapelessRecipe) recipe).getKey());
                Bukkit.addRecipe(recipe);

            } catch (MalformedRecipeFileException | InvalidRecipeValueException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerRecipe(String file) throws MalformedRecipeFileException, InvalidRecipeValueException {
        RecipeParser parser = RecipeParserFactory.loadFromFile(this, file);
        Recipe recipe = parser.parseRecipe();
        Bukkit.removeRecipe(((ShapelessRecipe) recipe).getKey());
        Bukkit.addRecipe(recipe);

        ArrayList<BaseRequirement> requirements = RequirementParserFactory.parseRequirements(parser.getRecipeJson());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static CustomRecipes getInstance() {
        return instance;
    }

    public HashMap<Recipe, ArrayList<BaseRequirement>> getRecipeRequirements() {
        return recipeRequirements;
    }
}
