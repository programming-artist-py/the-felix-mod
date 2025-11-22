package penguin.felix.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import penguin.felix.FelixMod;
import penguin.felix.client.render.FelixRenderer;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class FelixModClient implements ClientModInitializer {

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public void onInitializeClient() {
        // Register the renderer for the static field
        EntityRendererRegistry.register(FelixMod.FELIXENTITY, FelixEntityRenderer::new);
        HandledScreens.register(FelixMod.FELIX_MENU_HANDLER, FelixMenuScreen::new);
    }
}