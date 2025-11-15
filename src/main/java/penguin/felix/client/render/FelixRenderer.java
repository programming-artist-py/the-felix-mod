package penguin.felix.client.render;

import penguin.felix.entities.FelixEntity;
import penguin.felix.client.models.FelixModel;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FelixRenderer extends GeoEntityRenderer<FelixEntity> {

    public FelixRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FelixModel());
        this.shadowRadius = 0.5f; // size of shadow under entity
    }

    @Override
    public Identifier getTextureLocation(FelixEntity instance) {
        return Identifier.of("felix", "textures/entity/felix.png");
    }
}