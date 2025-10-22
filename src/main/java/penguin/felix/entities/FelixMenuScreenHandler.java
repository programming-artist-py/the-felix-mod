package penguin.felix.entities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import penguin.felix.FelixMod;
public class FelixMenuScreenHandler extends ScreenHandler {

    private final SimpleInventory inventory; // fuck you warning
    private final FelixEntity owner;

    public FelixMenuScreenHandler(int syncId, PlayerInventory playerInventory, FelixEntity owner) {
        super(FelixMod.FELIX_MENU_HANDLER, syncId);
        this.inventory = new SimpleInventory(3); // number of custom slots
        this.owner = owner;
        if (owner != null) owner.setAiDisabled(true);

        // Player inventory slots
        int startX = 10;
        int startY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 4; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 4; ++col) {
            this.addSlot(new Slot(playerInventory, col, startX + col * 18, startY + 58));
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
        return ItemStack.EMPTY;
    }
}