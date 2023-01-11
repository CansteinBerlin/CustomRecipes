package de.canstein_berlin.customrecipes.requirements;

import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import org.bukkit.event.inventory.CraftItemEvent;
import org.json.JSONObject;

public abstract class BaseRequirement {

    protected final JSONObject jsonObject;

    public BaseRequirement(JSONObject jsonObject) throws MalformedRecipeFileException {
        this.jsonObject = jsonObject;
        parse();
    }

    public abstract boolean check(CraftItemEvent event);
    protected abstract void parse() throws MalformedRecipeFileException;

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
