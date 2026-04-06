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

package xyz.water.rmatrix.cmod.craftguibelike.mixin.client.mouseScollOnRecipeBool;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xyz.water.rmatrix.cmod.craftguibelike.button.MouseScrollEnableButton;

@Mixin(RecipeBookScreen.class)
public abstract class RecipeBookScreenMixin extends HandledScreenHokeMixin{

    @Shadow
    @Final
    private RecipeBookWidget<?> recipeBook;

    protected RecipeBookScreenMixin(Text title) {
        super(title);
    }

    @Override
    protected boolean mouseScrolledHoke(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, Operation<Boolean> original){

        if(recipeBook != null && recipeBook.isOpen()){
            if(MouseScrollEnableButton.isEnableMouseScroll() && isOnWeight(mouseX, mouseY, this.recipeBook)){
                RecipeBookResultAccess recipeBookResultAccess = (RecipeBookResultAccess) ((RecipeBookWidgetAccess) recipeBook).craftGui_BELike$getRecipesArea();

                int pageAmount = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) ? 5 : 1;

                int currentpage = recipeBookResultAccess.craftGui_BELike$getCurrentPage();
                int pageCount = recipeBookResultAccess.craftGui_BELike$getPageCount();

                if(verticalAmount < 0){
                    int goalPage = Math.min(pageCount - 1, currentpage + pageAmount);
                    recipeBookResultAccess.craftGui_BELike$setCurrentPage(goalPage);
                }
                else if(verticalAmount > 0){
                    int goalPage = Math.max(0, currentpage - pageAmount);
                    recipeBookResultAccess.craftGui_BELike$setCurrentPage(goalPage);
                }

                recipeBook.refresh();
            }
        }

        return super.mouseScrolledHoke(mouseX, mouseY, horizontalAmount, verticalAmount, original);

    }

    @Unique
    private boolean isOnWeight(double mouseX, double mouseY, RecipeBookWidget<?> widget){
        int x = ((RecipeBookWidgetAccess)widget).craftGui_BELike$getLeft();
        int y = ((RecipeBookWidgetAccess)widget).craftGui_BELike$getTop();

        // 配方书字段的weight(int field_32408 = 147);
        // height(public static final int field_32409 = 166);
        return mouseX >= x && mouseX <= x + 147 && mouseY >= y && mouseY <= y + 166;
    }

}
