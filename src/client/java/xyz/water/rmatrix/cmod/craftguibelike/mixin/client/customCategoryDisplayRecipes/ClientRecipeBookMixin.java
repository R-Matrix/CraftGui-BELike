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

package xyz.water.rmatrix.cmod.craftguibelike.mixin.client.customCategoryDisplayRecipes;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.EnhancedRecipeBookCategoryAPIImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

    @Unique
    private final EnhancedRecipeBookCategoryAPIImpl enhancedRecipeBookCategoryAPI = EnhancedRecipeBookCategoryAPIImpl.getINSTANCE();


    @ModifyReturnValue(method = "getResultsForCategory", at = @At("RETURN"))
    private List<RecipeResultCollection> modifyReturnRecipes(
            List<RecipeResultCollection> original, RecipeBookGroup category){

        if(category instanceof RecipeBookType) {
            return original;
        }

        if(!(category instanceof RecipeBookCategory cat)) return original;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if(player == null) return original;

        RecipeFinder finder = new RecipeFinder();

        player.getInventory().populateRecipeFinder(finder);

        if(!enhancedRecipeBookCategoryAPI.isRegisteredCategory(cat)){
            return original.stream().filter(collection -> !enhancedRecipeBookCategoryAPI.isEntirelyCustom(collection)).toList();
        }
        else{
            enhancedRecipeBookCategoryAPI.refreshClientDisplayEntries();
            return enhancedRecipeBookCategoryAPI.getEntriesUnderCategory(cat).stream()
                    .map(entry -> {
                         RecipeResultCollection collection = new RecipeResultCollection(List.of(entry));
                         collection.populateRecipes(finder, k -> true);
                         return collection;
                    })
                    .toList();
        }
    }
}
