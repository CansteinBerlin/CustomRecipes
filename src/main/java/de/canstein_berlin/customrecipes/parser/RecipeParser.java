package de.canstein_berlin.customrecipes.parser;

import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class RecipeParser {

    protected JSONObject recipeJson;
    protected JavaPlugin plugin;
    protected File file;

    public RecipeParser(JavaPlugin plugin, File file, JSONObject jsonObject){
        this.recipeJson = jsonObject;
        this.plugin = plugin;
        this.file = file;
    }

    public abstract Recipe parseRecipe() throws MalformedRecipeFileException, InvalidRecipeValueException;

    protected ItemStack getItemStack(Object object) throws MalformedRecipeFileException {
        if(object instanceof JSONObject) return getItemStack(((JSONObject) object));
        if(object instanceof String){
            return new ItemStack(Material.valueOf(((String) object).replace("minecraft:", "").toUpperCase(Locale.ROOT)));
        }
        return null;
    }

    private ItemStack getItemStack(JSONObject jsonObject) throws MalformedRecipeFileException {
        if(!jsonObject.has("item")) throw new MalformedRecipeFileException("Could not retrieve Itemstack. Missing key \"item\"");
        ItemStack result = new ItemStack(Material.valueOf(jsonObject.getString("item").replace("minecraft:", "").toUpperCase(Locale.ROOT)));

        if(jsonObject.has("count")) result.setAmount(jsonObject.getInt("count"));

        //NBT Support
        if(jsonObject.has("nbt")){
            NBTContainer nbtContainer = new NBTContainer(jsonObject.getJSONObject("nbt").toString());
            NBTItem nbtItem = new NBTItem(result);
            nbtItem.mergeCompound(nbtContainer);
            result = nbtItem.getItem();
        }

        return result;
    }

    private RecipeChoice getRecipeChoice(JSONObject jsonObject) throws InvalidRecipeValueException, MalformedRecipeFileException {
        if(jsonObject.has("tag")){
            NamespacedKey key = NamespacedKey.fromString(jsonObject.getString("tag"));
            if(key == null) throw new InvalidRecipeValueException("Invalid Tag Key: \" + " + jsonObject.getString("tag") + "\"");
            Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class);
            if(tag == null) throw new InvalidRecipeValueException("Invalid Tag Key: \" + " + jsonObject.getString("tag") + "\"");
            return new RecipeChoice.MaterialChoice(tag);
        }
        if(!jsonObject.has("item")){
            throw new MalformedRecipeFileException("Missing key \"item\" or \"tag\"");
        }

        Material m = Material.valueOf(jsonObject.getString("item").replace("minecraft:", "").toUpperCase(Locale.ROOT));
        if(!jsonObject.has("nbt") && !jsonObject.has("count")){
            return new RecipeChoice.MaterialChoice(m);
        }
        ItemStack stack = new ItemStack(m);
        if(jsonObject.has("count")) stack.setAmount(jsonObject.getInt("count"));

        //NBT Support
        if(jsonObject.has("nbt")){
            NBTContainer nbtContainer = new NBTContainer(jsonObject.getJSONObject("nbt").toString());
            NBTItem nbtItem = new NBTItem(stack);
            nbtItem.mergeCompound(nbtContainer);
            stack = nbtItem.getItem();
        }

        return new RecipeChoice.ExactChoice(stack);
    }

    private RecipeChoice getMultipleRecipeChoice(JSONArray jsonArray) throws InvalidRecipeValueException, MalformedRecipeFileException {
        ArrayList<RecipeChoice> elements = new ArrayList<>();
        for(int i = 0; i< jsonArray.length(); i++){
            if(jsonArray.optJSONObject(i) == null){
                throw new MalformedRecipeFileException("Malformed ingredient list");
            }
            elements.add(getRecipeChoice(jsonArray.getJSONObject(i)));
        }

        boolean materialChoice = true;
        for(RecipeChoice c : elements){
            if(c instanceof RecipeChoice.ExactChoice) {
                materialChoice = false;
                break;
            }
        }

        if(materialChoice){
            ArrayList<Material> materials = new ArrayList<>();
            for(RecipeChoice c : elements){
                materials.addAll(((RecipeChoice.MaterialChoice) c).getChoices());
            }
            return new RecipeChoice.MaterialChoice(materials);
        }

        ArrayList<ItemStack> stacks = new ArrayList<>();
        for(RecipeChoice c : elements){
            if(c instanceof RecipeChoice.ExactChoice){
                stacks.addAll(((RecipeChoice.ExactChoice) c).getChoices());
            }else{
                stacks.addAll(((RecipeChoice.MaterialChoice) c).getChoices().stream().map(ItemStack::new).collect(Collectors.toList()));
            }
        }
        return new RecipeChoice.ExactChoice(stacks);
    }

    public RecipeChoice getRecipeChoice(Object o) throws InvalidRecipeValueException, MalformedRecipeFileException {
        if(o instanceof JSONObject) return getRecipeChoice(((JSONObject) o));
        else if(o instanceof JSONArray) return getMultipleRecipeChoice(((JSONArray) o));
        throw new InvalidRecipeValueException("Malformed Ingredient list");
    }

    public JSONObject getRecipeJson() {
        return recipeJson;
    }
}
