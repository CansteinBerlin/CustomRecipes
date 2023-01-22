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
