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

package xyz.water.rmatrix.cmod.craftguibelike.utils.favoriteMiscUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.recipe.RecipeFinder;

import java.util.List;
import java.util.stream.Collectors;

public class ClientRecipeBookHelper {

    /**
     * 获取所有解锁的配方, 未合并版
     */
    public static List<RecipeResultCollection> getAllUnmergedRecipes(ClientRecipeBook clientRecipeBook){
        List<RecipeResultCollection> mergedResults = clientRecipeBook.getOrderedResults();

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return null;
        RecipeFinder finder = new RecipeFinder();
        player.getInventory().populateRecipeFinder(finder);

        return mergedResults.stream().flatMap(collection ->
                collection.getAllRecipes().stream().map(entry -> {
                RecipeResultCollection collection1 = new RecipeResultCollection(List.of(entry));
                collection1.populateRecipes(finder, k -> true);
                return collection1;
            })).collect(Collectors.toList());
    }

    public static List<RecipeResultCollection> getAllUnmergedRecipes(ClientPlayerEntity player){
        if(player == null) return null;
        ClientRecipeBook recipeBook = player.getRecipeBook();
        RecipeFinder finder = new RecipeFinder();
        player.getInventory().populateRecipeFinder(finder);
        return recipeBook.getOrderedResults().stream().flatMap(collection ->
                collection.getAllRecipes().stream().map(entry -> {
                    RecipeResultCollection collection1 = new RecipeResultCollection(List.of(entry));
                    collection1.populateRecipes(finder, k -> true);
                    return collection1;
                })).collect(Collectors.toList());
    }
}
