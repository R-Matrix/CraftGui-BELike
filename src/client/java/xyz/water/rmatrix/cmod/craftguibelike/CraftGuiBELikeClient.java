package xyz.water.rmatrix.cmod.craftguibelike;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Identifier;
import xyz.water.rmatrix.cmod.craftguibelike.api.impl.EnhancedRecipeBookCategoryAPIImpl;
import xyz.water.rmatrix.cmod.craftguibelike.command.ClearFavoriteCommand;
import xyz.water.rmatrix.cmod.craftguibelike.item.ModItem;
import xyz.water.rmatrix.cmod.craftguibelike.network.RecipeCategoryS2CPayLoad;

import java.util.Map;

public class CraftGuiBELikeClient implements ClientModInitializer {

	public static final Identifier FAVORITE_CATEGORY_ID = Identifier.of(CraftGuiBELike.MOD_ID, "favorite_category");
	public static RecipeBookCategory FAVORITE_CATEGORY;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EnhancedRecipeBookCategoryAPIImpl api = EnhancedRecipeBookCategoryAPIImpl.getINSTANCE();
		FAVORITE_CATEGORY = api.registerCategory(FAVORITE_CATEGORY_ID, ModItem.FAVORITE_STAR);

		ClientCommandRegistrationCallback.EVENT.register(new ClearFavoriteCommand());

		PayloadTypeRegistry.playS2C().register(RecipeCategoryS2CPayLoad.ID, RecipeCategoryS2CPayLoad.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(RecipeCategoryS2CPayLoad.ID,
				(payload, context) -> {
					Map<Identifier, Identifier> map = payload.categoryMapping();
					context.client().execute(() -> System.out.println("Received " + payload.getId() + " category mappings!"));
				});
	}
}