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

package xyz.water.rmatrix.cmod.craftguibelike.sorters;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import xyz.water.rmatrix.cmod.craftguibelike.api.IRecipeSorter;

import java.util.List;

public class RegistryOrderSorter implements IRecipeSorter {

    @Override
    public String getName() {
        return "craftgui-belike.sort.register_order_sort";
    }

    @Override
    public List<RecipeResultCollection> sortRecipes(List<RecipeResultCollection> recipes) {
        Registry<Item> registry = Registries.ITEM;
        return recipes.stream().sorted(
                (a, b) -> {
                    int idA = getRegistryItemId(a, registry);
                    int idB = getRegistryItemId(b, registry);
                    return Integer.compare(idA, idB);
                }
        ).toList();
    }

    private int getRegistryItemId(RecipeResultCollection collection, Registry<Item> registry){
        for (RecipeDisplayEntry recipe : collection.getAllRecipes()) {
            ItemStack output = null;
            if (MinecraftClient.getInstance().world != null) {
                output = recipe.display().result().getFirst(SlotDisplayContexts.createParameters(MinecraftClient.getInstance().world));
            }
            if (output != null && !output.isEmpty()) {
                return registry.getRawId(output.getItem());
            }
        }
        return Integer.MAX_VALUE;
    }
}
