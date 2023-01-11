package de.canstein_berlin.customrecipes.util;

import de.canstein_berlin.customrecipes.CustomRecipes;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.Recipe;

public class RecipeUtil {

    public static NamespacedKey getKeyFromRecipe(Recipe recipe){
        if(recipe instanceof Keyed) return ((Keyed) recipe).getKey();
        return NamespacedKey.fromString("MISSING!!", CustomRecipes.getInstance());
    }
}
