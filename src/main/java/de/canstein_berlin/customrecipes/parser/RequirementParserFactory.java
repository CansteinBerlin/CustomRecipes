package de.canstein_berlin.customrecipes.parser;

import de.canstein_berlin.customrecipes.exceptions.InvalidRecipeValueException;
import de.canstein_berlin.customrecipes.exceptions.MalformedRecipeFileException;
import de.canstein_berlin.customrecipes.requirements.BaseRequirement;
import de.canstein_berlin.customrecipes.requirements.PermissionRequirement;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequirementParserFactory {

    public static ArrayList<BaseRequirement> parseRequirements(JSONObject jsonObject) throws InvalidRecipeValueException {
        if(!jsonObject.has("requirements")) return null;

        ArrayList<BaseRequirement> requirements = new ArrayList<>();
        JSONArray jsonArray = jsonObject.optJSONArray("requirements");
        if(jsonArray == null) throw new InvalidRecipeValueException("Requirements field has to be an array");
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
            if(!jsonObject.has("type")){
                throw new InvalidRecipeValueException("Missing requirements type");
            }
            switch (jsonObject.getString("type")){
                case PermissionRequirement.ID:

            }
        }


        return null;
    }

}
