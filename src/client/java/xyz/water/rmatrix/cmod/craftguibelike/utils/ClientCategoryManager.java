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

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ClientCategoryManager {
    private static ClientCategoryManager INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger("ClientCategoryManager");
    private final CategoryDetector detector = new CategoryDetector();

    public static ClientCategoryManager getInstance() {
        if(INSTANCE == null) INSTANCE = new ClientCategoryManager();
        return INSTANCE;
    }

    public void loadAllCategoryConfigs() {
        for(String modid : FabricLoader.getInstance().getAllMods()
                .stream().map(mod -> mod.getMetadata().getId()).toList()){
            Identifier configId = Identifier.of(modid, "recipe_categories.json");
            Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager()
                    .getResource(configId);
            if(resource.isPresent()){
                try(InputStream stream = resource.get().getInputStream()){
                    detector.loadConfig(modid, stream);
                    LOGGER.info("Load recipe categories from {}", modid);
                }
                catch (IOException e){
                    LOGGER.error("Failed to load config from {}", modid, e);
                }
            }
        }
    }

    /**
     * 查询配方分类
     */
    public Optional<Identifier> getCategory(Identifier recipeId) {
        return detector.getCategory(recipeId);
    }
}
