package de.canstein_berlin.customrecipes.api.exceptions;

/**
 * Exception thrown when a recipe file is malformed
 */
public class MalformedRecipeFileException extends Exception{

    public MalformedRecipeFileException(String message){
        super(message);
    }

}
