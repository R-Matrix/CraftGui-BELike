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

package xyz.water.rmatrix.cmod.craftguibelike.mixin;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.water.rmatrix.cmod.craftguibelike.event.RecipeUnlockCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mixin(ServerRecipeBook.class)
public abstract class ServerRecipeBookMixin {

    @Shadow @Final
    protected Set<RegistryKey<Recipe<?>>> unlocked;

    @Shadow
    public abstract int lockRecipes(Collection<RecipeEntry<?>> recipes, ServerPlayerEntity player);

    @Inject(method = "unlockRecipes", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
            shift = At.Shift.AFTER))
    private void onUnlockRecipeSendMyPacket(
            Collection<RecipeEntry<?>> recipes,
            ServerPlayerEntity player,
            CallbackInfoReturnable<Integer> cir
    ){
        List<RecipeEntry<?>> failedRecipes = new ArrayList<>();
        for(var recipeEntry : recipes){
            RegistryKey<Recipe<?>> registryKey = recipeEntry.id();
            if (this.unlocked.contains(registryKey) || recipeEntry.value().isIgnoredInRecipeBook()) continue;

            ActionResult result = RecipeUnlockCallback.EVENT.invoker().interact(player, registryKey.getValue());
            if(result == ActionResult.FAIL){
                failedRecipes.add(recipeEntry);
            }
        }

        lockRecipes(failedRecipes, player);

    }
}
