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

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.EnhancedRecipeBookCategoryAPIImpl;
import xyz.water.rmatrix.cmod.craftguibelike.utils.favoriteMiscUtils.custom_buttonScaleFlagAccess;

import java.util.function.Function;

@Mixin(RecipeGroupButtonWidget.class)
public abstract class RecipeGroupButtonWidgetMixin extends ToggleButtonWidgetMixin implements custom_buttonScaleFlagAccess {

    @Unique boolean custom_buttonScaleFlag = false;
    @Unique Text displayName;

    @Shadow
    public abstract RecipeBookGroup getCategory();

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"))
    private void drawBackground(DrawContext context, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        if(!custom_buttonScaleFlag) {
            original.call(context, renderLayers, sprite, x, y, width, height);
            return;
        }

        int bgSize = this.toggled ? 24 : 20;
        int bhSizy = this.hovered ? 2 : 0;
        int bgX = x + (width - bgSize) / 2;
        int bgY = y + (height - bgSize) / 2;
        if (!this.toggled) {
            context.fill(bgX, bgY - bhSizy, bgX + bgSize, bgY + bgSize, 0x66000000);
            return;
        }
        bgX += 2;
        // 半透明深色圆角矩形（用 drawFill 模拟，也可以使用 DrawContext fill）
        int fillColor = 0x8044AA44;        // 约50%透明度绿色
        int borderColor = 0xFF44FF44;      // 亮绿色
        int border = 1;
        // 先填充背景
        context.fill(bgX, bgY, bgX + bgSize, bgY + bgSize, fillColor);
        // 绘制边框 (上下左右)
        context.fill(bgX, bgY, bgX + bgSize, bgY + border, borderColor);                     // 上
        context.fill(bgX, bgY + bgSize - border, bgX + bgSize, bgY + bgSize, borderColor);  // 下
        context.fill(bgX, bgY, bgX + border, bgY + bgSize, borderColor);                     // 左
        context.fill(bgX + bgSize - border, bgY, bgX + bgSize, bgY + bgSize, borderColor);  // 右
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void renderTextDisplayName(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci){
        if(this.hovered && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_ALT)){

            if(!custom_buttonScaleFlag) return;

            EnhancedRecipeBookCategoryAPIImpl api = EnhancedRecipeBookCategoryAPIImpl.getINSTANCE();
            displayName = api.getCategoryShowName(this.getCategory());
            if(displayName == null) return;
            int textX = this.getX();
            int textY = this.getY() - 35;
            int k = this.getX() + this.getWidth() + 10;
            int l = this.getY() + this.getHeight();
            drawScrollableText(context, MinecraftClient.getInstance().textRenderer, displayName, textX, textY, k, l, 0xFFFFFF);
        }
    }


    @Override
    protected boolean modifyIsMouseOver(boolean original, double mouseX, double mouseY){
        if(!custom_buttonScaleFlag) return super.modifyIsMouseOver(original, mouseX, mouseY);
        if (!this.active || !this.visible) return false;
        int centerX = this.getX() + 17;
        int centerY = this.getY() + 13;
        int visualSize = (this.toggled || this.hovered) ? 24 : 20;
        int half = visualSize / 2;
        return mouseX >= centerX - half && mouseX <= centerX + half &&
                mouseY >= centerY - half && mouseY <= centerY + half;
    }

    @Override
    protected void modifyHovered(ClickableWidget instance, boolean value, Operation<Void> original, DrawContext context,int mouseX, int mouseY){
        if(!custom_buttonScaleFlag){
            super.modifyHovered(instance, value, original, context, mouseX, mouseY);
            return;
        }
        int centerX = this.getX() + 17;
        int centerY = this.getY() + 13;
        int visualSize = this.toggled ? 24 : 20;
        int half = visualSize / 2;
        boolean bl1 = context.scissorContains(mouseX, mouseY);
        boolean bl2 = mouseX >= centerX - half && mouseX <= centerX + half &&
                mouseY >= centerY - half && mouseY <= centerY + half;
        super.modifyHovered(instance, bl1 && bl2, original, context, mouseX, mouseY);
    }

    @ModifyVariable(method = "renderIcons", at = @At(value = "STORE", ordinal = 0))
    private int ModifyValueOfI(int origin){
        if(!custom_buttonScaleFlag) return origin;
        return 0;
    }

    @WrapMethod(method = "renderIcons")
    private void scaleIcons(DrawContext context, Operation<Void> original){
        if((this.hovered || this.toggled) && this.custom_buttonScaleFlag){
            context.getMatrices().push();
            // 以按钮中心为缩放原点（保持图标整体居中）
            float centerX = this.getX() + 17.5f; // 35/2
            float centerY = this.getY() + 13.5f; // 27/2
            context.getMatrices().translate(centerX, centerY, 0);
            context.getMatrices().scale(1.2f, 1.2f, 1.0f);
            context.getMatrices().translate(-centerX, -centerY, 0);
            original.call(context);
            context.getMatrices().pop();
            return;
        }
        original.call(context);
    }

    @Override
    public void craftGuiBELike$SetCustom_buttonScaleFlagAccess(boolean bl) {
        custom_buttonScaleFlag = bl;
    }

}
