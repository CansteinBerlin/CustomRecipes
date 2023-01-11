package de.canstein_berlin.customrecipes.requirements;

import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PermissionRequirement extends BaseRequirement {

    public static final String ID = "permission";

    private ArrayList<String> permissions;

    public PermissionRequirement(JSONObject jsonObject) throws MalformedRecipeFileException {
        super(jsonObject);
    }

    @Override
    public boolean check(CraftItemEvent event) {
        if(!(event.getInventory().getHolder() instanceof Player)) return false;

        Player p = (Player) event.getInventory().getHolder();
        for(String permission : permissions){
            if(p.hasPermission(permission)) return true;
        }
        return false;
    }

    @Override
    public void parse() throws MalformedRecipeFileException {
        if(!jsonObject.has("permissions")) throw new MalformedRecipeFileException("Missing field \"permissions\" for requirement permission");

        permissions = new ArrayList<>();

        //Check for multiple permissions
        JSONArray list = jsonObject.optJSONArray("permissions");
        if(list != null){
            for(int i = 0; i < list.length(); i++){
                permissions.add(list.getString(i));
            }
            return;
        }

        permissions.add(jsonObject.getString("permissions"));
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    @Override
    public String toString() {
        return "PermissionRequirement{" +
                "permissions=" + permissions +
                '}';
    }
}
