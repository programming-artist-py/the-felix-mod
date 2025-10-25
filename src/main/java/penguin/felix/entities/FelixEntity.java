package penguin.felix.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class FelixEntity extends AnimalEntity implements NamedScreenHandlerFactory {

    private boolean menuOpen = false;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(12, ItemStack.EMPTY);

    public FelixEntity(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createFelixAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(3, new TemptGoal(this, 1.1, (stack) -> stack.getItem() == Items.DIAMOND, false));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false; // safe default
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        NbtList itemList = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                NbtCompound stackTag = new NbtCompound();
                stackTag.putByte("Slot", (byte)i);
                stackTag.putString("Item", net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).toString());
                stackTag.putByte("Count", (byte)stack.getCount());
                // NO getNbt() or setNbt() called here
                itemList.add(stackTag);
            }
        }
        nbt.put("Inventory", itemList);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        inventory.clear();
        NbtList itemList = nbt.getList("Inventory", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound stackTag = itemList.getCompound(i);
            int slot = stackTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < inventory.size()) {
                String itemId = stackTag.getString("Item");
                int count = stackTag.getByte("Count");
                var item = net.minecraft.registry.Registries.ITEM.get(Identifier.tryParse(itemId));
                if (item != null) inventory.set(slot, new ItemStack(item, count));
            }
        }
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity mate) {
        return null; // safe default
    }

    public boolean isMenuOpen() {
        return menuOpen;
    }

    public void setMenuOpen(boolean menuOpen) {
        this.menuOpen = menuOpen;
    }

    @Override
    public void tickMovement() {
        if (!this.menuOpen && this.isAiDisabled()) {
            this.setAiDisabled(false);
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!player.getWorld().isClient) {
            ItemStack heldItem = player.getStackInHand(hand);

            //If holding nothing, open the menu :3

            if (heldItem.isEmpty()) {
                // Open the menu
                if (!this.getWorld().isClient) {
                    player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                        (syncId, inv, playerEntity) -> new FelixMenuScreenHandler(syncId, inv, this.getId()), // pass entity id
                        Text.literal(" ")
                    ));
                }
                return ActionResult.SUCCESS;
            }

            if (heldItem.isOf(Items.APPLE)) {
                this.heal(4.0F); // Heal Felix
                player.sendMessage(Text.translatable("felixentity.appleheal.message"), true);
                if (!player.getAbilities().creativeMode) {
                    heldItem.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("felixentity.name");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FelixMenuScreenHandler(syncId, inv, this.getId()); // this = FelixEntity
    }

    public DefaultedList<ItemStack> getInventoryList() {
        return inventory;
    }

    public Inventory getInventory() {
        return new Inventory() {
            @Override
            public int size() {
                return inventory.size();
            }

            @Override
            public boolean isEmpty() {
                return inventory.stream().allMatch(ItemStack::isEmpty);
            }

            @Override
            public ItemStack getStack(int slot) {
                return inventory.get(slot);
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                ItemStack stack = Inventories.splitStack(inventory, slot, amount);
                markDirty();
                return stack;
            }

            @Override
            public ItemStack removeStack(int slot) {
                ItemStack stack = inventory.get(slot);
                inventory.set(slot, ItemStack.EMPTY);
                markDirty();
                return stack;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                inventory.set(slot, stack);
                markDirty();
            }

            @Override
            public void markDirty() {}

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return true;
            }

            @Override
            public void clear() {
                inventory.clear();
            }
        };
    }

}