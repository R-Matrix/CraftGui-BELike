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

package xyz.water.rmatrix.cmod.craftguibelike.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.FavoritesManagerImpl;

public class ClearFavoriteCommand implements ClientCommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(ClientCommandManager.literal("favoriteRecipe")
                .then(ClientCommandManager.literal("clear")
                    .then(ClientCommandManager.literal("confirm")
                        .executes(context -> clearFavoriteRecipe(context, false))
                        .then(ClientCommandManager.literal("all")
                            .executes(context -> clearFavoriteRecipe(context, true)))))

                .then(ClientCommandManager.literal("count")
                .executes(this::countFavorite)));
    }

    private int clearFavoriteRecipe(CommandContext<FabricClientCommandSource> context, boolean allClear){
        FabricClientCommandSource source = context.getSource();

        if(allClear){
            FavoritesManagerImpl.getINSTANCE().clearFavoritesAll();
            source.sendFeedback(Text.literal("§a已清除所有玩家的收藏配方").formatted(Formatting.GREEN));
        }
        else {
            FavoritesManagerImpl.getINSTANCE().clearFavorites(source.getPlayer().getUuid());
            source.sendFeedback(Text.literal("§a已清除当前玩家的收藏配方").formatted(Formatting.GREEN));
        }
        source.getPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
        return 1;
    }

    private int countFavorite(CommandContext<FabricClientCommandSource> context){
        FabricClientCommandSource source = context.getSource();
        int s = FavoritesManagerImpl.getINSTANCE().getFavorites(source.getPlayer().getUuid()).size();

        source.sendFeedback(Text.literal("§7你收藏了 §f" + s + " §7个配方").formatted(Formatting.GRAY));

        return 1;
    }
}
