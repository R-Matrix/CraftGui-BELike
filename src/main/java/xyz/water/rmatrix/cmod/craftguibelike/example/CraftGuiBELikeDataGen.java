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

package xyz.water.rmatrix.cmod.craftguibelike.example;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.TestOnly;
import xyz.water.rmatrix.cmod.craftguibelike.CraftGuiBELike;
import xyz.water.rmatrix.cmod.craftguibelike.category.RecipeCategoryDefinition;
import xyz.water.rmatrix.cmod.craftguibelike.datagen.RecipeCategoryProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 一个使用本模组的配置示例
 */
@TestOnly
public class CraftGuiBELikeDataGen implements DataGeneratorEntrypoint {


    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        Identifier EXAMPLE_CATEGORY_ID = Identifier.of(CraftGuiBELike.MOD_ID, "example_category");

        final RecipeCategoryDefinition EXAMPLE_CATEGORY = new RecipeCategoryDefinition.Builder()
                .id(EXAMPLE_CATEGORY_ID)
                .displayName("craftgui-belike.category.magic")
                .primaryIcon(Items.ENCHANTED_BOOK)
                .build();

        Collection<Identifier> recipes = List.of(
                Identifier.of("minecraft:cobblestone_slab"),
                Identifier.of("minecraft:brick_slab")
                // you can add more ...
        );


        pack.addProvider(MyRecipeGenerator::new);
        pack.addProvider(((output, registriesFuture) -> {
            RecipeCategoryProvider categoryProvider = new RecipeCategoryProvider(output, CraftGuiBELike.MOD_ID);
            categoryProvider.registerCategory(EXAMPLE_CATEGORY_ID, EXAMPLE_CATEGORY);
            categoryProvider.mapPattern("*bricks", EXAMPLE_CATEGORY_ID);
            categoryProvider.mapRecipe(Identifier.of("minecraft:gold_block"), EXAMPLE_CATEGORY_ID);
            categoryProvider.mapRecipes(recipes, EXAMPLE_CATEGORY_ID);
            return categoryProvider;
        }));

    }

    private static class MyRecipeGenerator extends FabricRecipeProvider {

        public MyRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }
        @Override
        protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
            return new RecipeGenerator(registryLookup, exporter) {
                @Override
                public void generate() {
                    /*
                      在这里, 你可以编写你的自定义配方
                     */
                }
            };
        }
        @Override
        public String getName() {
            return "recipes";
        }
    }
}
