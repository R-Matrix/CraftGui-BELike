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

package xyz.water.rmatrix.cmod.craftguibelike.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

/**
 * 配方解锁事件 <p>
 * 玩家解锁新配方时调用 <p>
 * 会返回:
 * <li> Success 退出后续处理过程, 然后继续正常的配方解锁行为 </li>
 * <li> Pass 回落到后续处理过程, 如果没有其他监听器了, 默认为 Success </li>
 * <li> Fail 推出后续处理过程, 配方不会被解锁 </li>
 */
public interface RecipeUnlockCallback {

    Event<RecipeUnlockCallback> EVENT = EventFactory.createArrayBacked(
        RecipeUnlockCallback.class,
        (listeners) -> (player, recipeId) -> {
            for(RecipeUnlockCallback listener : listeners){
                ActionResult result = listener.interact(player, recipeId);

                if(result != ActionResult.PASS) return result;
            }

            return ActionResult.PASS;
        }
    );

    /**
     *
     * @param player 玩家
     * @param recipeId 配方 id
     * @return ActionResult
     */
    ActionResult interact(ServerPlayerEntity player, Identifier recipeId);
}
