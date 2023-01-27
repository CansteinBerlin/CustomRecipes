package de.canstein_berlin.customrecipes.api.recipes.serializer;

import de.canstein_berlin.customrecipes.api.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class BaseRecipeSerializer {

    private final String id;
    private final Class<?> recipeClass;

    public BaseRecipeSerializer(String id, Class<?> recipeClass) {
        this.id = id;
        this.recipeClass = recipeClass;
    }

    /**
     * Method called if a json object should be interpreted as a recipe
     *
     * @param jsonObject JsonObject to be parsed
     * @param plugin     Plugin that wants this recipe to be parsed
     * @param filename   Filename of the file, used for NamespacedKey
     * @return Parsed custom recipe
     * @throws MalformedRecipeFileException
     * @throws InvalidRecipeValueException
     */
    public abstract CustomRecipe parse(JSONObject jsonObject, JavaPlugin plugin, String filename) throws MalformedRecipeFileException, InvalidRecipeValueException;

    /**
     * Method called when a recipe should be converted to json
     *
     * @param recipe Recipe to be serialized
     * @return Serialized recipe
     */
    public abstract JSONObject serialize(Recipe recipe);

    public String getId() {
        return id;
    }

    /**
     * Convert a json object to an ItemStack
     *
     * @param object Json Object or string
     * @return Interpreted ItemStack
     * @throws MalformedRecipeFileException
     */
    protected ItemStack getItemStack(Object object) throws MalformedRecipeFileException {
        if (object instanceof JSONObject) return getItemStack(((JSONObject) object));
        if (object instanceof String) {
            return new ItemStack(Material.valueOf(((String) object).replace("minecraft:", "").toUpperCase(Locale.ROOT)));
        }
        return null;
    }

    private ItemStack getItemStack(JSONObject jsonObject) throws MalformedRecipeFileException {
        if (!jsonObject.has("item"))
            throw new MalformedRecipeFileException("Could not retrieve Itemstack. Missing key \"item\"");
        ItemStack result = new ItemStack(Material.valueOf(jsonObject.getString("item").replace("minecraft:", "").toUpperCase(Locale.ROOT)));

        if (jsonObject.has("count")) result.setAmount(jsonObject.getInt("count"));

        //NBT Support
        if (jsonObject.has("nbt")) {
            NBTContainer nbtContainer = new NBTContainer(jsonObject.getJSONObject("nbt").toString());
            NBTItem nbtItem = new NBTItem(result);
            nbtItem.mergeCompound(nbtContainer);
            result = nbtItem.getItem();
        }

        return result;
    }

    /**
     * Serialize an ItemStack to a Json Object
     *
     * @param stack     ItemStack to be converted
     * @param ignoreNBT if true no nbt tag will be generated
     * @return Serialized ItemStack as a Json Object
     */
    protected JSONObject serializeItemStack(ItemStack stack, boolean ignoreNBT) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("item", stack.getType().getKey().toString());

        if (stack.getAmount() > 1) jsonObject.put("count", stack.getAmount());

        if (!ignoreNBT) {
            ReadWriteNBT nbt = NBT.itemStackToNBT(stack);
            if (nbt.hasTag("tag")) {
                jsonObject.put("nbt", new JSONObject(nbt.getCompound("tag").toString()));
            }
        }
        return jsonObject;
    }

    /**
     * Serialize a recipe choice to either a json object of a json array
     *
     * @param recipeChoice Choice to be converted
     * @return Json Object if recipe choice only contain one item; json array of more items are present
     */
    protected Object serializeRecipeChoice(RecipeChoice recipeChoice) {
        if (recipeChoice instanceof RecipeChoice.MaterialChoice)
            return serializeMaterialChoice(((RecipeChoice.MaterialChoice) recipeChoice));
        return serializeExactChoice(((RecipeChoice.ExactChoice) recipeChoice));
    }

    private Object serializeMaterialChoice(RecipeChoice.MaterialChoice recipeChoice) {
        if (recipeChoice.getChoices().size() == 1) {
            return serializeItemStack(new ItemStack(recipeChoice.getChoices().get(0)), true);
        }

        JSONArray jsonArray = new JSONArray();
        for (Material m : recipeChoice.getChoices()) {
            jsonArray.put(serializeItemStack(new ItemStack(m), true));
        }
        return jsonArray;
    }

    private Object serializeExactChoice(RecipeChoice.ExactChoice recipeChoice) {
        if (recipeChoice.getChoices().size() == 1) {
            return serializeItemStack(new ItemStack(recipeChoice.getChoices().get(0)), false);
        }

        JSONArray jsonArray = new JSONArray();
        for (ItemStack stack : recipeChoice.getChoices()) {
            jsonArray.put(serializeItemStack(stack, true));
        }
        return jsonArray;
    }

    private RecipeChoice getRecipeChoice(JSONObject jsonObject) throws InvalidRecipeValueException, MalformedRecipeFileException {
        if (jsonObject.has("tag")) {
            NamespacedKey key = NamespacedKey.fromString(jsonObject.getString("tag"));
            if (key == null)
                throw new InvalidRecipeValueException("Invalid Tag Key: \" + " + jsonObject.getString("tag") + "\"");
            Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class);
            if (tag == null)
                throw new InvalidRecipeValueException("Invalid Tag Key: \" + " + jsonObject.getString("tag") + "\"");
            return new RecipeChoice.MaterialChoice(tag);
        }
        if (!jsonObject.has("item")) {
            throw new MalformedRecipeFileException("Missing key \"item\" or \"tag\"");
        }

        Material m = Material.valueOf(jsonObject.getString("item").replace("minecraft:", "").toUpperCase(Locale.ROOT));
        if (!jsonObject.has("nbt") && !jsonObject.has("count")) {
            return new RecipeChoice.MaterialChoice(m);
        }
        ItemStack stack = new ItemStack(m);
        if (jsonObject.has("count")) stack.setAmount(jsonObject.getInt("count"));

        //NBT Support
        if (jsonObject.has("nbt")) {
            NBTContainer nbtContainer = new NBTContainer(jsonObject.getJSONObject("nbt").toString());
            NBTItem nbtItem = new NBTItem(stack);
            nbtItem.mergeCompound(nbtContainer);
            stack = nbtItem.getItem();
        }

        return new RecipeChoice.ExactChoice(stack);
    }

    private RecipeChoice getMultipleRecipeChoice(JSONArray jsonArray) throws InvalidRecipeValueException, MalformedRecipeFileException {
        ArrayList<RecipeChoice> elements = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.optJSONObject(i) == null) {
                throw new MalformedRecipeFileException("Malformed ingredient list");
            }
            elements.add(getRecipeChoice(jsonArray.getJSONObject(i)));
        }

        boolean materialChoice = true;
        for (RecipeChoice c : elements) {
            if (c instanceof RecipeChoice.ExactChoice) {
                materialChoice = false;
                break;
            }
        }

        if (materialChoice) {
            ArrayList<Material> materials = new ArrayList<>();
            for (RecipeChoice c : elements) {
                materials.addAll(((RecipeChoice.MaterialChoice) c).getChoices());
            }
            return new RecipeChoice.MaterialChoice(materials);
        }

        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (RecipeChoice c : elements) {
            if (c instanceof RecipeChoice.ExactChoice) {
                stacks.addAll(((RecipeChoice.ExactChoice) c).getChoices());
            } else {
                stacks.addAll(((RecipeChoice.MaterialChoice) c).getChoices().stream().map(ItemStack::new).collect(Collectors.toList()));
            }
        }
        return new RecipeChoice.ExactChoice(stacks);
    }

    /**
     * Convert a json object or json array to a RecipeChoice
     *
     * @param o Json Object or Json array to be converted
     * @return Converted RecipeChoice
     * @throws InvalidRecipeValueException
     * @throws MalformedRecipeFileException
     */
    public RecipeChoice getRecipeChoice(Object o) throws InvalidRecipeValueException, MalformedRecipeFileException {
        if (o instanceof JSONObject) return getRecipeChoice(((JSONObject) o));
        else if (o instanceof JSONArray) return getMultipleRecipeChoice(((JSONArray) o));
        throw new InvalidRecipeValueException("Malformed Ingredient list");
    }

    public Class<?> getRecipeClass() {
        return recipeClass;
    }
}
