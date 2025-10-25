package penguin.felix.entities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import penguin.felix.FelixMod;

public class FelixMenuScreenHandler extends ScreenHandler {

    private final Inventory felixInventory;
    private final int entityId;
    private FelixEntity owner;
    
    public FelixMenuScreenHandler(int syncId, PlayerInventory playerInventory, int entityId) {
        super(FelixMod.FELIX_MENU_HANDLER, syncId);

        PlayerEntity player = playerInventory.player;
        World world = player.getWorld();
        if (world != null && world.getEntityById(entityId) instanceof FelixEntity felix) {
            owner = felix;
            owner.setAiDisabled(true);
        } else {
            owner = null;
        }

        this.felixInventory = new Inventory() {
            private final DefaultedList<ItemStack> inv = (owner != null) ? owner.getInventoryList() : DefaultedList.ofSize(12, ItemStack.EMPTY);

            @Override public int size() { return inv.size(); }
            @Override public boolean isEmpty() { return inv.stream().allMatch(ItemStack::isEmpty); }
            @Override public ItemStack getStack(int slot) { return inv.get(slot); }
            @Override public ItemStack removeStack(int slot) { ItemStack s = inv.get(slot); inv.set(slot, ItemStack.EMPTY); markDirty(); return s; }
            @Override public ItemStack removeStack(int slot, int amount) { ItemStack s = Inventories.splitStack(inv, slot, amount); markDirty(); return s; }
            @Override public void setStack(int slot, ItemStack stack) { inv.set(slot, stack); markDirty(); }
            @Override public void markDirty() {}
            @Override public boolean canPlayerUse(PlayerEntity player) { return true; }
            @Override public void clear() { inv.clear(); }
        };
        this.entityId = 0;

        int startX = 10;
        int startY = 84;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                int slotIndex = col + row * 4;
                this.addSlot(new Slot(felixInventory, slotIndex, startX + col * 18, startY + row * 18));
            }
        }

        // Player hotbar
        startY = 142;
        for (int col = 0; col < 4; ++col) {
            int x = startX + col * 18;
            this.addSlot(new Slot(playerInventory, col, x, startY)); // Visible
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) { return true; }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        if (!player.getWorld().isClient() && player.getWorld() instanceof ServerWorld serverWorld) {
            // Look up the entity by ID
            var entity = serverWorld.getEntityById(entityId);
            if (entity instanceof FelixEntity felix) {
                felix.setAiDisabled(false);
                owner.setAiDisabled(false);
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack original = slot.getStack();
            newStack = original.copy();

            if (index >= 0 && index < 12) { // Felix slots
                if (!this.insertItem(original, 16, 12, true)) return ItemStack.EMPTY;
            } else if (index >= 12 && index < 16) { // player hotbar
                if (!this.insertItem(original, 0, 12, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }

            if (original.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }
        return newStack;
    }
}