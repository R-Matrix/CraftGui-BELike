package xyz.water.rmatrix.cmod.craftguibelike;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.water.rmatrix.cmod.craftguibelike.event.RecipeUnlockCallback;
import xyz.water.rmatrix.cmod.craftguibelike.item.ModItem;
import xyz.water.rmatrix.cmod.craftguibelike.manager.ServerCategoryManager;
import xyz.water.rmatrix.cmod.craftguibelike.network.RecipeCategoryS2CPayLoad;

public class CraftGuiBELike implements ModInitializer {
	public static final String MOD_ID = "craftgui-belike";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModItem.initialize();

		PayloadTypeRegistry.playS2C().register(RecipeCategoryS2CPayLoad.ID, RecipeCategoryS2CPayLoad.CODEC);

		RecipeUnlockCallback.EVENT.register(((player, recipeId) -> {
			ServerCategoryManager.getInstance().onRecipeUnlocked(player, recipeId);
			return ActionResult.PASS;
		}));

		LOGGER.info("Hello Fabric world!");
	}
}