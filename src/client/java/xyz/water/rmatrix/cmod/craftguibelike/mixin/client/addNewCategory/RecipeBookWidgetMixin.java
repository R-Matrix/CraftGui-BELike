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

package xyz.water.rmatrix.cmod.craftguibelike.mixin.client.addNewCategory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;

import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.water.rmatrix.cmod.craftguibelike.CraftGuiBELikeClient;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.EnhancedRecipeBookCategoryAPIImpl;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin {

    @Unique
    private final int customButton_k = 27;
    @Shadow
    private int parentHeight;
    @Unique
    private final int customButton_j = (this.parentHeight - 166) / 2 - 30;
    @Shadow
    private int leftOffset;
    @Shadow
    private int parentWidth;
    @Unique
    private final int customButton_i =  (this.parentWidth - 147) / 2 - this.leftOffset + 30;
    @Shadow
    private ClientRecipeBook recipeBook;
    @Unique
    private int customButton_l = 0;
    @Unique
    private int customButton_r = 0;
    @Unique
    private int customButton_hasShownCount = 0;

    @WrapOperation(method = "refreshTabButtons", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget;hasKnownRecipes(Lnet/minecraft/client/recipebook/ClientRecipeBook;)Z"))
    private boolean setFavoriteDisplayAlways(
            RecipeGroupButtonWidget instance,
            ClientRecipeBook recipeBook,
            Operation<Boolean> original,
            @Local(argsOnly = true) boolean filteringCraftable){

        if(instance.getCategory() instanceof RecipeBookCategory category){
            Identifier id = Registries.RECIPE_BOOK_CATEGORY.getId(category);
            EnhancedRecipeBookCategoryAPIImpl apiImpl = EnhancedRecipeBookCategoryAPIImpl.getINSTANCE();

            if (CraftGuiBELikeClient.FAVORITE_CATEGORY_ID.equals(id)){
                return true;
            }

            else if(apiImpl.isRegisteredCategory(id)){
                if(apiImpl.getRecipesUnderCategory(category).isEmpty()) return false;
                instance.setPosition(customButton_i + customButton_k * customButton_l++, customButton_j);
                instance.checkForNewRecipes(this.recipeBook, filteringCraftable);
                customButton_hasShownCount++;
            }
        }
        return original.call(instance, recipeBook);
    }
}
