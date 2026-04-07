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

package xyz.water.rmatrix.cmod.craftguibelike.sorters;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import xyz.water.rmatrix.cmod.craftguibelike.api.IRecipeSorter;

import java.util.*;

public class ContainGroupSorter implements IRecipeSorter {

    @Override
    public String getName() {
        return "craftgui-belike.contain_group_sort";
    }

    @Override
    public List<RecipeResultCollection> sortRecipes(List<RecipeResultCollection> recipes) {

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null || recipes.isEmpty()) return recipes;

        RecipeFinder finder = new RecipeFinder();
        player.getInventory().populateRecipeFinder(finder);

        Map<Integer, List<RecipeDisplayEntry>> groups = new LinkedHashMap<>();
        List<Integer> groupOrder = new ArrayList<>();

        for (RecipeResultCollection collection : recipes) {
            for (RecipeDisplayEntry recipe : collection.getAllRecipes()) {
                int groupId = getGroupId(recipe, collection);
                groups.computeIfAbsent(groupId, g -> {
                    groupOrder.add(g);
                    return new ArrayList<>();
                }).add(recipe);
            }
        }

        List<RecipeResultCollection> result = new ArrayList<>();

        for (Integer groupId : groupOrder) {
            List<RecipeDisplayEntry> groupRecipes = groups.get(groupId);

            groupRecipes.sort(Comparator.comparing(this::getRecipeName));

            for(RecipeDisplayEntry entry : groupRecipes) {
                RecipeResultCollection newCollection = new RecipeResultCollection(List.of(entry));
                newCollection.populateRecipes(finder, recipeDisplay -> true);
                result.add(newCollection);
            }
        }

        return result;
    }

    private int getGroupId(RecipeDisplayEntry recipe, RecipeResultCollection collection) {

        OptionalInt group = recipe.group();
        return group.orElse(collection.hashCode());
    }

    private String getRecipeName(RecipeDisplayEntry recipe) {

        return recipe.getStacks(new ContextParameterMap.Builder().build(new ContextType.Builder().build()))
                .getFirst().getItem().getName().getString();
    }
}
