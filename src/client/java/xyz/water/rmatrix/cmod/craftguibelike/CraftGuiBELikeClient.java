package xyz.water.rmatrix.cmod.craftguibelike;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Identifier;
import xyz.water.rmatrix.cmod.craftguibelike.api.IEnhancedRecipeBookCategoryAPI;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.EnhancedRecipeBookCategoryAPIImpl;
import xyz.water.rmatrix.cmod.craftguibelike.command.ClearFavoriteCommand;
import xyz.water.rmatrix.cmod.craftguibelike.item.ModItem;

public class CraftGuiBELikeClient implements ClientModInitializer {

	public static final Identifier FAVORITE_CATEGORY_ID = Identifier.of(CraftGuiBELike.MOD_ID, "favorite_category");
	public static RecipeBookCategory FAVORITE_CATEGORY;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		IEnhancedRecipeBookCategoryAPI api = EnhancedRecipeBookCategoryAPIImpl.getINSTANCE();
		FAVORITE_CATEGORY = api.registerCategory(FAVORITE_CATEGORY_ID, ModItem.FAVORITE_STAR);

		ClientCommandRegistrationCallback.EVENT.register(new ClearFavoriteCommand());
	}
}