package de.canstein_berlin.customrecipes.api.recipes;

import de.canstein_berlin.customrecipes.api.CustomRecipesAPI;
import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.serializer.RecipeSerializerFactory;
import de.canstein_berlin.customrecipes.api.requirements.BaseRequirement;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CustomRecipe {

    private final NamespacedKey namespacedKey;
    private final ArrayList<BaseRequirement> requirements;
    private Recipe recipe;

    public CustomRecipe(NamespacedKey namespacedKey, Recipe recipe) {
        this.recipe = recipe;
        this.namespacedKey = namespacedKey;

        requirements = new ArrayList<>();
    }

    public static CustomRecipe fromResource(JavaPlugin plugin, File file) {
        try {
            return RecipeSerializerFactory.getInstance().loadFromFile(plugin, file);
        } catch (IOException | MalformedRecipeFileException | InvalidRecipeValueException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CustomRecipe fromResource(JavaPlugin plugin, String path) {
        return fromResource(plugin, new File(plugin.getDataFolder(), path));
    }

    public boolean compareWithRecipe(Recipe recipe) {
        return CustomRecipesAPI.getNamespacedKeyFromRecipe(recipe).equals(namespacedKey);
    }

    public boolean hasRequirements() {
        return requirements.size() > 0;
    }

    public void addRequirement(BaseRequirement requirement) {
        this.requirements.add(requirement);
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public ArrayList<BaseRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(ArrayList<BaseRequirement> requirements) {
        this.requirements.clear();
        this.requirements.addAll(requirements);
    }

    public boolean canCraft(CraftItemEvent event) {
        for (BaseRequirement r : requirements) {
            if (!r.check(event)) return false;
        }

        return true;
    }

    public void writeToFile(JavaPlugin plugin, String path) {
        writeToFile(plugin, new File(plugin.getDataFolder(), path));
    }

    public void writeToFile(JavaPlugin plugin, File path) {
        JSONObject jsonObject = RecipeSerializerFactory.getInstance().customRecipeToJsonObject(plugin, this);
        if (path.isDirectory()) {
            path = new File(path, namespacedKey.getKey() + ".json");
        }

        if (!path.exists()) {
            path.getParentFile().mkdirs();
            try {
                path.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            FileWriter file = new FileWriter(path);
            file.write(jsonObject.toString(4));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
