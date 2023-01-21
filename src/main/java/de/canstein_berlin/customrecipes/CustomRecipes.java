package de.canstein_berlin.customrecipes;

import de.canstein_berlin.customrecipes.api.CustomRecipesAPI;
import de.canstein_berlin.customrecipes.api.recipes.CustomRecipe;
import de.canstein_berlin.customrecipes.debug.ClassDebug;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

public final class CustomRecipes extends JavaPlugin {

    public static CustomRecipes instance;

    public static CustomRecipes getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        CustomRecipesAPI.getInstance();
    }

    @Override
    public void onEnable() {
        instance = this;

        String[] files = new String[]{};


        try {
            files = getFiles().toArray(new String[]{});
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        for (String file : files) {
            System.out.println(file);
            saveResource(file.substring(1), true);

            CustomRecipe recipe = CustomRecipe.fromResource(this, file);

            if (recipe != null) {
                System.out.println("#####################################" + file + "############################################");
                System.out.println(new ClassDebug(recipe.getRecipe()));
                System.out.println("#####################################" + file + "############################################");

                CustomRecipesAPI.getInstance().registerRecipes(recipe);
            } else {
                System.out.println("Recipe is null");
            }

        }
    }

    private ArrayList<String> getFiles() throws URISyntaxException, IOException {
        URI uri = getClassLoader().getResource("test").toURI();
        Path myPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem;
            try {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            } catch (FileSystemAlreadyExistsException e) {
                fileSystem = FileSystems.getFileSystem(uri);
            }
            myPath = fileSystem.getPath("/test");
        } else {
            myPath = Paths.get(uri);
        }
        Stream<Path> walk = Files.walk(myPath, 1);
        ArrayList<String> list = new ArrayList<>();
        for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
            String path = it.next().toString();
            if (path.endsWith(".json")) list.add(path);
        }
        return list;
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
