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

import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.water.rmatrix.cmod.craftguibelike.CraftGuiBELike;
import xyz.water.rmatrix.cmod.craftguibelike.api.IRecipeSorter;
import xyz.water.rmatrix.cmod.craftguibelike.sorters.*;

import java.util.*;

public class SorterManager {

    private static SorterManager INSTANCE;

    private final Map<Identifier, IRecipeSorter> sorters = new LinkedHashMap<>();
    private IRecipeSorter currentSorter;

    private SorterManager(){
        registerDefaultSorter();
    }

    public static SorterManager getINSTANCE(){
        if(INSTANCE == null) INSTANCE = new SorterManager();
        return INSTANCE;
    }

    private void registerDefaultSorter() {
        String my_modid = CraftGuiBELike.MOD_ID;
        IRecipeSorter sorter = new DefaultSorter();
        register(my_modid ,sorter);
        register(my_modid, new MaterialMatchSorter());
        register(my_modid, new RegistryOrderSorter());
        register(my_modid, new VanillaFirstSorter());
        register(my_modid, new ModFirstSorter());
        register(my_modid, new PinYinSorter());
        register(my_modid, new ContainGroupSorter());
        if(currentSorter == null) currentSorter = sorter;
    }

    public void register(String modId , @NotNull IRecipeSorter sorter){
        Identifier identifier = Identifier.of(modId, sorter.getName());
        sorters.put(identifier, sorter);
    }

    public IRecipeSorter getCurrentSorter(){
        return this.currentSorter;
    }

    public void setCurrentSorter(Identifier id){
        this.currentSorter = sorters.get(id);
    }

    public void setCurrentSorter(String id){
        this.currentSorter = sorters.get(Identifier.of(id));
    }

    public void setCurrentSorter(@Nullable IRecipeSorter sorter){
        this.currentSorter = sorter;
    }

    public Collection<Identifier> getAvailableSorters() {
        return sorters.keySet();
    }

    public List<RecipeResultCollection> sort(
            List<RecipeResultCollection> recipes) {

        if (currentSorter == null) {
            return recipes;
        }
        return currentSorter.sortRecipes(recipes);
    }

    public void cycleCurrentSorter() {

        List<IRecipeSorter> allsorters = new ArrayList<>(sorters.values());

        if(allsorters.isEmpty()) return;

        int currentIndex = allsorters.indexOf(currentSorter);
        int nextIndex = (currentIndex + 1) % (allsorters.size());

        this.currentSorter = allsorters.get(nextIndex);
    }
}
