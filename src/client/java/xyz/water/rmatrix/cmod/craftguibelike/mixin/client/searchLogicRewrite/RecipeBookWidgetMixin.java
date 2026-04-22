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

package xyz.water.rmatrix.cmod.craftguibelike.mixin.client.searchLogicRewrite;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.EnhancedRecipeBookCategoryAPIImpl;
import xyz.water.rmatrix.cmod.craftguibelike.button.StrictSearchButton;
import xyz.water.rmatrix.cmod.craftguibelike.utils.favoriteMiscUtils.ClientRecipeBookHelper;
import xyz.water.rmatrix.cmod.craftguibelike.utils.favoriteMiscUtils.StrictSearchProvider;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {

    @Shadow
    private RecipeGroupButtonWidget currentTab;


    //todo : fix me : 自定义分类不能使用搜索

    @Unique
    private SearchProvider<RecipeResultCollection> strictSearchProvider;


    @Shadow
    private @Nullable TextFieldWidget searchField;

    @Shadow
    private ClientRecipeBook recipeBook;

    @Inject(method = "initialize", at = @At("TAIL"))
    private void initStrictSearchProvider(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow, CallbackInfo ci){
        strictSearchProvider = StrictSearchProvider.buildStrictSearchProvider(ClientRecipeBookHelper.getAllUnmergedRecipes(recipeBook));
    }


    @WrapOperation(method = "refreshResults", at = @At(value = "INVOKE",
            target = "Ljava/util/List;removeIf(Ljava/util/function/Predicate;)Z", ordinal = 1))
    private boolean redirectRemoveIf(List<RecipeResultCollection> instance,
                                     Predicate<RecipeResultCollection> predicate,
                                     Operation<Boolean> original){

        RecipeGroupButtonWidget currentTab1 = this.currentTab;

        // todo : flag 启用严格搜索模式
        if (this.searchField == null) return false;

        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return false;

        RecipeFinder finder = new RecipeFinder();
        player.getInventory().populateRecipeFinder(finder);

        String string = this.searchField.getText();

        boolean bl = EnhancedRecipeBookCategoryAPIImpl.getINSTANCE().isRegisteredCategory(currentTab1.getCategory());
        if(StrictSearchButton.isStrictSearchMode() || bl){

            Set<NetworkRecipeId> matchesId = strictSearchProvider
                    .findAll(string.toLowerCase(Locale.ROOT)).stream()
                    .flatMap(collection -> collection.getAllRecipes().stream())
                    .map(RecipeDisplayEntry::id)
                    .collect(Collectors.toSet());

            var iterator = instance.listIterator();
            while (iterator.hasNext()){
                RecipeResultCollection collection = iterator.next();

                List<RecipeDisplayEntry> matchesEntry = collection.getAllRecipes().stream()
                        .filter(entry -> matchesId.contains(entry.id())).toList();

                if(matchesEntry.isEmpty()) iterator.remove();
                else if(matchesEntry.size() != collection.getAllRecipes().size()){
                    RecipeResultCollection collection1 = new RecipeResultCollection(matchesEntry);
                    collection1.populateRecipes(finder, i -> true);
                    iterator.remove();
                    iterator.add(collection1);
                }
            }

            return true;

        }
        return original.call(instance, predicate);
    }
}
