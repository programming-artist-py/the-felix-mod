package penguin.felix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import penguin.felix.entities.FelixEntity;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity {
    public BoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getPassengerAttachmentPos", at = @At("RETURN"), cancellable = true)
    private void adjustFelixPassengerOffset(Entity passenger, EntityDimensions dimensions, float scale, CallbackInfoReturnable<Vec3d> cir) {
        if (passenger instanceof FelixEntity) {
            Vec3d original = cir.getReturnValue();
            Vec3d adjusted = original.add(0.0, -0.5, 0.0);
            cir.setReturnValue(adjusted);
        }
    }
}