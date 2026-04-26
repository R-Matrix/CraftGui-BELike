package xyz.water.rmatrix.cmod.craftguibelike;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import xyz.water.rmatrix.cmod.craftguibelike.api.IEnhancedRecipeBookCategoryAPI;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.EnhancedRecipeBookCategoryAPIImpl;
import xyz.water.rmatrix.cmod.craftguibelike.command.ClearFavoriteCommand;
import xyz.water.rmatrix.cmod.craftguibelike.item.ModItem;
import xyz.water.rmatrix.cmod.craftguibelike.sorters.*;
import xyz.water.rmatrix.cmod.craftguibelike.utils.ClientCategoryManager;
import xyz.water.rmatrix.cmod.craftguibelike.utils.SorterManager;

import static xyz.water.rmatrix.cmod.craftguibelike.CraftGuiBELike.MOD_ID;

public class CraftGuiBELikeClient implements ClientModInitializer {

	public static final Identifier FAVORITE_CATEGORY_ID = Identifier.of(MOD_ID, "favorite_category");
	public static RecipeBookCategory FAVORITE_CATEGORY;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		IEnhancedRecipeBookCategoryAPI api = EnhancedRecipeBookCategoryAPIImpl.getINSTANCE();
		FAVORITE_CATEGORY = api.registerCategory(FAVORITE_CATEGORY_ID, ModItem.FAVORITE_STAR);

		SorterManager.register(MOD_ID, new MaterialMatchSorter());
		SorterManager.register(MOD_ID, new RegistryOrderSorter());
		SorterManager.register(MOD_ID, new VanillaFirstSorter());
		SorterManager.register(MOD_ID, new ModFirstSorter());
		SorterManager.register(MOD_ID, new PinYinSorter());
		SorterManager.register(MOD_ID, new ContainGroupSorter());

		ClientCommandRegistrationCallback.EVENT.register(new ClearFavoriteCommand());

		ClientCategoryManager.getInstance().loadAndRegisterAllCategories();

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ClientCategoryManager.getInstance());
	}
}