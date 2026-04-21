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

package xyz.water.rmatrix.cmod.craftguibelike.api.impl;

import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.water.rmatrix.cmod.craftguibelike.api.IEnhancedRecipeBookCategoryAPI;
import xyz.water.rmatrix.cmod.craftguibelike.registry.RecipeBookTabRegistry;

import java.util.*;

public class EnhancedRecipeBookCategoryAPIImpl implements IEnhancedRecipeBookCategoryAPI {

    private static EnhancedRecipeBookCategoryAPIImpl INSTANCE;

    private final Map<RecipeBookCategory, Set<Identifier>> categoryRecipes = new LinkedHashMap<>();


    private EnhancedRecipeBookCategoryAPIImpl(){}

    public static EnhancedRecipeBookCategoryAPIImpl getINSTANCE(){
        if(INSTANCE == null) INSTANCE = new EnhancedRecipeBookCategoryAPIImpl();
        return INSTANCE;
    }

    @Override
    public RecipeBookCategory registerCategory(String modId, String categoryId, Item icon) {
        return registerCategory(modId, categoryId, icon, null);
    }

    @Override
    public RecipeBookCategory registerCategory(String modId, String categoryId, Item primaryIcon, @Nullable Item secondaryIcon) {
        Identifier idf = Identifier.of(modId, categoryId);
        return registerCategory(idf,primaryIcon, secondaryIcon);
    }

    public RecipeBookCategory registerCategory(Identifier identifier, Item icon){
        return registerCategory(identifier, icon, null);
    }

    public RecipeBookCategory registerCategory(Identifier identifier, Item primaryIcon, @Nullable Item secondaryIcon){
        RecipeBookCategory existCategory = Registries.RECIPE_BOOK_CATEGORY.get(identifier);

        if(existCategory != null){
            categoryRecipes.putIfAbsent(existCategory, new HashSet<>());
            return existCategory;
        }

        RecipeBookCategory newCategory = Registry.register(Registries.RECIPE_BOOK_CATEGORY, identifier, new RecipeBookCategory());
        categoryRecipes.put(newCategory, new HashSet<>());

        RecipeBookTabRegistry.register(newCategory, primaryIcon, secondaryIcon);

        return newCategory;
    }

    @Override
    public void addRecipeToCategory(RecipeBookCategory category, Identifier recipeId) {
        Set<Identifier> recipes = categoryRecipes.get(category);
        if(recipes != null) recipes.add(recipeId);
    }

    @Override
    public void removeRecipeFromCategory(RecipeBookCategory category, Identifier recipeId) {
        Set<Identifier> recipes = categoryRecipes.get(category);
        if(recipes != null) recipes.remove(recipeId);
    }

    public Set<Identifier> getRecipesUnderCategory(RecipeBookCategory category){
        Set<Identifier> recipes = categoryRecipes.get(category);
        return recipes != null ? Collections.unmodifiableSet(recipes) :
                Collections.emptySet();
    }

    public boolean isRegisteredCategory(RecipeBookGroup category){
        if(category instanceof RecipeBookCategory category1) return categoryRecipes.containsKey(category1);
        return false;
    }
}
