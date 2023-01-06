package de.canstein_berlin.customrecipes;

import org.bukkit.plugin.java.JavaPlugin;

public final class CustomRecipes extends JavaPlugin {

    @Override
    public void onEnable() {

        String[] files = new String[]{"blasting.json", "campfire_cooking.json", "shaped.json", "shapeless.json", "smelting.json", "smoking.json", "stonecutting.json"};

        for(String file : files){
            if(getResource(file) != null) saveResource(file, true);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
