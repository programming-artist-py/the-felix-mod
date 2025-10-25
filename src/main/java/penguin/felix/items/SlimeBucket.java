package penguin.felix.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SlimeBucket extends Item {

    public SlimeBucket(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            // Heal the player by 2 hearts
            user.heal(4.0F);

            // Play a squishy sound
            world.playSound(
                null,
                user.getX(), user.getY(), user.getZ(),
                net.minecraft.sound.SoundEvents.ENTITY_SLIME_SQUISH,
                net.minecraft.sound.SoundCategory.PLAYERS,
                1.0F,
                1.0F
            );

            // Consume one item
            if (!user.isCreative()) {
                stack.decrement(1);
            }
        } else {
            // Spawn particles client-side for visuals
            for (int i = 0; i < 10; i++) {
                double x = user.getX() + (world.random.nextDouble() - 0.5);
                double y = user.getEyeY() - 0.3;
                double z = user.getZ() + (world.random.nextDouble() - 0.5);
                world.addParticle(net.minecraft.particle.ParticleTypes.ITEM_SLIME, x, y, z, 0, 0.05, 0);
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}