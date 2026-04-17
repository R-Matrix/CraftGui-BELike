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

package xyz.water.rmatrix.cmod.craftguibelike.category;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record RecipeCategoryDefinition(
        Identifier id,
        Text displayName,
        Item primaryIcon,
        @Nullable Item secondaryIcon,
        int priority) {

    public static class Builder {
        private Identifier id;
        private Text displayName;
        private Item primaryIcon;
        private Item secondaryIcon = null;
        private int priority = 50;

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder displayName(Text displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder displayName(String translationKey) {
            this.displayName = Text.translatable(translationKey);
            return this;
        }

        public Builder primaryIcon(Item primaryIcon) {
            this.primaryIcon = primaryIcon;
            return this;
        }

        public Builder primaryIcon(ItemConvertible primaryIcon){
            this.primaryIcon = new ItemStack(primaryIcon).getItem();
            return this;
        }

        public Builder secondaryIcon(Item secondaryIcon) {
            this.secondaryIcon = secondaryIcon;
            return this;
        }

        public Builder secondaryIcon(ItemConvertible secondaryIcon){
            this.secondaryIcon = new ItemStack(secondaryIcon).getItem();
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public RecipeCategoryDefinition build() {
            if (id == null) throw new RuntimeException("Category ID should be set");
            if (displayName == null) displayName = Text.literal(id.getPath());
            if (primaryIcon == null) throw new RuntimeException("Primary Icon should be set");
            return new RecipeCategoryDefinition(id, displayName, primaryIcon, secondaryIcon, priority);
        }
    }
}
