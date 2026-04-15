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

package xyz.water.rmatrix.cmod.craftguibelike.manager;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.water.rmatrix.cmod.craftguibelike.category.CategoryDetector;
import xyz.water.rmatrix.cmod.craftguibelike.mixin.ServerRecipeBookAccess;
import xyz.water.rmatrix.cmod.craftguibelike.network.RecipeCategoryS2CPayLoad;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ServerCategoryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServerCategoryManager");
    private static ServerCategoryManager INSTANCE;

    private final CategoryDetector detector = new CategoryDetector();
    private Map<Identifier, Identifier> cachedMapping = new HashMap<>(); // 配方 id -> 分类 id
    private boolean initialized = false;

    public static ServerCategoryManager getInstance(){
        if(INSTANCE == null) INSTANCE = new ServerCategoryManager();
        return INSTANCE;
    }

    public void initialize(MinecraftServer server){
        if(initialized){
            LOGGER.info("ServerCraftManager has already initialized");
            return;
        }

        RecipeManager recipeManager = server.getRecipeManager();

        loadAllConfigs(server);

        cachedMapping = detector.detectAll(recipeManager);

        initialized = true;
        LOGGER.info("ServerCategoryManager initialized with {} mappings", cachedMapping.size());
    }

    /**
     * 在玩家解锁配方时发送分类数据
     */
    public void onRecipeUnlocked(ServerPlayerEntity player, Identifier recipeId){
        if(!ServerPlayNetworking.canSend(player, RecipeCategoryS2CPayLoad.ID)) return;

        Identifier categoryId = getCategoryForRecipe(recipeId);

        if(categoryId == null) return;

        Map<Identifier, Identifier> update = Map.of(recipeId, categoryId);
        RecipeCategoryS2CPayLoad packet = new RecipeCategoryS2CPayLoad(update);
        ServerPlayNetworking.send(player, packet);
        LOGGER.debug("Send incremental category update for {},  to {}", recipeId, player.getName());
    }

    public void onPlayerLoginSendUnlockedCategory(ServerPlayerEntity player){
        ServerRecipeBook recipeBook = player.getRecipeBook();
        Collection<RegistryKey<Recipe<?>>> unlocked = ((ServerRecipeBookAccess)recipeBook).craftGui_BELike$getUnlockedRecipes();

        Map<Identifier, Identifier> unlockedMappings = new HashMap<>();

        for(var key : unlocked){
            Identifier recipeId = key.getValue();
            Optional<Identifier> categoryId = detector.detectCategory(recipeId);

            categoryId.ifPresent(identifier -> unlockedMappings.put(recipeId, identifier));

            ServerPlayNetworking.send(player, new RecipeCategoryS2CPayLoad(unlockedMappings));

        }
    }




    private void loadAllConfigs(MinecraftServer server) {
        //todo
    }

    public Identifier getCategoryForRecipe(Identifier recipeId){
        return cachedMapping.get(recipeId);
    }

}
