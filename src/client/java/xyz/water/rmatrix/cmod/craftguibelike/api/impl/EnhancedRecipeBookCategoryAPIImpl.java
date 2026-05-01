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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.water.rmatrix.cmod.craftguibelike.api.IEnhancedRecipeBookCategoryAPI;
import xyz.water.rmatrix.cmod.craftguibelike.api.IRecipeIdToDisplayEntryAdapt;
import xyz.water.rmatrix.cmod.craftguibelike.api.IRecipeManager;
import xyz.water.rmatrix.cmod.craftguibelike.mixin.client.favoriteRecipe.ClientRecipeBookAccess;
import xyz.water.rmatrix.cmod.craftguibelike.registry.RecipeBookTabRegistry;
import xyz.water.rmatrix.cmod.craftguibelike.utils.CategoryDetector;
import xyz.water.rmatrix.cmod.craftguibelike.utils.favoriteMiscUtils.ClientRecipeBookHelper;

import java.util.*;

public class EnhancedRecipeBookCategoryAPIImpl implements IEnhancedRecipeBookCategoryAPI, IRecipeManager, IRecipeIdToDisplayEntryAdapt {

    private static EnhancedRecipeBookCategoryAPIImpl INSTANCE;

    private final Map<RecipeBookCategory, Set<Identifier>> categoryRecipes = new LinkedHashMap<>(); // 配方分类 -> 配方 id 集合

    private final Map<Identifier, RecipeBookCategory> idToCategoryMap = new HashMap<>(); // 配方分类 id -> 配方分类

    private final Map<RecipeBookCategory, Text> categoryShowNameMap = new HashMap<>(); // 配方id -> 配方名称展示

    private final Map<RecipeBookCategory, Set<RecipeDisplayEntry>> categoryDisplayEntries = new HashMap<>();


    private EnhancedRecipeBookCategoryAPIImpl(){}

    public static EnhancedRecipeBookCategoryAPIImpl getINSTANCE(){
        if(INSTANCE == null) INSTANCE = new EnhancedRecipeBookCategoryAPIImpl();
        return INSTANCE;
    }

    public RecipeBookCategory registerCategory(Identifier identifier, Item primaryIcon, @Nullable Item secondaryIcon){
        RecipeBookCategory existCategory = Registries.RECIPE_BOOK_CATEGORY.get(identifier);

        if(existCategory != null){
            categoryRecipes.putIfAbsent(existCategory, new HashSet<>());
            return existCategory;
        }

        RecipeBookCategory newCategory = Registry.register(Registries.RECIPE_BOOK_CATEGORY, identifier, new RecipeBookCategory());
        categoryRecipes.put(newCategory, new HashSet<>());
        idToCategoryMap.put(identifier, newCategory);
        categoryDisplayEntries.put(newCategory, new HashSet<>());

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

    public boolean isRegisteredCategory(Identifier categoryId){
        return idToCategoryMap.containsKey(categoryId);
    }

    public RecipeBookCategory getCategoryFromCategoryId(Identifier categoryId){
        return idToCategoryMap.get(categoryId);
    }

    @Override
    public RecipeBookCategory getCategoryFromRecipeId(Identifier recipeId) {
        for(var category : categoryRecipes.entrySet()){
            if(category.getValue().contains(recipeId)){
                return category.getKey();
            }
        }

        Optional<Identifier> catId = CategoryDetector.getInstance().getCategory(recipeId);
        if(catId.isPresent()){
            RecipeBookCategory category = idToCategoryMap.get(catId.get());
            if(category != null){
                categoryRecipes.computeIfAbsent(category, k -> new HashSet<>()).add(recipeId);
                return category;
            }
        }

        return null;
    }


    @Override
    public Text getCategoryShowName(RecipeBookGroup category) {
        if(isRegisteredCategory(category)){
            return categoryShowNameMap.get((RecipeBookCategory) category);
        }
        throw new RuntimeException("Recipe Category Not Found!");
    }

    @Override
    public void addTextToCategory(Identifier categoryId, Text displayName){

        if(!idToCategoryMap.containsKey(categoryId)) throw new RuntimeException("Recipe Category Not Found!");

        categoryShowNameMap.put(idToCategoryMap.get(categoryId), displayName);
    }

    @Override
    public void refreshStorgeMap() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        for (var category : categoryRecipes.keySet()){
            categoryRecipes.get(category).clear();
        }

        List<RecipeResultCollection> recipeResultCollections = ClientRecipeBookHelper.getAllUnmergedRecipes(player);

        for(RecipeResultCollection collection : recipeResultCollections){
            for(var entry : collection.getAllRecipes()) {
                ItemStack output = null;
                if (MinecraftClient.getInstance().world != null) {
                    output = entry.display().result().getFirst(
                            SlotDisplayContexts.createParameters(MinecraftClient.getInstance().world));
                }
                if (output == null) continue;
                Identifier id = Registries.ITEM.getId(output.getItem());
                var category = getCategoryFromRecipeId(id);
                if (category == null) continue;
                categoryRecipes.computeIfAbsent(category, k -> new HashSet<>()).add(id);
            }
        }
        refreshClientDisplayEntries();

        player.getRecipeBook().refresh();
    }

    @Override
    public void refreshClientDisplayEntries() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        for (var category : categoryDisplayEntries.keySet()){
            categoryDisplayEntries.get(category).clear();
        }

        List<RecipeBookCategory> crafting = RecipeBookType.valueOf("CRAFTING").getCategories();
        Collection<RecipeDisplayEntry> allEntries = ((ClientRecipeBookAccess)(player.getRecipeBook())).craftGui_BELike$getAllRecipes().values().stream()
                .filter(entry -> crafting.contains(entry.category()))
                .toList();

        for(var entry : allEntries){
            Optional<Identifier> optionalIdentifier = exactItemId(entry);
            if(optionalIdentifier.isEmpty()) continue;
            Identifier id = optionalIdentifier.get();
            RecipeBookCategory category = getCategoryFromRecipeId(id);
            if(category == null) continue;
            categoryDisplayEntries.computeIfAbsent(category, k -> new HashSet<>()).add(entry);
        }
    }


    @Override
    public Set<RecipeDisplayEntry> getEntriesUnderCategory(RecipeBookCategory category) {
        Set<RecipeDisplayEntry> recipes = categoryDisplayEntries.get(category);
        return recipes != null ? Collections.unmodifiableSet(recipes) :
                Collections.emptySet();
    }


    @Override
    public boolean isEntirelyCustom(RecipeResultCollection collection) {
        return collection.getAllRecipes().stream().allMatch(
                entry -> {
                    if(exactItemId(entry).isEmpty()) return true;
                    Identifier identifier = exactItemId(entry).get();
                    return isRegisteredCategory(getCategoryFromRecipeId(identifier));
                }
        );
    }

    private Optional<Identifier> exactItemId(RecipeDisplayEntry entry){
        ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null) return Optional.empty();
        List<ItemStack> stacks = entry.display().result().getStacks(SlotDisplayContexts.createParameters(world));
        if(stacks.isEmpty()) return Optional.empty();
        return Optional.of(Registries.ITEM.getId(stacks.getFirst().getItem()));
    }
}
