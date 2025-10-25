package penguin.felix.client;

import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;
import penguin.felix.entities.FelixEntity;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;

@Environment(EnvType.CLIENT)
public class FelixEntityRenderer extends MobEntityRenderer<FelixEntity, PlayerEntityModel<FelixEntity>> {

    public FelixEntityRenderer(EntityRendererFactory.Context context) {
        // true = slim arms, false = normal arms
        super(context, new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER_SLIM), true), 0.5f);
    }

    @Override
    public Identifier getTexture(FelixEntity entity) {
        return Identifier.of("felix", "textures/entities/felix.png");
    }
}