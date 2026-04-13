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

package xyz.water.rmatrix.cmod.craftguibelike.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import xyz.water.rmatrix.cmod.craftguibelike.CraftGuiBELike;

import java.util.HashMap;
import java.util.Map;

/**
* 配方id -> 分类id
**/
public record RecipeCategoryS2CPayLoad(Map<Identifier, Identifier> categoryMapping) implements CustomPayload {
    public static final Identifier CATEGORY_PACKET_ID = Identifier.of(CraftGuiBELike.MOD_ID, "category_sync");
    public static final Id<RecipeCategoryS2CPayLoad> ID = new Id<>(CATEGORY_PACKET_ID);
    public static final PacketCodec<PacketByteBuf, RecipeCategoryS2CPayLoad> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeVarInt(value.categoryMapping().size());
                value.categoryMapping.forEach((recipeId, categoryId) ->{
                    buf.writeIdentifier(recipeId);
                    buf.writeIdentifier(categoryId);
                });
            },
            buf -> {
                int size = buf.readVarInt();
                Map<Identifier, Identifier> map = new HashMap<>();
                for(int i = 0; i < size; i++){
                    Identifier recipeId = buf.readIdentifier();
                    Identifier categoryId = buf.readIdentifier();
                    map.put(recipeId, categoryId);
                }
                return new RecipeCategoryS2CPayLoad(map);
            }
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
