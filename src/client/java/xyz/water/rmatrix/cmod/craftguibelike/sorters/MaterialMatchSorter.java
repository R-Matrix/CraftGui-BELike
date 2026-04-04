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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import xyz.water.rmatrix.cmod.craftguibelike.api.IRecipeSorter;

import java.util.*;

public class MaterialMatchSorter implements IRecipeSorter {

    @Override
    public String getName(){
        return "craftgui-belike.sort.material_match_sort";
    }

    @Override
    public List<RecipeResultCollection> sortRecipes(List<RecipeResultCollection> recipes) {
        PlayerEntity playerEntity = MinecraftClient.getInstance().player;

        if (playerEntity == null) return recipes;

        ScreenHandler screenHandler = playerEntity.currentScreenHandler;
        RecipeFinder finder = new RecipeFinder();
        List<ItemStack> allItems = new ArrayList<>();

        for(Slot slot : screenHandler.slots){
            if(slot instanceof CraftingResultSlot) continue;
            ItemStack itemStack = slot.getStack();
            if(!itemStack.isEmpty()) {
                finder.addInputIfUsable(itemStack);
                allItems.add(itemStack);
            }
        }

        return recipes.stream()
                .sorted((a, b) -> {
                    int tierA = calculateTier(a, finder, allItems);
                    int tierB= calculateTier(b, finder, allItems);
                    return Integer.compare(tierB, tierA);
                })
            .toList();

    }


    private int calculateTier(RecipeResultCollection collection, RecipeFinder finder, List<ItemStack> allItems) {
        boolean hasAnyMatch = false;
        for(RecipeDisplayEntry recipe : collection.getAllRecipes()){
            if(recipe.isCraftable(finder)){
                return 3;
            }

            Optional<List<Ingredient>> requirements = recipe.craftingRequirements();
            if(requirements.isPresent()){
                for(Ingredient ingredient : requirements.get()){
                    if(!ingredient.isEmpty() && checkIngredient(ingredient, allItems)){
                        hasAnyMatch = true;
                        break;
                    }
                }
            }
        }

        return hasAnyMatch ? 2 : 1;
    }

    private boolean checkIngredient(Ingredient ingredient, List<ItemStack> allItems) {
        for (ItemStack stack : allItems) {
            if (ingredient.test(stack)) {
                return true;
            }
        }
        return false;
    }

}