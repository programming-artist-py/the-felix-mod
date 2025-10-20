package penguin.felix.entities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import penguin.felix.FelixMod;
import net.minecraft.inventory.SimpleInventory;

public class NpcScreenHandler extends ScreenHandler {

    private final SimpleInventory inventory;

    public NpcScreenHandler(int syncId, PlayerInventory playerInventory, int i) {
        super(FelixMod.NPC_SCREEN_HANDLER, syncId);
        this.inventory = new SimpleInventory(0);

        // Player inventory slots (if you want to show them)
        int startX = 8;
        int startY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        startX + col * 18, startY + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, startX + col * 18, startY + 58));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }
}