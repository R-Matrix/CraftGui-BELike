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

package xyz.water.rmatrix.cmod.craftguibelike.api.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import xyz.water.rmatrix.cmod.craftguibelike.api.IFavoritesManager;
import xyz.water.rmatrix.cmod.craftguibelike.mixin.client.favoriteRecipe.ClientRecipeBookAccess;
import xyz.water.rmatrix.cmod.craftguibelike.utils.FavoriteRecipeStorage;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FavoritesManagerImpl implements IFavoritesManager {

    private static FavoritesManagerImpl INSTANCE;

    private final FavoriteRecipeStorage storage;

    public FavoritesManagerImpl(){
        this.storage = FavoriteRecipeStorage.getInstance();
    }

    public static FavoritesManagerImpl getINSTANCE(){
        if(INSTANCE == null) INSTANCE = new FavoritesManagerImpl();
        return INSTANCE;
    }

    @Override
    public boolean isFavorited(UUID playerId, NetworkRecipeId recipeId) {
        return storage.isFavorite(playerId, recipeId.index());
    }

    @Override
    public boolean toggleFavorite(UUID playerId, NetworkRecipeId recipeId) {
        return storage.toggleFavorite(playerId, recipeId.index());
    }

    @Override
    public Set<NetworkRecipeId> getFavorites(UUID playerId) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return Set.of();
        return ((ClientRecipeBookAccess)player.getRecipeBook()).craftGui_BELike$getAllRecipes().keySet().stream()
                .filter(k -> isFavorited(playerId, k))
                .collect(Collectors.toSet());
    }

    public Set<RecipeDisplayEntry> getFavoriteRecipeDisplayEntry(UUID playerId){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return Set.of();
        return  ((ClientRecipeBookAccess)player.getRecipeBook()).craftGui_BELike$getAllRecipes().entrySet().stream()
                .filter(k -> isFavorited(playerId ,k.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    @Override
    public void clearFavorites(UUID playerId) {
        storage.getFavorites(playerId).clear();
        storage.save();
    }

    public void clearFavoritesAll(){
        for (UUID player : storage.getAllPlayer()){
            storage.getFavorites(player).clear();
        }
        storage.save();
    }
}
