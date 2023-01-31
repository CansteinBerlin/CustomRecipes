package de.canstein_berlin.customrecipes.commands;

import de.canstein_berlin.customrecipes.CustomRecipes;
import de.canstein_berlin.customrecipes.api.CustomRecipesAPI;
import de.canstein_berlin.customrecipes.gui.ListAllRecipesGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/*
    Permission: customrecipes.commands.listallrecipesgui
 */
public class ListRecipesCommand implements CommandExecutor {

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

        new ListAllRecipesGUI(CustomRecipesAPI.getInstance().getDefinedRecipes()).show(((Player) sender));
        sender.sendMessage(CustomRecipes.getLang("lang.commands.listAllRecipes.success"));

        return true;
    }

}
