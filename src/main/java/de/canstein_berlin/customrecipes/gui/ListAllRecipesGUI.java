package de.canstein_berlin.customrecipes.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.canstein_berlin.customrecipes.CustomRecipes;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.builder.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListAllRecipesGUI extends ChestGui {

    private final PaginatedPane pages;
    private final OutlinePane background;
    private final StaticPane navigation;
    private final ArrayList<CustomRecipe> recipes;

    public ListAllRecipesGUI(ArrayList<CustomRecipe> recipes) {
        super(6, "Recipes", CustomRecipes.getInstance());

        this.recipes = recipes;

        ////// Set up GUI //////
        //Display Items
        pages = new PaginatedPane(0, 0, 9, 5);
        pages.populateWithGuiItems(recipes.stream().map(this::getItemFromRecipe).collect(Collectors.toList()));
        addPane(pages);

        //Background
        background = new OutlinePane(0, 5, 9, 1);
        background.addItem(new GuiItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .setDisplayName(" ")
                .build()));
        background.setRepeat(true);

        background.setPriority(Pane.Priority.LOWEST);
        addPane(background);

        //Navigation
        navigation = new StaticPane(0, 5, 9, 1);
        navigation.addItem(new GuiItem(new ItemBuilder(Material.RED_WOOL).setDisplayName("§cPrevious Page").build(), event ->
        {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
            }

            update();
        }), 0, 0);
        navigation.addItem(new GuiItem(new ItemBuilder(Material.LIME_WOOL).setDisplayName("§aNextPage Page").build(), event ->
        {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
            }
            update();
        }), 8, 0);

        addPane(navigation);

        //Global
        setOnGlobalClick(event -> event.setCancelled(true));

    }

    private GuiItem getItemFromRecipe(CustomRecipe recipe) {

        ItemStack result = recipe.getRecipe().getResult();
        List<Component> lore = result.lore();
        if (lore == null) lore = new ArrayList<>();

        lore.add(Component.text("§r§7Click to view"));
        lore.add(Component.text(" "));
        lore.add(Component.text("§r§5NamespacedKey:"));
        lore.add(Component.text("§r§6  " + recipe.getNamespacedKey().toString()));
        lore.add(Component.text("§r§5Type: "));
        lore.add(Component.text("§r§6  " + getType(recipe.getRecipe())));

        result.lore(lore);
        return new GuiItem(result, event -> openRecipeDisplayGUI(recipe));
    }

    private void openRecipeDisplayGUI(CustomRecipe recipe) {
        if (recipe.getRecipe() instanceof ShapedRecipe)
            new ShapedCraftingGUI(recipe, this, getViewers().get(0)).show(getViewers().get(0));
        if (recipe.getRecipe() instanceof ShapelessRecipe)
            new ShapelessCraftingGUI(recipe, this, getViewers().get(0)).show(getViewers().get(0));
        if (recipe.getRecipe() instanceof SmithingRecipe)
            new SmithingGUI(recipe, this, getViewers().get(0)).show(getViewers().get(0));
        if (recipe.getRecipe() instanceof StonecuttingRecipe)
            new StonecuttingGUI(recipe, this, getViewers().get(0)).show(getViewers().get(0));
        if (recipe.getRecipe() instanceof FurnaceRecipe)
            new SmeltingGUI(recipe, this, getViewers().get(0)).show(getViewers().get(0));
        if (recipe.getRecipe() instanceof BlastingRecipe)
            new BlastingRecipeGUI(recipe, this, getViewers().get(0)).show(getViewers().get(0));
        if (recipe.getRecipe() instanceof SmokingRecipe)
            new SmokerRecipeGUI(recipe, this, getViewers().get(0)).show(getViewers().get(0));
        if (recipe.getRecipe() instanceof CampfireRecipe)
            new CampfireCookingGUI(recipe, this, getViewers().get(0)).show(getViewers().get(0));
    }

    private String getType(Recipe recipe) {
        if (recipe instanceof ShapedRecipe) return "Shaped Crafting";
        if (recipe instanceof ShapelessRecipe) return "Shapeless Crafting";
        if (recipe instanceof SmithingRecipe) return "Smithing";
        if (recipe instanceof StonecuttingRecipe) return "Stonecutting";
        if (recipe instanceof FurnaceRecipe) return "Smelting";
        if (recipe instanceof BlastingRecipe) return "Blasting";
        if (recipe instanceof SmokingRecipe) return "Smoking";
        if (recipe instanceof CampfireRecipe) return "Campfire";
        return "";
    }
}
