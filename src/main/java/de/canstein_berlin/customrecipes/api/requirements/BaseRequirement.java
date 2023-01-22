package de.canstein_berlin.customrecipes.api.requirements;


import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import org.bukkit.event.inventory.CraftItemEvent;
import org.json.JSONObject;

public abstract class BaseRequirement {

    private final String id;

    public BaseRequirement(String id) {
        this.id = id;
    }

    public abstract BaseRequirement parse(JSONObject object) throws MalformedRecipeFileException;

    public abstract boolean check(CraftItemEvent event);

    public String getId() {
        return id;
    }
}
