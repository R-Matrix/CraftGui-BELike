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
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import xyz.water.rmatrix.cmod.craftguibelike.api.IRecipeSorter;

import java.util.*;
import java.util.stream.Collectors;

public class ContainGroupSorter implements IRecipeSorter {

    @Override
    public String getName() {
        return "craftgui-belike.contain_group_sort";
    }

    @Override
    public List<RecipeResultCollection> sortRecipes(List<RecipeResultCollection> recipes) {

        if(MinecraftClient.getInstance().player == null) return recipes;
        // 1. 展开所有配方，记录组信息
        List<RecipeWithGroup> allRecipes = new ArrayList<>();
        RecipeFinder finder = new RecipeFinder();
        MinecraftClient.getInstance().player.getInventory().populateRecipeFinder(finder);

        for (RecipeResultCollection collection : recipes) {
            for (RecipeDisplayEntry recipe : collection.getAllRecipes()) {
                allRecipes.add(new RecipeWithGroup(recipe, collection));
            }
        }

        // 2. 按组分类
        Map<Integer, List<RecipeWithGroup>> groups = new LinkedHashMap<>();
        List<Integer> groupOrder = new ArrayList<>();

        for (RecipeWithGroup recipeWithGroup : allRecipes) {
            int groupId = recipeWithGroup.getGroupId();

            groups.computeIfAbsent(groupId, g -> {
                groupOrder.add(g);
                return new ArrayList<>();
            }).add(recipeWithGroup);
        }

        // 3. 组内按拼音排序
        for (List<RecipeWithGroup> groupList : groups.values()) {
            groupList.sort((a, b) -> {
                String nameA = a.getRecipeName();
                String nameB = b.getRecipeName();
                return nameA.compareTo(nameB);
            });
        }

        // 4. 重新构建 RecipeResultCollection
        List<RecipeResultCollection> result = new ArrayList<>();

        for (Integer groupId : groupOrder) {
            List<RecipeWithGroup> groupList = groups.get(groupId);

            // 如果这个组原来就是折叠的（多个配方）
            if (groupList.size() > 1) {
                // 创建一个包含所有配方的集合
                List<RecipeDisplayEntry> recipesInGroup = groupList.stream()
                        .map(RecipeWithGroup::recipe)
                        .toList();

                for(RecipeDisplayEntry entry : recipesInGroup) {
                    RecipeResultCollection collection1 = new RecipeResultCollection(List.of(entry));
                    collection1.populateRecipes(finder, recipeDisplay -> true);
                    result.add(collection1);
                }

            } else {
                // 单个配方
                RecipeDisplayEntry recipe = groupList.getFirst().recipe();
                RecipeResultCollection collection1 = new RecipeResultCollection(List.of(recipe));
                collection1.populateRecipes(finder, recipeDisplay -> true);
                result.add(collection1);
            }


        }

        return result;
    }

    private String getFirstRecipeName(RecipeResultCollection collection) {
        RecipeDisplayEntry first = collection.getAllRecipes().getFirst();
        return first.display().result().getFirst(new ContextParameterMap.Builder().build(new ContextType.Builder().build())).getItem().getName().getString();
    }

    // 辅助类：记录配方和它的组信息
    private record RecipeWithGroup(RecipeDisplayEntry recipe, RecipeResultCollection originalCollection) {

        public int getGroupId() {
            // 优先使用配方的组号
            OptionalInt group = recipe.group();
            if (group.isPresent()) {
                return group.getAsInt();
            }

            // 如果没有组，用原集合的hash作为组号
            return originalCollection.hashCode();
        }

        public String getRecipeName() {
            return recipe.display().result().getFirst(new ContextParameterMap.Builder().build(new ContextType.Builder().build())).getItem().getName().getString();
        }
    }

//    RecipeFinder finder = new RecipeFinder();
//            MinecraftClient.getInstance().player.getInventory().populateRecipeFinder(finder);
//            collection1.populateRecipes(finder, recipeDisplay -> true);
//            originalOrder.add(collection1);
}
