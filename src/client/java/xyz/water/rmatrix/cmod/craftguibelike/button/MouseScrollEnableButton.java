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

package xyz.water.rmatrix.cmod.craftguibelike.button;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MouseScrollEnableButton extends AbstractMyRecipeBookResultButton {

    private static boolean enableMouseScroll = true;


    public MouseScrollEnableButton(int x, int y) {
        super(x, y, 10, 10,
                Text.literal("s"),
                button -> {
                    enableMouseScroll = !enableMouseScroll;
                    button.setTooltip(Tooltip.of(getTooltipText()));
                },
                ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.setTooltip(Tooltip.of(getTooltipText()));
    }


    public static boolean isEnableMouseScroll() {
        return enableMouseScroll;
    }

    private static Text getTooltipText() {
        return Text.translatable("craftgui-belike.button.mouse_scroll.current").append(
                Text.literal(enableMouseScroll ? " §a✓" : " §c✗")
        );
    }
}
