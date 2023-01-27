package de.canstein_berlin.customrecipes.api.requirements;


import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import org.bukkit.event.inventory.CraftItemEvent;
import org.json.JSONObject;

public abstract class BaseRequirement {

    //The requirement id that the factory uses to determine the requirement to parse
    private final String id;

    public BaseRequirement(String id) {
        this.id = id;
    }

    /**
     * Method that is called of a requirement json object should be converted to a requirement.
     * The Method is expected to return a NEW Requirement of it's type
     *
     * @param object Requirement json
     * @return The interpreted requirement
     * @throws MalformedRecipeFileException
     */
    public abstract BaseRequirement parse(JSONObject object) throws MalformedRecipeFileException;

    /**
     * Method that is used to serialize a requirement to json
     *
     * @return the json variant of the requirement
     */
    public abstract JSONObject serialize();

    /**
     * Method that is called to check if the requirement is met
     *
     * @param event Raw Event to use
     * @return true if the requirement is met
     */
    public abstract boolean check(CraftItemEvent event);

    public String getId() {
        return id;
    }
}
