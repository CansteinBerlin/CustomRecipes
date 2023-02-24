package de.canstein_berlin.customrecipes.commands;

import de.canstein_berlin.customrecipes.CustomRecipes;
import de.canstein_berlin.customrecipes.api.CustomRecipesAPI;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.gui.ListAllRecipesGUI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
    Permission: customrecipes.commands.listallrecipesgui
 */
public class ListRecipesCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Player
        if (!(sender instanceof Player)) {
            sender.sendMessage(CustomRecipes.PREFIX + CustomRecipes.getLang("lang.commands.noPlayer"));
            return true;
        }

        // Permissions
        if (!sender.hasPermission("customrecipes.commands.listallrecipesgui")) {
            sender.sendMessage(CustomRecipes.PREFIX + CustomRecipes.getLang("lang.commands.noPermission"));
            return true;
        }

        Player p = ((Player) sender);

        if (args.length == 0) args = new String[]{"all"};

        if (args[0].equalsIgnoreCase("all")) {
            showGUI(p, CustomRecipesAPI.getInstance().getDefinedRecipes(""));
        } else if (args[0].equalsIgnoreCase("minecraft")) {
            showGUI(p, getMinecraftRecipes());
        } else if (args[0].equalsIgnoreCase("disabled")) {
            showGUI(p, getDisabledRecipes());
        } else if (CustomRecipesAPI.getInstance().getDefinedRecipes(args[0]).size() != 0) {
            System.out.println("get specific Recipe");
            showGUI(p, CustomRecipesAPI.getInstance().getDefinedRecipes(args[0]));
        } else {
            sender.sendMessage(CustomRecipes.getLang("lang.commands.listAllRecipes.noNamespace", "namespace", args[0]));
        }
        return true;
    }

    private ArrayList<CustomRecipe> getDisabledRecipes() {
        ArrayList<CustomRecipe> recipes = new ArrayList<>();

        for (Map.Entry<NamespacedKey, Recipe> rec : CustomRecipesAPI.getInstance().getDisabledRecipes().entrySet()) {
            recipes.add(new CustomRecipe(rec.getKey(), rec.getValue()));
        }

        return recipes;
    }

    private void showGUI(Player sender, ArrayList<CustomRecipe> recipes) {
        new ListAllRecipesGUI(recipes).show(sender);
        sender.sendMessage(CustomRecipes.getLang("lang.commands.listAllRecipes.success"));
    }

    private ArrayList<CustomRecipe> getMinecraftRecipes() {
        ArrayList<CustomRecipe> recipes = new ArrayList<>();
        Iterator<Recipe> recipeIterator = Bukkit.getServer().recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            NamespacedKey key = CustomRecipesAPI.getNamespacedKeyFromRecipe(recipe);
            if (key == null) continue;
            if (!key.getNamespace().equalsIgnoreCase("minecraft")) continue;
            recipes.add(new CustomRecipe(key, recipe));
        }
        return recipes;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> possible = new ArrayList<>(List.of("minecraft", "disabled"));
        possible.addAll(CustomRecipesAPI.getInstance().getRegisteredNamespaces());

        return possible
                .stream()
                .filter(s -> s.startsWith(args[0]))
                .sorted()
                .collect(Collectors.toList());
    }
}
