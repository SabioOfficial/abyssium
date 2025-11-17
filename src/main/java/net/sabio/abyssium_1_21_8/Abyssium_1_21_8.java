package net.sabio.abyssium_1_21_8;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.sabio.abyssium_1_21_8.block.ModBlocks;
import net.sabio.abyssium_1_21_8.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Abyssium_1_21_8 implements ModInitializer {
    public static final String MOD_ID = "abyssium_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final RegistryKey<PlacedFeature> ORE_SINGLE_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("abyssium_mod", "ore_abyssium_ore_single"));

    public static final RegistryKey<PlacedFeature> ORE_CLUMP_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("abyssium_mod", "ore_abyssium_ore_clump"));

    public static final RegistryKey<PlacedFeature> END_STONE_INFESTED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("abyssium_mod", "end_stone_infested"));

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModBlocks.initialize();
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_SINGLE_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_CLUMP_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_DECORATION, END_STONE_INFESTED_KEY);
    }
}
