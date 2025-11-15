package penguin.felix.client.models;

import penguin.felix.entities.FelixEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class FelixModel extends GeoModel<FelixEntity> {

    @Override
    public Identifier getModelResource(FelixEntity object) {
        return Identifier.of("felix", "geo/hehi.geo.json");
    }

    @Override
    public Identifier getTextureResource(FelixEntity object) {
        return Identifier.of("felix", "textures/entity/felix.png");
    }

    @Override
    public Identifier getAnimationResource(FelixEntity object) {
        return Identifier.of("felix", "animations/hehi.animation.json");
    }

}
