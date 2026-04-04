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

import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.registry.Registries;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import xyz.water.rmatrix.cmod.craftguibelike.api.IRecipeSorter;

import java.util.List;
import java.util.UUID;

public class PinYinSorter implements IRecipeSorter {

    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();
    static {
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }


    @Override
    public String getName() {
        return "craftgui-belike.pinyin_sort";
    }

    @Override
    public List<RecipeResultCollection> sortRecipes(List<RecipeResultCollection> recipes) {
        return recipes.stream()
                .sorted((a, b) -> {
                    String pinyinA;
                    String pinyinB;
                    try {
                        pinyinA = getPinyin(a);
                        pinyinB = getPinyin(b);
                        return pinyinA.compareTo(pinyinB);
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        return 0;
                    }

                })
                .toList();
    }

    private String getPinyin(RecipeResultCollection collection) throws BadHanyuPinyinOutputFormatCombination {
        for (RecipeDisplayEntry recipe : collection.getAllRecipes()) {
            ItemStack output = recipe.display().result().getFirst(new ContextParameterMap.Builder().build(new ContextType.Builder().build()));
            if (!output.isEmpty()) {
                String localizedName = output.getName().getString();
                return toPinyin(localizedName);
            }
        }
        return "";
    }

    private String toPinyin(String input) throws BadHanyuPinyinOutputFormatCombination {
        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
            if (pinyins != null && pinyins.length > 0) {
                result.append(pinyins[0]);
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
