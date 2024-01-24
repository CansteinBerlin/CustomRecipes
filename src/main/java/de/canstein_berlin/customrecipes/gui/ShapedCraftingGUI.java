package de.canstein_berlin.customrecipes.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.CraftingTableGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.canstein_berlin.customrecipes.CustomRecipes;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;

public class ShapedCraftingGUI extends CraftingTableGui {

    private final HumanEntity viewer;

    public ShapedCraftingGUI(CustomRecipe r, ListAllRecipesGUI parent, HumanEntity entity) {
        super(r.getName(), CustomRecipes.getInstance());
        this.viewer = entity;

        ShapedRecipe recipe = ((ShapedRecipe) r.getRecipe());

        //Output
        StaticPane output = new StaticPane(0, 0, 1, 1);
        output.addItem(new GuiItem(recipe.getResult()), 0, 0);
        getOutputComponent().addPane(output);

        //Input
        StaticPane input = new StaticPane(0, 0, 3, 3);
        for (int y = 0; y < recipe.getShape().length; y++) {
            for (int x = 0; x < recipe.getShape()[y].length(); x++) {
                char c = recipe.getShape()[y].charAt(x);
                if (c == ' ') continue;
                input.addItem(new GuiItem(recipe.getChoiceMap().get(c).getItemStack()), x, y);
            }
        }
        getInputComponent().addPane(input);


        //Global
        new BukkitRunnable() {

            @Override
            public void run() {
                setOnClose(e ->
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                parent.show(viewer);
                            }
                        }.runTaskLater(CustomRecipes.getInstance(), 1));

                setOnGlobalClick(e -> {
                    e.setCancelled(true);
                    ((Player) entity).updateInventory();
                });

            }
        }.runTaskLater(CustomRecipes.getInstance(), 1);

    }

}
