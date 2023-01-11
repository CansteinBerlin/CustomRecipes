package de.canstein_berlin.customrecipes.gui;

import de.canstein_berlin.customrecipes.CustomRecipes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;

public class RecipeViewerGui {

    public static final String BASE_TITLE = "&6Recipes";

    private Player player;
    private Inventory inventory;

    public RecipeViewerGui(Player player, int page){
        ArrayList<Recipe> registeredRecipes = CustomRecipes.getInstance().getRegisteredRecipes();

        String title = registeredRecipes.size() > 9*5+5 ? BASE_TITLE + "(" + page + "/" + registeredRecipes.size() % (9*5+5) + ")" : BASE_TITLE;
        inventory = Bukkit.createInventory(null, 9*6, title);

    }
}
