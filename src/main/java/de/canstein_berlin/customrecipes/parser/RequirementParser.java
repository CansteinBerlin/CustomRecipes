package de.canstein_berlin.customrecipes.parser;

import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.requirements.BaseRequirement;
import de.canstein_berlin.customrecipes.requirements.PermissionRequirement;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequirementParser {

    public static ArrayList<BaseRequirement> parseRequirements(JSONObject jsonObject) throws InvalidRecipeValueException, MalformedRecipeFileException {
        if(!jsonObject.has("requirements")) return new ArrayList<>();

        ArrayList<BaseRequirement> requirements = new ArrayList<>();
        JSONArray jsonArray = jsonObject.optJSONArray("requirements");
        if(jsonArray == null) throw new InvalidRecipeValueException("Requirements field has to be an array");

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject element = jsonArray.optJSONObject(i);

            if(!element.has("type")){
                throw new InvalidRecipeValueException("Missing requirements type");
            }
            switch (element.getString("type")){
                case PermissionRequirement.ID -> requirements.add(new PermissionRequirement(element));
                default -> throw new InvalidRecipeValueException("Unknown requirement type: " + jsonObject.getString("type"));

            }
        }


        return requirements;
    }

}
