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

package xyz.water.rmatrix.cmod.craftguibelike.utils.favoriteMiscUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.search.TextSearchProvider;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Formatting;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.world.World;

import java.util.List;

public class StrictSearchProvider {

    public static SearchProvider<RecipeResultCollection> buildStrictSearchProvider(List<RecipeResultCollection> collections){
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        if(world == null) return SearchProvider.empty();

        DynamicRegistryManager dynamicRegistryManager = world.getRegistryManager();
        Registry<Item> registry = dynamicRegistryManager.getOrThrow(RegistryKeys.ITEM);
        Item.TooltipContext tooltipContext = Item.TooltipContext.create(dynamicRegistryManager);
        ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(world);
        TooltipType tooltipType = TooltipType.Default.BASIC;
        return new TextSearchProvider<>(
                resultCollection -> resultCollection.getAllRecipes().stream()
                        .flatMap(displayEntry -> displayEntry.getStacks(contextParameterMap).stream())
                        .flatMap(stack -> stack.getTooltip(tooltipContext, null, tooltipType).stream())
                        .map(text -> Formatting.strip(text.getString().trim()))
                        .filter(string -> !string.isEmpty()),

                resultCollection -> resultCollection.getAllRecipes().stream()
                        .flatMap(displayEntry -> displayEntry.getStacks(contextParameterMap).stream())
                        .map(stack -> registry.getId(stack.getItem())),
                collections
        );
    }
}
