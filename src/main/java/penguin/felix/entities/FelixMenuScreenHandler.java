package penguin.felix.entities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import penguin.felix.FelixMod;

public class FelixMenuScreenHandler extends ScreenHandler {

    private final FelixEntity owner;

    public FelixMenuScreenHandler(int syncId, PlayerInventory playerInventory, FelixEntity owner) {
        super(FelixMod.FELIX_MENU_HANDLER, syncId);
        this.owner = owner;

        if (owner != null) owner.setAiDisabled(true);

        //FELIX INVENTORY SLOTS
        int startX = 10;
        int startY = 18;
        SimpleInventory felixInv = (owner != null) ? owner.getInventory() : new SimpleInventory(12);

        for (int row = 0; row < 3; row++) {         // 3 rows
            for (int col = 0; col < 4; col++) {     // 4 columns = 12 slots
                int slotIndex = col + row * 4;
                this.addSlot(new Slot(felixInv, slotIndex, startX + col * 18, startY + row * 18));
            }
        }

        //PLAYER INVENTORY SLOTS
        startX = 10;
        startY = 84;

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int playerSlot = col + row * 9 + 9;
                this.addSlot(new Slot(playerInventory, playerSlot, startX + col * 18, startY + row * 18));
            }
        }

        //HOTBAR
        startY = 142;
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, startX + col * 18, startY));
        }
    }

    public FelixEntity getOwner() {
        return owner;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (this.owner != null) {
            this.owner.setAiDisabled(false);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // Optional: implement shift-click logic here
        return ItemStack.EMPTY;
    }
}