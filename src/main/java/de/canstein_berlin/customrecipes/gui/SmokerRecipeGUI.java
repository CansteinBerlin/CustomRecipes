package de.canstein_berlin.customrecipes.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.SmokerGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.canstein_berlin.customrecipes.CustomRecipes;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.scheduler.BukkitRunnable;

public class SmokerRecipeGUI extends SmokerGui {

    private final HumanEntity viewer;

    public SmokerRecipeGUI(CustomRecipe r, ListAllRecipesGUI parent, HumanEntity entity) {
        super(r.getName(), CustomRecipes.getInstance());
        this.viewer = entity;

        SmokingRecipe recipe = ((SmokingRecipe) r.getRecipe());

        //Output
        StaticPane output = new StaticPane(0, 0, 1, 1);
        output.addItem(new GuiItem(recipe.getResult()), 0, 0);
        getOutputComponent().addPane(output);

        //Input
        StaticPane input = new StaticPane(0, 0, 1, 1);
        input.addItem(new GuiItem(recipe.getInputChoice().getItemStack()), 0, 0);
        getIngredientComponent().addPane(input);

        //Fuel
        StaticPane fuel = new StaticPane(0, 0, 1, 1);
        fuel.addItem(new GuiItem(new ItemStack(Material.LAVA_BUCKET)), 0, 0);
        getFuelComponent().addPane(fuel);


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
