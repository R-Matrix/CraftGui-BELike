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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import xyz.water.rmatrix.cmod.craftguibelike.category.RecipeCategoryDefinition;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RecipeCategoryProvider implements DataProvider {
    private final FabricDataOutput output;
    private final String modid;

    private final Map<Identifier, RecipeCategoryDefinition> categories = new HashMap<>();
    private final Map<Identifier, List<String>> categoryToRecipes = new HashMap<>();

    public RecipeCategoryProvider(FabricDataOutput output, String modid){
        this.output = output;
        this.modid = modid;
    }

    /**
     * 注册配方分类
     */
    public void registerCategory(Identifier categoryId, RecipeCategoryDefinition definition){
        if(categories.containsKey(categoryId)) throw new RuntimeException("已有重复配方分类类型");
        categories.put(categoryId, definition);
    }


    /**
     * 记录单个配方的分类
     */
    public void mapRecipe(Identifier recipeId, Identifier categoryId) {
        categoryToRecipes
                .computeIfAbsent(categoryId, k -> new ArrayList<>())
                .add(recipeId.toString());
    }

    /**
     * 批量映射配方
     */
    public void mapRecipes(Collection<Identifier> recipeIds, Identifier categoryId) {
        recipeIds.forEach(id -> mapRecipe(id, categoryId));
    }

    /**
     * 使用模式匹配批量映射（支持通配符）
     */
    public void mapPattern(String pattern, Identifier categoryId) {
        categoryToRecipes
                .computeIfAbsent(categoryId, k -> new ArrayList<>())
                .add(pattern);
    }


    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        JsonObject root = new JsonObject();
        JsonObject categoriesJson = new JsonObject();

        for(var entry : categories.entrySet()){
            Identifier categoryId = entry.getKey();
            RecipeCategoryDefinition definition = entry.getValue();

            JsonObject categoryJson = new JsonObject();
            categoryJson.addProperty("id", definition.id().toString());
            categoryJson.addProperty("displayName", extractTranslationKey(definition.displayName()));
            categoryJson.addProperty("primaryIcon", definition.primaryIcon().getTranslationKey());
            if(definition.secondaryIcon() != null) categoriesJson.addProperty("secondaryIcon", definition.secondaryIcon().getTranslationKey());
            categoryJson.addProperty("priority", definition.priority());

            categoriesJson.add(categoryId.getPath(), categoryJson);

            if(categoryToRecipes.containsKey(categoryId)){
                JsonArray recipeArray = new JsonArray();
                for(String pattern : categoryToRecipes.get(categoryId)){
                    recipeArray.add(pattern);
                }
                categoryJson.add("recipes", recipeArray);
            }
        }

        root.add("categories", categoriesJson);

        Path path = output.getPath().resolve("data/" + modid + "/recipe_categories.json");

        return DataProvider.writeToPath(writer, root, path);
    }

    private String extractTranslationKey(Text text){
        if(text.getContent() instanceof TranslatableTextContent content){
            return content.getKey();
        }
        return text.getString();
    }

    @Override
    public String getName() {
        return "recipe_category_provider";
    }
}
