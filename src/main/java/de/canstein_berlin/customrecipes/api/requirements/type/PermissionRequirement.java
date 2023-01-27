package de.canstein_berlin.customrecipes.api.requirements.type;

import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.requirements.BaseRequirement;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PermissionRequirement extends BaseRequirement {

    ArrayList<String> permissions;

    public PermissionRequirement() {
        super("permission");

        this.permissions = new ArrayList<>();
    }

    private PermissionRequirement(ArrayList<String> permissions) {
        super("permission");

        this.permissions = permissions;
    }

    /**
     * The player has to have the defined permission, or one of them if multiple are defined. If you need the player to have multiple permission just use another permission requirement
     * <p>
     *
     * <p>
     * Structure of the Requirement:
     * {@code
     * {
     * "type": "permission",
     * "permissions": "perm.a"
     * }
     * }
     * <p>
     * or {@code
     * {
     * "type": "permission",
     * "permissions": [
     * "perm.a",
     * "perm.b"
     * ]
     * }
     * }
     *
     * @param object Requirement json
     * @return new PermissionRequirement
     * @throws MalformedRecipeFileException
     */
    @Override
    public BaseRequirement parse(JSONObject object) throws MalformedRecipeFileException {
        if (!object.has("permissions"))
            throw new MalformedRecipeFileException("Missing element \"permissions\" for permission requirement");

        ArrayList<String> permissions = new ArrayList<>();
        JSONArray array = object.optJSONArray("permissions");
        if (array != null) {
            //Parse Array
            for (int i = 0; i < array.length(); i++) {
                permissions.add(array.getString(i));
            }
        } else {
            String perm = object.getString("permissions");
            permissions.add(perm);
        }

        return new PermissionRequirement(permissions);
    }

    @Override
    public JSONObject serialize() {
        JSONObject jsonObject = new JSONObject();
        //Type
        jsonObject.put("type", getId());

        //Gamemodes
        jsonObject.put("permissions", permissions);

        return jsonObject;
    }

    @Override
    public boolean check(CraftItemEvent event) {
        if (!(event.getInventory().getHolder() instanceof Player)) return false;

        Player p = ((Player) event.getInventory().getHolder());
        for (String s : permissions) {
            if (p.hasPermission(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "PermissionRequirement{" +
                "permissions=" + permissions +
                '}';
    }
}
