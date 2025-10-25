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
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
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
import net.minecraft.world.World;

public class FelixEntity extends AnimalEntity implements NamedScreenHandlerFactory {

    private boolean menuOpen = false;
    private final SimpleInventory inventory = new SimpleInventory(12);

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
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putByte("Slot", (byte) i);
                // Use the encode method with the registry from the world
                stack.encode(getWorld().getRegistryManager(), itemTag);
                itemList.add(itemTag);
            }
        }
        nbt.put("Inventory", itemList);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        NbtList itemList = nbt.getList("Inventory", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < inventory.size()) {
                // Decode using world registry
                ItemStack stack = ItemStack.fromNbt(getWorld().getRegistryManager(), itemTag).orElse(ItemStack.EMPTY);
                inventory.setStack(slot, stack);
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
        if (!menuOpen) {
            super.tickMovement(); // normal movement
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
                    NamedScreenHandlerFactory factory = new SimpleNamedScreenHandlerFactory(
                        (syncId, playerInventory, playerEntity) -> new FelixMenuScreenHandler(syncId, playerInventory, this),
                        Text.literal(" ")
                    );
                    player.openHandledScreen(factory);
                }
                return ActionResult.SUCCESS;
            }

            // Example: Feed him an apple to heal
            if (heldItem.isOf(Items.APPLE)) {
                this.heal(4.0F); // Heal Felix
                player.sendMessage(Text.translatable("felixentity.appleheal.message"), true);
                if (!player.getAbilities().creativeMode) {
                    heldItem.decrement(1);
                }
                return ActionResult.SUCCESS;
            }

            // Add other interactions here!
        }

        return super.interactMob(player, hand);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("felixentity.name");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FelixMenuScreenHandler(syncId, inv, this); // this = FelixEntity
    }


    public SimpleInventory getInventory() {
        return inventory;
    }
}