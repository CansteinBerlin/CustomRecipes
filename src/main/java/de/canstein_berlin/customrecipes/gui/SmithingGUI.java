package de.canstein_berlin.customrecipes.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.SmithingTableGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.canstein_berlin.customrecipes.CustomRecipes;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.scheduler.BukkitRunnable;

public class SmithingGUI extends SmithingTableGui {

    private final HumanEntity viewer;

    public SmithingGUI(CustomRecipe r, ListAllRecipesGUI parent, HumanEntity entity) {
        super(r.getName(), CustomRecipes.getInstance());
        this.viewer = entity;

        SmithingRecipe recipe = ((SmithingRecipe) r.getRecipe());

        //Output
        StaticPane output = new StaticPane(0, 0, 1, 1);
        output.addItem(new GuiItem(recipe.getResult()), 0, 0);
        output.setOnClick(event -> event.setCancelled(true));
        getResultComponent().addPane(output);

        //Base
        StaticPane base = new StaticPane(0, 0, 1, 1);
        base.addItem(new GuiItem(recipe.getBase().getItemStack()), 0, 0);
        base.setOnClick(event -> event.setCancelled(true));
        getFirstItemComponent().addPane(base);

        //Addition
        StaticPane addition = new StaticPane(0, 0, 1, 1);
        addition.addItem(new GuiItem(recipe.getAddition().getItemStack()), 0, 0);
        addition.setOnClick(event -> event.setCancelled(true));
        getSecondItemComponent().addPane(addition);


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
