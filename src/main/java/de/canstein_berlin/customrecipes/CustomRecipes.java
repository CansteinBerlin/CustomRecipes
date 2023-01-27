package de.canstein_berlin.customrecipes;

import de.canstein_berlin.customrecipes.api.CustomRecipesAPI;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.listeners.ItemCraftListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public final class CustomRecipes extends JavaPlugin {

    public static CustomRecipes instance;

    public static CustomRecipes getInstance() {
        return instance;
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

        //Load recipes from recipes folder

        for (String file : getRecipesFromFolder("recipes")) {
            CustomRecipe recipe = CustomRecipesAPI.getInstance().createAndRegister(this, file);
            recipe.writeToFile(getDataFolder());

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
