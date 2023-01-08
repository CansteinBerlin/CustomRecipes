package de.canstein_berlin.customrecipes.debug;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ClassDebug {

    private final Object object;

    public ClassDebug(Object object) {
        this.object = object;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(object.getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        HashSet<Field> fields = new HashSet<>(Arrays.asList(object.getClass().getDeclaredFields()));
        Class<?> c = object.getClass();
        while(c != null){
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
            if(c.getSuperclass().getName().equalsIgnoreCase("java.lang.object")) break;
            c = c.getSuperclass();
        }
        //print field names paired with their values
        result.append("  Fields: ").append(newLine);
        for (Field field : fields) {
            result.append("    ");
            try {
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(field.get(object));
            } catch (IllegalAccessException ex) {
                //System.out.println(ex);
                field.setAccessible(true);
                try {
                    result.append(field.get(object));
                } catch (IllegalAccessException e) {
                    //System.out.println("Something wrong happened");
                }
            }
            result.append(newLine);
        }

        result.append(newLine).append("  Methods: ").append(newLine);

        for (Method method : object.getClass().getDeclaredMethods()) {
            result.append("    ").append(method.getName()).append(": ").append(method).append(newLine);
        }

        result.append("}");


        return result.toString();
    }
}
