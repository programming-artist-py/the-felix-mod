package penguin.felix;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import penguin.felix.entities.FelixEntity;
import penguin.felix.entities.FelixMenuScreenHandler;
import penguin.felix.items.FelixSpawnEggItem;
import penguin.felix.items.SlimeBall;
import penguin.felix.items.SlimeBucket;
import penguin.felix.items.SlimeStick;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.item.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FelixMod implements ModInitializer {
	public static final String MOD_ID = "felix";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static EntityType<FelixEntity> FELIXENTITY;
    public static ScreenHandlerType<FelixMenuScreenHandler> FELIX_MENU_HANDLER;
    public static final Item FELIXSLIMEBALL = new SlimeBall(new Item.Settings().maxCount(64));
    public static final Item FELIXSLIMEBUCKET = new SlimeBucket(new Item.Settings().maxCount(1));
    public static final Item FELIXSLIMESTICK = new SlimeStick(new Item.Settings().maxCount(16));
    public static final Item FELIXSPAWNEGG = new FelixSpawnEggItem(new Item.Settings().maxCount(64));
    public static RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = null;

    @Override
    public void onInitialize() {
        // Assign static field
        FELIXENTITY = registerEntity(MOD_ID, "felix",
            EntityType.Builder.create(FelixEntity::new, SpawnGroup.MISC)
                .dimensions(0.8f, 1.8f) // similar to a player
        );
        FabricDefaultAttributeRegistry.register(FELIXENTITY, FelixEntity.createFelixAttributes());
        FELIX_MENU_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("felix", "npc_menu"),
            new ScreenHandlerType<>((syncId, inv) -> new FelixMenuScreenHandler(syncId, inv, 0), FeatureFlags.VANILLA_FEATURES)
        );
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "goo_ball"), FELIXSLIMEBALL);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "goo_bucket"), FELIXSLIMEBUCKET);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "goo_stick"), FELIXSLIMESTICK);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "felix_spawn_egg"), FELIXSPAWNEGG);
        

        // creative tab
        CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(MOD_ID, "felixthings"));
        ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
		.icon(() -> new ItemStack(FELIXSLIMEBALL))
		.displayName(Text.translatable("itemGroup.felixmod"))
		.build();
        
        Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);
        ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY).register(ItemGroup -> {
            ItemGroup.add(FELIXSLIMEBALL);
            ItemGroup.add(FELIXSLIMEBUCKET);
            ItemGroup.add(FELIXSLIMESTICK);
            ItemGroup.add(FELIXSPAWNEGG);
        });
		LOGGER.info("[Felix] meow :3");
	}


    public static <T extends Entity> EntityType<T> registerEntity(String namespace, String id, EntityType.Builder<T> builder) {
        // Build the entity type using the String ID
        EntityType<T> entityType = builder.build(id);  
		RegistryKey<EntityType<?>> registryKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(namespace, id));
        // Register it in the entity registry
        return Registry.register(Registries.ENTITY_TYPE, registryKey, entityType);
    }

}