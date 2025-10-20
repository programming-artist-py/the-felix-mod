package penguin.felix.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import penguin.felix.FelixMod;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class FelixModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register the renderer for the static field
        EntityRendererRegistry.register(FelixMod.FELIXENTITY, FelixEntityRenderer::new);
    }
}