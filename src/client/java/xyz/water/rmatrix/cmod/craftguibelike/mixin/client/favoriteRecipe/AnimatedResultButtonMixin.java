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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.FavoritesManagerImpl;
import xyz.water.rmatrix.cmod.craftguibelike.item.ModItem;

@Mixin(AnimatedResultButton.class)
public abstract class AnimatedResultButtonMixin extends ClickableWidget {

    public AnimatedResultButtonMixin(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
        throw new RuntimeException();
    }

    @Shadow
    public abstract NetworkRecipeId getCurrentId();

    @Shadow
    public abstract int getWidth();

    @ModifyReturnValue(method = "isValidClickButton", at = @At("RETURN"))
    private boolean ModifyValidaButton(boolean original, int button){
        return original || button == 2;
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void addStarToFavoriteRecipe(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;

        NetworkRecipeId recipeId = this.getCurrentId();
        if(recipeId == null) return;

        if(FavoritesManagerImpl.getINSTANCE().isFavorited(player.getUuid(), recipeId)){
            int starX = this.getWidth()  + this.getX() - 10;
            int starY = this.getY();
            context.getMatrices().push();
            context.getMatrices().translate(starX, starY, 0);
            context.getMatrices().scale(0.5f, 0.5f, 1.0f);
            context.drawItem(ModItem.FAVORITE_STAR.getDefaultStack(), 0, 0);
            context.getMatrices().pop();
        }
    }


}
