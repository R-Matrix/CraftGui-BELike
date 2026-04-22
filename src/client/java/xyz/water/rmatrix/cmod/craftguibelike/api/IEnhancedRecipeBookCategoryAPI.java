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

package xyz.water.rmatrix.cmod.craftguibelike.api;

import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IEnhancedRecipeBookCategoryAPI {

    default RecipeBookCategory registerCategory(String modId, String categoryName, Item icon){
        return registerCategory(modId, categoryName, icon, null);
    }

    default RecipeBookCategory registerCategory(String modId, String categoryName, Item primaryIcon, @Nullable Item secondaryIcon){
        return registerCategory(Identifier.of(modId, categoryName), primaryIcon, secondaryIcon);
    };

    default RecipeBookCategory registerCategory(Identifier categoryId, Item icon){
        return registerCategory(categoryId, icon, null);
    }

    RecipeBookCategory registerCategory(Identifier categoryId, Item primaryIcon, @Nullable Item secondaryIcon);

    void addRecipeToCategory(RecipeBookCategory category, Identifier recipeId);

    void removeRecipeFromCategory(RecipeBookCategory category, Identifier recipeId);

    Set<Identifier> getRecipesUnderCategory(RecipeBookCategory category);

    boolean isRegisteredCategory(RecipeBookGroup category);

    RecipeBookCategory getCategoryFromId(Identifier categoryId);
}
