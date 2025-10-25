package penguin.felix.data;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import penguin.felix.FelixMod;

public class FelixRecipeProvider extends FabricRecipeProvider {

    public FelixRecipeProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, FelixMod.FELIXSLIMEBUCKET, 1)
            .pattern(" # ")
            .pattern(" * ")
            .pattern("   ")
            .input('#', FelixMod.FELIXSLIMEBALL)
            .input('*', Items.BUCKET)
            .criterion(hasItem(FelixMod.FELIXSLIMEBALL), conditionsFromItem(FelixMod.FELIXSLIMEBALL))
            .offerTo(exporter, "felix_goo_to_bucket");
    }

}