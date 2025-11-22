package penguin.felix.entities;

import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import penguin.felix.FelixMod;
import penguin.felix.data.FelixConfig;
import penguin.serpentine.core.Serpentine;

public class FelixEntity extends AnimalEntity implements GeoEntity, NamedScreenHandlerFactory, Immutable {

    private boolean menuOpen = false;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(12, ItemStack.EMPTY);
    private boolean felixMenuDisabledAi = false;
    public PlayerEntity currentViewer; // keep public for handler access
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static int Default_hp = Serpentine.get(FelixMod.MOD_ID, FelixConfig.class).default_felix_hp;

    public FelixEntity(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
        float configHp = Serpentine.get(FelixMod.MOD_ID, FelixConfig.class).default_felix_hp;
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(configHp);
        this.setHealth(configHp); // set current health to match
    }

    public static DefaultAttributeContainer.Builder createFelixAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0) // temporary default
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(3, new TemptGoal(this, 1.1, stack -> stack.getItem() == Items.DIAMOND, false));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        NbtList itemList = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                NbtCompound stackTag = new NbtCompound();
                stackTag.putByte("Slot", (byte) i);
                stackTag.putString("Item", net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).toString());
                stackTag.putByte("Count", (byte) stack.getCount());
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
        return null;
    }

    public boolean isMenuOpen() {
        return menuOpen;
    }

    public void setMenuOpen(boolean menuOpen) {
        this.menuOpen = menuOpen;
    }

    @Override
    public void tickMovement() {
        if (this.hasVehicle()) {
            float vehicleYaw = this.getVehicle().getYaw();
            this.setYaw(vehicleYaw);
            this.setBodyYaw(vehicleYaw);
            this.setHeadYaw(vehicleYaw);
            return;
        }

        super.tickMovement();

        if (!this.menuOpen && this.isAiDisabled()) {
            this.setAiDisabled(false);
        }

        if (!this.getWorld().isClient && this.age % 1200 == 0 && this.random.nextFloat() < 0.125F) {
            this.dropGooBall();
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        // Close menus before entity is removed
        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                if (player.currentScreenHandler instanceof FelixMenuScreenHandler handler && handler.getFelix() == this) {
                    player.closeHandledScreen();
                }
            }
        }

        super.onDeath(source);

        // Drop Felix's inventory after closing screens
        if (!this.getWorld().isClient) {
            for (ItemStack stack : this.inventory) {
                if (!stack.isEmpty()) this.dropStack(stack);
            }
            this.inventory.clear();
        }
    }

    private void dropGooBall() {
        if (this.getWorld().isClient) return;
        var goo = new ItemStack(FelixMod.FELIXSLIMEBALL);
        this.dropStack(goo);
        this.playSound(net.minecraft.sound.SoundEvents.ENTITY_SLIME_SQUISH, 0.5F, 1.0F + this.random.nextFloat() * 0.4F);
    }

    public void disableAiForMenu() {
        this.setAiDisabled(true);
        this.felixMenuDisabledAi = true;
    }

    public void enableAiForMenu() {
        this.setAiDisabled(false);
        this.felixMenuDisabledAi = false;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!player.getWorld().isClient) {
            ItemStack heldItem = player.getStackInHand(hand);

            if (heldItem.isEmpty()) {
                this.currentViewer = player;
                this.disableAiForMenu();

                player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                        (syncId, inv, p) -> new FelixMenuScreenHandler(syncId, inv, this.getId()),
                        Text.literal(" ")
                ));
                return ActionResult.SUCCESS;
            }

            if (heldItem.isOf(Items.APPLE)) {
                this.heal(4.0F);
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
        return new FelixMenuScreenHandler(syncId, inv, this.getId());
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        this.currentViewer = null;
    }

    public DefaultedList<ItemStack> getInventoryList() {
        return inventory;
    }

    public Inventory getInventory() {
        return new Inventory() {
            @Override public int size() { return inventory.size(); }
            @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
            @Override public ItemStack getStack(int slot) { return inventory.get(slot); }
            @Override public ItemStack removeStack(int slot, int amount) {
                ItemStack stack = Inventories.splitStack(inventory, slot, amount);
                markDirty(); return stack;
            }
            @Override public ItemStack removeStack(int slot) {
                ItemStack stack = inventory.get(slot);
                inventory.set(slot, ItemStack.EMPTY);
                markDirty(); return stack;
            }
            @Override public void setStack(int slot, ItemStack stack) {
                inventory.set(slot, stack);
                markDirty();
            }
            @Override public void markDirty() {}
            @Override public boolean canPlayerUse(PlayerEntity player) { return true; }
            @Override public void clear() { inventory.clear(); }
        };
    }

    @Override
    public <E> Optional<Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOptional'");
    }

    @Override
    public Stream<Entry<?>> streamAllRegistries() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'streamAllRegistries'");
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Example — you can add animation controllers here
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}