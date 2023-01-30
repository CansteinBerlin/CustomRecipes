package de.canstein_berlin.customrecipes.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.StonecutterGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.canstein_berlin.customrecipes.CustomRecipes;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.scheduler.BukkitRunnable;

public class StonecuttingGUI extends StonecutterGui {

    private final HumanEntity viewer;

    public StonecuttingGUI(CustomRecipe r, ListAllRecipesGUI parent, HumanEntity entity) {
        super(r.getName(), CustomRecipes.getInstance());
        this.viewer = entity;

        StonecuttingRecipe recipe = ((StonecuttingRecipe) r.getRecipe());

        //Output
        StaticPane output = new StaticPane(0, 0, 1, 1);
        output.addItem(new GuiItem(recipe.getResult()), 0, 0);
        getResultComponent().addPane(output);

        //Input
        StaticPane input = new StaticPane(0, 0, 1, 1);
        input.addItem(new GuiItem(recipe.getInputChoice().getItemStack()), 0, 0);
        getInputComponent().addPane(input);


        //Global
        new BukkitRunnable() {

            @Override
            public void run() {
                setOnClose(e -> parent.show(viewer));
                setOnGlobalClick(e -> {
                    e.setCancelled(true);
                    ((Player) entity).updateInventory();
                });

            }
        }.runTaskLater(CustomRecipes.getInstance(), 1);

    }
}
