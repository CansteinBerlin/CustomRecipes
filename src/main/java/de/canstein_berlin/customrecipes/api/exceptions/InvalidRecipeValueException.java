package de.canstein_berlin.customrecipes.api.exceptions;

/**
 * Exception thrown if a value of a recipe is not as expected
 */
public class InvalidRecipeValueException extends Exception{

    public InvalidRecipeValueException(String msg){
        super(msg);
    }
}

