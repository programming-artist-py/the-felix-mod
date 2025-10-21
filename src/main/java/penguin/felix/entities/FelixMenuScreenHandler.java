package penguin.felix.entities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import penguin.felix.FelixMod;
public class FelixMenuScreenHandler extends ScreenHandler {

    private final SimpleInventory inventory;

    public FelixMenuScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(FelixMod.NPC_SCREEN_HANDLER, syncId);
        this.inventory = new SimpleInventory(3); // number of custom slots

        // Custom slots (like villager input/output)
        for (int i = 0; i < 3; i++) {
            this.addSlot(new Slot(inventory, i, 62 + i * 18, 17));
        }

        // Player inventory slots
        int startX = 8;
        int startY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }

        // Hotbar
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