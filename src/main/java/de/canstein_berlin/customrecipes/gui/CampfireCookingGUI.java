package de.canstein_berlin.customrecipes.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.canstein_berlin.customrecipes.CustomRecipes;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CampfireCookingGUI extends ChestGui {

    private final HumanEntity viewer;

    public CampfireCookingGUI(CustomRecipe r, ListAllRecipesGUI parent, HumanEntity entity) {
        super(3, r.getName(), CustomRecipes.getInstance());
        this.viewer = entity;

        CampfireRecipe recipe = ((CampfireRecipe) r.getRecipe());

        //Background
        OutlinePane background = new OutlinePane(0, 0, 9, 3);
        background.addItem(new GuiItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").build()));
        background.setRepeat(true);
        background.setPriority(Pane.Priority.LOWEST);

        addPane(background);

        //Gui
        StaticPane pane = new StaticPane(0, 1, 9, 1);
        pane.addItem(new GuiItem(recipe.getInputChoice().getItemStack()), 1, 0);
        pane.addItem(new GuiItem(new ItemStack(Material.CAMPFIRE)), 4, 0);
        pane.addItem(new GuiItem(recipe.getResult()), 7, 0);
        addPane(pane);

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
