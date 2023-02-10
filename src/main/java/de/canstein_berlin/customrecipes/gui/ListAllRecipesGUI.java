package de.canstein_berlin.customrecipes.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.canstein_berlin.customrecipes.CustomRecipes;
import de.canstein_berlin.customrecipes.api.CustomRecipesAPI;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.builder.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

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

    public static String getNameFromMaterial(Material m) {
        char[] chars = m.name().replace("_", " ").toLowerCase().toCharArray();

        for (int counter = 0; counter < chars.length; counter++) {
            if (counter == 0) chars[counter] = ("" + chars[counter]).toUpperCase().toCharArray()[0];
            else {
                if ((chars[counter - 1]) == ' ') chars[counter] = ("" + chars[counter]).toUpperCase().toCharArray()[0];
            }
        }
        StringBuilder out = new StringBuilder();
        for (char c : chars) {
            out.append(c);
        }
        return out.toString();
    }

    private GuiItem getItemFromRecipe(CustomRecipe recipe) {

        ItemStack result = recipe.getRecipe().getResult();

        //Initial Setup
        applyItemName(result, recipe);
        applyItemLore(result, recipe);

        return new GuiItem(result, event -> {
            if (event.isShiftClick()) {
                CustomRecipesAPI.getInstance().toggleDisabled(recipe);

                //Update Item
                applyItemName(result, recipe);
                applyItemLore(result, recipe);

                update();
                return;
            }

            openRecipeDisplayGUI(recipe);
        });
    }

    private void applyItemLore(ItemStack result, CustomRecipe recipe) {
        List<Component> lore = result.lore();
        if (lore == null) lore = new ArrayList<>();

        lore.clear();

        lore.add(Component.text("§r§7Click to view"));
        lore.add(Component.text("§r§7Shift click to " + (!CustomRecipesAPI.getInstance().isDisabled(recipe) ? "enable" : "disable")));
        lore.add(Component.text(" "));
        lore.add(Component.text((!CustomRecipesAPI.getInstance().isDisabled(recipe) ? "§aEnabled" : "§cDisabled")));
        lore.add(Component.text(" "));
        lore.add(Component.text("§r§5NamespacedKey:"));
        lore.add(Component.text("§r§6  " + recipe.getNamespacedKey().toString()));
        lore.add(Component.text("§r§5Type: "));
        lore.add(Component.text("§r§6  " + getType(recipe.getRecipe())));

        result.lore(lore);
    }

    private void applyItemName(ItemStack result, CustomRecipe recipe) {
        String titleText = "[" + getNameFromMaterial(result.getType()) + "]";

        Component title = Component.text(
                titleText,
                Style.style(!CustomRecipesAPI.getInstance().isDisabled(recipe) ? TextColor.color(0, 255, 0) : TextColor.color(255, 0, 0))
        ).decoration(TextDecoration.ITALIC, false);

        ItemMeta meta = result.getItemMeta();
        meta.displayName(title);
        result.setItemMeta(meta);
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
