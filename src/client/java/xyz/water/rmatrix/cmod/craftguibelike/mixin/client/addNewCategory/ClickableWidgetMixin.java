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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin{

    @Shadow public boolean active = true;
    @Shadow public boolean visible = true;
    @Shadow
    protected boolean hovered;

    @Shadow
    protected static void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int startX, int startY, int endX, int endY, int color) {
    }

    @Shadow
    public abstract int getY();

    @Shadow
    public abstract int getX();

    @Shadow
    public abstract int getWidth();

    @Shadow
    public abstract int getHeight();

    @ModifyReturnValue(method = "isMouseOver", at = @At("RETURN"))
    protected boolean modifyIsMouseOver(boolean original, double mouseX, double mouseY){
        return original;
    }

    @WrapOperation(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/ClickableWidget;hovered:Z", opcode = Opcodes.PUTFIELD))
    protected void modifyHovered(ClickableWidget instance, boolean value, Operation<Void> original, DrawContext context, int mouseX, int mouseY){
        original.call(instance, value);
    }

}
