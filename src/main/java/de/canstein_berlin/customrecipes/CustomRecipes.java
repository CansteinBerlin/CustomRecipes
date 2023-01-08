package de.canstein_berlin.customrecipes;

import de.canstein_berlin.customrecipes.debug.ClassDebug;
import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.parser.RecipeParser;
import de.canstein_berlin.customrecipes.parser.RecipeParserFactory;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomRecipes extends JavaPlugin {

    private static CustomRecipes instance;

    @Override
    public void onEnable() {
        instance = this;

        String[] files = new String[]{"blasting.json", "campfire_cooking.json", "shaped.json", "shapeless.json", "smelting.json", "smoking.json", "stonecutting.json", "smithing.json", "test_shapeless.json"};

        //String[] files = new String[]{"stonecutting.json"};

        for(String file : files){
            if(getResource(file) != null) saveResource(file, true);

            try {
                RecipeParser parser = RecipeParserFactory.loadFromFile(this, file);
                System.out.println("###################### " + file + " ######################");
                System.out.println(new ClassDebug(parser.parseRecipe()));
            } catch (MalformedRecipeFileException | InvalidRecipeValueException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static CustomRecipes getInstance() {
        return instance;
    }
}
