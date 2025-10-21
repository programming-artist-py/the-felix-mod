package penguin.felix;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import penguin.felix.entities.FelixEntity;
import penguin.felix.entities.FelixMenuScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FelixMod implements ModInitializer {
	public static final String MOD_ID = "felix";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static EntityType<FelixEntity> FELIXENTITY;
    public static ScreenHandlerType<FelixMenuScreenHandler> NPC_SCREEN_HANDLER;

    @Override
    public void onInitialize() {
        // Assign static field
        FELIXENTITY = registerEntity(MOD_ID, "felix",
            EntityType.Builder.create(FelixEntity::new, SpawnGroup.MISC)
                .dimensions(0.8f, 1.8f) // similar to a player
        );
        FabricDefaultAttributeRegistry.register(FELIXENTITY, FelixEntity.createFelixAttributes());
        NPC_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("felix", "npc_screen"),   
            new ScreenHandlerType<FelixMenuScreenHandler>((syncId, inv) -> new FelixMenuScreenHandler(syncId, inv), null)
        );
		LOGGER.info("Hello Fabric world!");
	}


    public static <T extends Entity> EntityType<T> registerEntity(String namespace, String id, EntityType.Builder<T> builder) {
        // Build the entity type using the String ID
        EntityType<T> entityType = builder.build(id);  
		RegistryKey<EntityType<?>> registryKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(namespace, id));
        // Register it in the entity registry
        return Registry.register(Registries.ENTITY_TYPE, registryKey, entityType);
    }

}