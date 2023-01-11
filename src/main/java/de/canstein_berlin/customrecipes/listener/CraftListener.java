package de.canstein_berlin.customrecipes.listener;

import de.canstein_berlin.customrecipes.CustomRecipes;
import de.canstein_berlin.customrecipes.requirements.BaseRequirement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;

public class CraftListener implements Listener {

    @EventHandler
    public void onRecipeCraft(CraftItemEvent event){
        Recipe recipe = event.getRecipe();
        ArrayList<BaseRequirement> requirements = CustomRecipes.getInstance().getRequirements(recipe);
        for(BaseRequirement r : requirements){
            if(!r.check(event)){
                event.setCancelled(true);
                return;
            }
        }
    }
}
