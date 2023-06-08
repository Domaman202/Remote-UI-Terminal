package ru.DmN.ruit;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class MainClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(Main.RUIT_SCREEN_HANDLER, GenericContainerScreen::new);
    }
}
