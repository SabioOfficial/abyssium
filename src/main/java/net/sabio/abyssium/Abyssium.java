package net.sabio.abyssium;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.sabio.abyssium.item.ModItems;
import net.sabio.abyssium.block.ModBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Abyssium implements ModInitializer {
    public static final String MOD_ID = "abyssium_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final RegistryKey<PlacedFeature> CUSTOM_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("abyssium_mod","ore_abyssium_ore"));

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModBlocks.initialize();
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES, CUSTOM_ORE_PLACED_KEY);
    }
}
