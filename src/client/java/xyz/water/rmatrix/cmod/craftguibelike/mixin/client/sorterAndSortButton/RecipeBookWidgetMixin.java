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

package xyz.water.rmatrix.cmod.craftguibelike.mixin.client.sorterAndSortButton;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.book.RecipeBookGroup;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.water.rmatrix.cmod.craftguibelike.button.SortButton;
import xyz.water.rmatrix.cmod.craftguibelike.utils.SorterManager;

import java.util.List;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin {

    @Shadow
    private @Nullable RecipeGroupButtonWidget currentTab;

    @Shadow
    private int parentWidth;
    @Shadow
    private int parentHeight;
    @Shadow
    private int leftOffset;
    @Shadow
    private boolean open;
    @Unique
    private SortButton sortButton;

    @Shadow
    public abstract boolean isOpen();

    @Shadow
    protected abstract void refreshResults(boolean resetCurrentPage, boolean filteringCraftable);

    @Shadow
    protected abstract boolean isFilteringCraftable();

    @WrapOperation(method = "refreshResults", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookResults;setResults(Ljava/util/List;ZZ)V"))
    private void sortRecipes(RecipeBookResults instance, List<RecipeResultCollection> resultCollections, boolean resetCurrentPage, boolean filteringCraftable, Operation<Void> original) {
        List<RecipeResultCollection> sortList = resultCollections;
        if (this.currentTab != null) {
            sortList = SorterManager.getINSTANCE().sort(sortList);
        }

        original.call(instance, sortList, resetCurrentPage, filteringCraftable);
    }

    @Inject(method = "initialize", at = @At("TAIL"))
    private void onInitialize(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow, CallbackInfo ci) {
        if (this.sortButton == null) {
            int x = (this.parentWidth - 147) / 2 - this.leftOffset + 10;
            int y = (this.parentHeight - 166) / 2 + 166 - 28;
            this.sortButton = new SortButton(x, y, 10, 10);
        }
        this.sortButton.visible = this.isOpen();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.sortButton != null && this.open) {
            context.getMatrices().push();
            context.getMatrices().translate(0.0F, 0.0F, 200.0F);
            this.sortButton.render(context, mouseX, mouseY, delta);
            context.getMatrices().pop();
        }
    }

    @Inject(method = "setOpen", at = @At("HEAD"))
    private void onSetOpen(boolean opened, CallbackInfo ci) {
        if (this.sortButton != null) {
            this.sortButton.visible = opened;
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.sortButton != null && this.open && this.sortButton.visible) {
            if (this.sortButton.mouseClicked(mouseX, mouseY, button)) {
                this.refreshResults(false, this.isFilteringCraftable());
                cir.setReturnValue(true);
            }
        }
    }
}
