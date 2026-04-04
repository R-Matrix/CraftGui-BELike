/*
 * Copyright (c) 2026 R-Matrix.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.water.rmatrix.cmod.craftguibelike.utils;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.recipe.NetworkRecipeId;
import xyz.water.rmatrix.cmod.craftguibelike.CraftGuiBELike;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FavoriteRecipeStorage {

    private static final Path FAVORITES_DIR = FabricLoader.getInstance().getConfigDir().resolve("carftgui-belike");

    private static final String FAVORITES_FILE = "favorites.json";
    private static FavoriteRecipeStorage INSTANCE;
    private final Map<UUID, Set<Integer>> playersFavorites = new LinkedHashMap<>();

    public static FavoriteRecipeStorage getInstance(){

        if(INSTANCE == null) {
            INSTANCE = new FavoriteRecipeStorage();
            INSTANCE.load();
        }
        return INSTANCE;
    }


    public void load(){
        try{
            Path file = FAVORITES_DIR.resolve(FAVORITES_FILE);
            if(Files.exists(file)) {
                String content = Files.readString(file);
                JsonObject json = JsonParser.parseString(content).getAsJsonObject();


                for (Map.Entry<String, JsonElement> entry : json.entrySet()){
                    UUID playerID = UUID.fromString(entry.getKey());
                    Set<Integer> favorites = new LinkedHashSet<>();

                    JsonArray arr = entry.getValue().getAsJsonArray();
                    for(JsonElement element : arr){
                        favorites.add(element.getAsInt());
                    }

                    playersFavorites.put(playerID, favorites);
                }
            }
        }
        catch (Exception e){
            CraftGuiBELike.LOGGER.error("Failed to load favorites!", e);
        }
    }


    public void save(){
        try{
            Files.createDirectories(FAVORITES_DIR);
            Path file = FAVORITES_DIR.resolve(FAVORITES_FILE);

            JsonObject json = new JsonObject();
            for (Map.Entry<UUID, Set<Integer>> entry : playersFavorites.entrySet()){
                JsonArray arr = new JsonArray();

                for(Integer recipesIndex : entry.getValue()){
                    arr.add(recipesIndex);
                }

                json.add(entry.getKey().toString(), arr);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Files.writeString(file, gson.toJson(json));
        }
        catch (Exception e){
            CraftGuiBELike.LOGGER.error("Fail to save favorites!", e);
        }
    }

    public Set<Integer> getFavorites(UUID playerID){
        return playersFavorites.computeIfAbsent(playerID, k -> new LinkedHashSet<>());
    }

    public boolean toggleFavorite(UUID playerId, Integer recipes){
        Set<Integer> favorites = getFavorites(playerId);
        boolean isFavorite;

        if(favorites.contains(recipes)){
            isFavorite = false;
            favorites.remove(recipes);
        }
        else{
            isFavorite = true;
            favorites.add(recipes);
        }

        this.save();
        return isFavorite;
    }

    public boolean isFavorite(UUID playerId, Integer recipes){
        return getFavorites(playerId).contains(recipes);
    }
}
