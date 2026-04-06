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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.util.InputUtil;
import net.minecraft.recipe.NetworkRecipeId;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.FavoritesManagerImpl;
import xyz.water.rmatrix.cmod.craftguibelike.utils.favoriteMiscUtils.CraftingHandlerAccess;

@Mixin(RecipeBookResults.class)
public abstract class RecipeBookResultMixin implements CraftingHandlerAccess {

    @Shadow
    private MinecraftClient client;
    @Unique
    private boolean isCraftingHandler;

    @Shadow
    protected abstract void refreshResultButtons();

    @Override
    public void craftGui_BELike$setCraftingHandlerFlag(boolean value){
        this.isCraftingHandler = value;
    }


    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/recipebook/AnimatedResultButton;mouseClicked(DDI)Z", ordinal = 0))
    private boolean toggleFavoriteTags(AnimatedResultButton instance, double mouseX, double mouseY, int button, Operation<Boolean> original){

        boolean bl = original.call(instance, mouseX, mouseY, button);
        if(!this.isCraftingHandler) return bl;

        if(bl && button == 0 && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F)){
            NetworkRecipeId recipeId = instance.getCurrentId();

            if(recipeId == null || this.client.player == null) return true;

            FavoritesManagerImpl.getINSTANCE().toggleFavorite(this.client.player.getUuid(), recipeId);
            this.refreshResultButtons();
        }
        return bl;
    }
}
