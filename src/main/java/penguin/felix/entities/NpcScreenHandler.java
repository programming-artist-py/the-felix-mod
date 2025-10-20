package penguin.felix.entities;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class NpcScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    // This is called from the NamedScreenHandlerFactory in NpcEntity
    public NpcScreenHandler(int syncId, PlayerInventory playerInventory, int npcEntityId) {
        super(null, syncId); // We'll register a ScreenHandlerType later if needed
        // For simplicity, give the NPC a dummy inventory (can be 0 slots for a menu-only GUI)
        this.inventory = new SimpleInventory(0);

        // Example: if you wanted a 9-slot inventory for demonstration
        // this.inventory = new SimpleInventory(9);
        // for (int i = 0; i < 9; i++) {
        //     this.addSlot(new Slot(inventory, i, 8 + i * 18, 18));
        // }

        // Player inventory slots (optional if you want player to see their own inventory)
        int startX = 8;
        int startY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        startX + col * 18, startY + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, startX + col * 18, startY + 58));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true; // always allow interaction
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // TODO
        throw new UnsupportedOperationException("Unimplemented method 'quickMove'");
    }
}