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

package xyz.water.rmatrix.cmod.craftguibelike.utils;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.water.rmatrix.cmod.craftguibelike.CraftGuiBELike;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.EnhancedRecipeBookCategoryAPIImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ClientCategoryManager implements IdentifiableResourceReloadListener {
    private static ClientCategoryManager INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger("CraftGui-BELike$ClientCategoryManager");
    private final Identifier LISTENER_ID = Identifier.of(CraftGuiBELike.MOD_ID, "category_loader");
    private final CategoryDetector detector = CategoryDetector.getInstance();

    public static ClientCategoryManager getInstance() {
        if(INSTANCE == null) INSTANCE = new ClientCategoryManager();
        return INSTANCE;
    }

    public void reloadAllCategoryConfigs(ResourceManager manager) {
        detector.clear();
        for(String modid : FabricLoader.getInstance().getAllMods()
                .stream().map(mod -> mod.getMetadata().getId()).toList()){
            Identifier configId = Identifier.of(modid, "recipe_categories/recipe_categories.json");
            Optional<Resource> resource = manager.getResource(configId);
            if(resource.isPresent()){
                try(InputStream stream = resource.get().getInputStream()){
                    detector.loadConfig(modid, stream, false);
                    LOGGER.info("Load recipe categories from {}", modid);
                }
                catch (IOException e){
                    LOGGER.error("Failed to load config from {}", modid, e);
                }
            }
        }
    }


    @Override
    public Identifier getFabricId() {
        return LISTENER_ID;
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.runAsync(() -> {
            reloadAllCategoryConfigs(manager);
            var api = EnhancedRecipeBookCategoryAPIImpl.getINSTANCE();
            api.refreshStorgeMap();
        }, applyExecutor).thenCompose(synchronizer::whenPrepared);
    }

    /**
     * 用于初始化加载平日放分类.
     */
    public void loadAndRegisterAllCategories(){
        for(var mod : FabricLoader.getInstance().getAllMods()){
            String modId = mod.getMetadata().getId();
            Optional<Path> configPath = mod.findPath("assets/" + modId + "/recipe_categories/recipe_categories.json");
            if(configPath.isPresent()){
                try (InputStream stream = Files.newInputStream(configPath.get())){
                    detector.loadConfig(modId, stream, true);
                }
                catch (Exception e){
                    LOGGER.error("Failed to load categories from mod: {}", modId, e);
                }
            }
        }
    }
}
