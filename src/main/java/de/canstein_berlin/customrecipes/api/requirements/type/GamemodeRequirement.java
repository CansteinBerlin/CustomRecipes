package de.canstein_berlin.customrecipes.api.requirements.type;

import de.canstein_berlin.customrecipes.api.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.api.requirements.BaseRequirement;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GamemodeRequirement extends BaseRequirement {

    List<GameMode> gameModes;

    public GamemodeRequirement() {
        super("gamemode");

        this.gameModes = new ArrayList<>();
    }

    private GamemodeRequirement(List<GameMode> gameModes) {
        super("gamemode");

        this.gameModes = gameModes;
    }

    /**
     * The player has to have the defined Gamemode, or one of them if multiple are defined.
     * <p>
     * Valid {@link GameMode}: SURVIVAL, ADVENTURE, CREATIVE, SPECTATOR
     * <p>
     * Structure of the Requirement:
     * {@code
     * {
     * "type": "gamemode",
     * "gamemodes": "SURVIVAL"
     * }
     * }
     * <p>
     * or {@code
     * {
     * "type": "gamemode",
     * "gamemodes": [
     * "SURVIVAL",
     * "CREATIVE"
     * ]
     * }
     * }
     *
     * @param object Requirement json
     * @return new GamemodeRequirement
     * @throws MalformedRecipeFileException
     */
    @Override
    public BaseRequirement parse(JSONObject object) throws MalformedRecipeFileException {
        if (!object.has("gamemodes"))
            throw new MalformedRecipeFileException("Missing element \"gamemodes\" for gamemode requirement");

        ArrayList<String> gamemodes = new ArrayList<>();
        JSONArray array = object.optJSONArray("gamemodes");
        if (array != null) {
            //Parse Array
            for (int i = 0; i < array.length(); i++) {
                gamemodes.add(array.getString(i));
            }
        } else {
            String gamemode = object.getString("gamemodes");
            gamemodes.add(gamemode);
        }

        return new GamemodeRequirement(gamemodes.stream().map(String::toUpperCase).map(GameMode::valueOf).collect(Collectors.toList()));
    }

    @Override
    public JSONObject serialize() {
        JSONObject jsonObject = new JSONObject();
        //Type
        jsonObject.put("type", getId());

        //Gamemodes
        jsonObject.put("gamemodes", gameModes);

        return jsonObject;
    }

    @Override
    public boolean check(CraftItemEvent event) {
        if (!(event.getInventory().getHolder() instanceof Player)) return false;

        Player p = ((Player) event.getInventory().getHolder());
        for (GameMode g : gameModes) {
            if (p.getGameMode().equals(g)) {
                return true;
            }
        }
        return false;
    }
}
