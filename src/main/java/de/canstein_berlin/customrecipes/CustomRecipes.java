package de.canstein_berlin.customrecipes;

import de.canstein_berlin.customrecipes.api.CustomRecipesAPI;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.commands.ListRecipesCommand;
import de.canstein_berlin.customrecipes.listeners.ItemCraftListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public final class CustomRecipes extends JavaPlugin {

    public static CustomRecipes instance;
    public static String PREFIX = "§b[§6CustomRecipes§b]§r ";

    public static CustomRecipes getInstance() {
        return instance;
    }

    public static String getLang(String key, String... args) {
        String lang = CustomRecipes.getInstance().getConfig().getString(key, "&cUnknown language key &6" + key);
        for (int i = 0; i + 1 < args.length; i += 2) {
            lang = lang.replace("%" + args[i] + "%", args[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', lang);
    }

    @Override
    public void onLoad() {
        CustomRecipesAPI.getInstance();
    }

    @Override
    public void onEnable() {
        instance = this;

        //Listeners
        Bukkit.getPluginManager().registerEvents(new ItemCraftListener(), this);

        //Set up config
        saveResource("config.yml", true);
        PREFIX = getLang("prefix");

        //Commands
        getCommand("listrecipes").setExecutor(new ListRecipesCommand());
        getCommand("listrecipes").setTabCompleter(new ListRecipesCommand());

        //Load recipes from recipes folder
        new File(getDataFolder(), "/out/").mkdir();
        for (String file : getRecipesFromFolder("recipes")) {
            CustomRecipe recipe = CustomRecipesAPI.getInstance().createAndRegister(this, file);
            if (recipe == null) continue;
            recipe.writeToFile(new File(getDataFolder(), "/out/"));
            CustomRecipesAPI.getInstance().unregisterRecipe(recipe);
        }
    }

    private ArrayList<String> getRecipesFromFolder(String dirName) {
        File file = new File(getDataFolder(), dirName);
        if (!file.exists()) file.mkdirs();

        ArrayList<String> list = new ArrayList<>();
        for (File f : file.listFiles()) {
            if (!f.isFile()) continue;
            if (f.getName().endsWith(".json")) list.add(dirName + "/" + f.getName());
        }
        return list;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
