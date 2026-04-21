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

package xyz.water.rmatrix.cmod.craftguibelike.mixin.client.favoriteRecipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.screen.AbstractCraftingScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.water.rmatrix.cmod.craftguibelike.CraftGuiBELikeClient;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.EnhancedRecipeBookCategoryAPIImpl;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.FavoritesManagerImpl;
import xyz.water.rmatrix.cmod.craftguibelike.utils.SorterManager;
import xyz.water.rmatrix.cmod.craftguibelike.utils.favoriteMiscUtils.CraftingHandlerAccess;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin {

    @Shadow
    private @Nullable RecipeGroupButtonWidget currentTab;

    @Shadow @Final
    private RecipeBookResults recipesArea;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setCraftingHandlerFlag(AbstractRecipeScreenHandler craftingScreenHandler, List<RecipeBookWidget.Tab> tabs, CallbackInfo ci){
        ((CraftingHandlerAccess)this.recipesArea).craftGui_BELike$setCraftingHandlerFlag(
                craftingScreenHandler instanceof AbstractCraftingScreenHandler
        );
    }

    @WrapOperation(method = "refreshResults", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/recipebook/ClientRecipeBook;getResultsForCategory(Lnet/minecraft/recipe/book/RecipeBookGroup;)Ljava/util/List;"))
    private List<RecipeResultCollection> mi(ClientRecipeBook instance, RecipeBookGroup category, Operation<List<RecipeResultCollection>> original){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (this.currentTab == null) return original.call(instance, category);
        if (!this.currentTab.getCategory().equals(CraftGuiBELikeClient.FAVORITE_CATEGORY) || player == null)
            return original.call(instance, category);

        RecipeFinder finder = new RecipeFinder();
        player.getInventory().populateRecipeFinder(finder);
        
        Set<RecipeDisplayEntry> favorites = FavoritesManagerImpl.getINSTANCE().getFavoriteRecipeDisplayEntry(player.getUuid());

        List<RecipeResultCollection> collections = favorites.stream()
                .map(k -> {
                    RecipeResultCollection collection = new RecipeResultCollection(List.of(k));
                    collection.populateRecipes(finder, recipeDisplay -> true);
                    return collection;
                })
                .collect(Collectors.toList());

        return SorterManager.getINSTANCE().sort(collections);
    }
}
