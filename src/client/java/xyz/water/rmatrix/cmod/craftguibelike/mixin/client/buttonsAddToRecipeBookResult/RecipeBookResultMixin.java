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

package xyz.water.rmatrix.cmod.craftguibelike.mixin.client.buttonsAddToRecipeBookResult;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.water.rmatrix.cmod.craftguibelike.button.MouseScrollEnableButton;
import xyz.water.rmatrix.cmod.craftguibelike.button.SortButton;
import xyz.water.rmatrix.cmod.craftguibelike.utils.RecipeBookButtonManager;

@Mixin(RecipeBookResults.class)
public abstract class RecipeBookResultMixin {

    @Unique
    private final RecipeBookButtonManager recipeBookButtonManager = RecipeBookButtonManager.getINSTANCE();
    @Shadow @Final
    private RecipeBookWidget<?> recipeBookWidget;

    @Inject(method = "initialize", at = @At("TAIL"))
    private void addSortButton(MinecraftClient client, int parentLeft, int parentTop, CallbackInfo ci){
        recipeBookButtonManager.resetWith(
                new SortButton(this.recipeBookWidget ,parentLeft + 10, parentTop + 137),
                new MouseScrollEnableButton(parentLeft + 20, parentTop + 137)
        );
    }

    @WrapOperation(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ToggleButtonWidget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V", ordinal = 0))
    private void drawSortButton(ToggleButtonWidget instance, DrawContext drawContext, int mouseX, int mouseY, float delta, Operation<Void> original){

        recipeBookButtonManager.drawButtons(drawContext, mouseX, mouseY, delta, recipeBookWidget);
        original.call(instance, drawContext, mouseX, mouseY, delta);
    }

    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ToggleButtonWidget;mouseClicked(DDI)Z", ordinal = 0))
    private boolean onClickSortButton(ToggleButtonWidget instance, double mouseX, double mouseY, int button, Operation<Boolean> original){
        if(recipeBookButtonManager.onButtonsClick(mouseX, mouseY, button)) return false;
        return original.call(instance, mouseX, mouseY, button);
    }

}
