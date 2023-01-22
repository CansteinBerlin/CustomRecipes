package de.canstein_berlin.customrecipes.listeners;

import de.canstein_berlin.customrecipes.api.CustomRecipesAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class ItemCraftListener implements Listener {

    @EventHandler
    public void onItemCraft(CraftItemEvent event) {
        if (!CustomRecipesAPI.getInstance().canCraftRecipe(event.getRecipe(), event)) event.setCancelled(true);
    }
    

}
