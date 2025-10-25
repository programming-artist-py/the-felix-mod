package penguin.felix.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import penguin.felix.FelixMod;
import penguin.felix.entities.FelixEntity;

public class FelixSpawnEggItem extends Item {

    public FelixSpawnEggItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;

        BlockPos pos = context.getBlockPos();
        Direction face = context.getSide();

        // spawn slightly offset from block face
        BlockPos spawnPos = pos.offset(face);
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        FelixEntity felix = FelixMod.FELIXENTITY.create(world);
        if (felix == null) return ActionResult.PASS;

        double x = spawnPos.getX() + 0.5;
        double y = spawnPos.getY();
        double z = spawnPos.getZ() + 0.5;

        felix.refreshPositionAndAngles(x, y, z, world.random.nextFloat() * 360F, 0.0F);
        world.spawnEntity(felix);

        if (player != null && !player.getAbilities().creativeMode)
            stack.decrement(1);

        return ActionResult.SUCCESS;
    }
}