package penguin.felix.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
import penguin.felix.entities.FelixEntity;

@Environment(EnvType.CLIENT)
public class FelixEntityRenderer extends MobEntityRenderer<FelixEntity, BipedEntityModel<FelixEntity>> {

    public FelixEntityRenderer(EntityRendererFactory.Context context) {
        // Must provide a concrete BipedEntityModel with ModelPart
        super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(FelixEntity entity) {
        // TODO
        return net.minecraft.util.Identifier.of("felix", "textures/entities/felix.png");
    }
}