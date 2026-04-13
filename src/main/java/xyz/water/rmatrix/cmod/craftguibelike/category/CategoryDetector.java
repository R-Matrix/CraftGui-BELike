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

package xyz.water.rmatrix.cmod.craftguibelike.category;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;


public class CategoryDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger("CategoryDetector");

    // 配方 -> 分类
    private final Map<Identifier, Identifier> categoryMapping = new HashMap<>();

    private final Map<Identifier, RecipeCategoryDefinition> registeredCategory = new HashMap<>();

    private final List<PatternRule> patternRules = new ArrayList<>();

    public void loadConfig(String modId, InputStream configStream){
        try {
            Gson gson = new Gson();
            JsonObject config = gson.fromJson(new InputStreamReader(configStream), JsonObject.class);

            JsonObject categories = config.getAsJsonObject("categories");
            if (categories == null) return;

            for (String categoryIdStr : categories.keySet()) {
                JsonObject categoryData = categories.getAsJsonObject(categoryIdStr);
                Identifier categoryId = Identifier.of(modId, categoryIdStr);
                registerCategoryFromConfig(categoryId, categoryData);

                if (categoryData.has("recipes")) {
                    for (var recipeElem : categoryData.getAsJsonArray("recipes")) {
                        String recipePattern = recipeElem.getAsString();
                        if (recipePattern.contains("*")) {
                            String regex = recipePattern.replace("*", ".*").replace("?", ".");
                            patternRules.add(new PatternRule(Pattern.compile("^" + regex + "&"), categoryId));
                        } else {
                            Identifier recipeId = Identifier.of(recipePattern);
                            categoryMapping.put(recipeId, categoryId);
                        }
                    }
                }
            }

            LOGGER.info("Load {} category mappings from mod : {}", categoryMapping.size(), modId);
        }
        catch (Exception e){
            LOGGER.error("Failed to load category config for mod : {}", modId, e);
        }

    }

    public Map<Identifier, Identifier> detectAll(RecipeManager recipeManager){
        Map<Identifier, Identifier> results = new HashMap<>();

        for(var entry : recipeManager.getStonecutterRecipes().entries()){

            Optional<RecipeEntry<StonecuttingRecipe>> recipeEntryOptional = entry.recipe().recipe();
            if(recipeEntryOptional.isEmpty()) continue;
            Identifier recipeId = recipeEntryOptional.get().id().getValue();
            Recipe<?> recipe = recipeEntryOptional.get().value();

            detectCategory(recipeId, recipe).ifPresent(categoryId -> results.put(recipeId, categoryId));
        }

        LOGGER.info("Detected {} categorized recipes", results.size());
        return results;
    }

    public void registerCategoryFromConfig(Identifier id, JsonObject data){
        RecipeCategoryDefinition.Builder builder = new RecipeCategoryDefinition.Builder().id(id);

        if(data.has("display_name")) builder.displayName(data.get("display_name").getAsString());
        if(data.has("primary_icon")) builder.primaryIcon(Registries.ITEM.get(Identifier.of(data.getAsString())));
        if(data.has("primary_icon")) builder.primaryIcon(Registries.ITEM.get(Identifier.of(data.getAsString())));
        if(data.has("priority"    )) builder.priority   (data.get("priority").getAsInt());

        //todo : icons and priority

        registeredCategory.put(id, builder.build());
    }

    /**
     * 检测单个物品分类
     *
     * @param recipeId 配方 id
     * @param recipe 配方
     * @return 分类 id
     */
    private Optional<Identifier> detectCategory(Identifier recipeId, Recipe<?> recipe){
        if(categoryMapping.containsKey(recipeId)){
            return Optional.of(categoryMapping.get(recipeId));
        }

        String recipeIdStr = recipeId.toString();
        for (PatternRule rule : patternRules){
            if(rule.pattern.matcher(recipeIdStr).matches()){
                return Optional.of(rule.categoryId);
            }
        }

        return Optional.empty();
    }

    private record PatternRule(Pattern pattern, Identifier categoryId) {}

}
