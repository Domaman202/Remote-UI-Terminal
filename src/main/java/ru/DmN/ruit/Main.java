package ru.DmN.ruit;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import ru.DmN.ruit.items.RUITItem;
import ru.DmN.ruit.screen.RUITScreenHandler;

public class Main implements ModInitializer {
    public static final Identifier ID = new Identifier("ruit", "ruit");
    public static final Item RUIT_ITEM = new RUITItem();
    public static final ScreenHandlerType<RUITScreenHandler> RUIT_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(ID, RUITScreenHandler::new);

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, ID, RUIT_ITEM);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> content.add(RUITItem.create(9 * 6)));

    }
}
