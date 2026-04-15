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

package xyz.water.rmatrix.cmod.craftguibelike.datagen;

import net.minecraft.util.Identifier;
import xyz.water.rmatrix.cmod.craftguibelike.category.CategoryDetector;
import xyz.water.rmatrix.cmod.craftguibelike.category.RecipeCategoryDefinition;

import java.util.Collection;

/**
 * 用于生成自定义配方分类配置文件
 */
public final class RecipeCategoryProviderAPI{

    private static final CategoryDetector detector = CategoryDetector.getInstance();

    private RecipeCategoryProviderAPI() {}

    /**
     * 注册一个模组自定义配方分类
     * @param categoryId 配方分类 id
     * @param definition 配方分类定义 {@link RecipeCategoryDefinition}
     */
    public static void registerCategory(Identifier categoryId, RecipeCategoryDefinition definition){
        detector.registerCategory(categoryId, definition);
    }

    /**
     * 为配方指定分类（运行时动态映射）
     * @param recipeId 配方 id
     * @param categoryId 配方分类 id
     */
    public static void mapRecipeToCategory(Identifier recipeId, Identifier categoryId){
        detector.addMapping(recipeId, categoryId);
    }

    /**
     * 批量映射配方分类
     * @param recipeIds 配方 id 列表
     * @param categoryId 配方分类 id
     */
    public static void mapRecipeToCategory(Collection<Identifier> recipeIds, Identifier categoryId){
        recipeIds.forEach(identifier -> mapRecipeToCategory(identifier, categoryId));
    }

    /**
     * 使用模式匹配批量映射配方分类
     * @param pattern 模式匹配表达式
     * @param categoryId 配方分类 id
     */
    public static void mapPatternToCategory(String pattern, Identifier categoryId){
        detector.addPatternMapping(pattern, categoryId);
    }

    public static RecipeCategoryDefinition.Builder categoryBuilder$craftgui_belike(){
        return new RecipeCategoryDefinition.Builder();
    }


}
