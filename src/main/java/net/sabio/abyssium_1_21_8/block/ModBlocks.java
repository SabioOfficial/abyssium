package net.sabio.abyssium_1_21_8.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.sabio.abyssium_1_21_8.Abyssium_1_21_8;

import java.util.function.Function;

public class ModBlocks {
    public static final Block ABYSSIUM_ORE = register(
            "abyssium_ore",
            Block::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.ANCIENT_DEBRIS),
            35.0f, // Strength value
            true
    );

    public static final Block BLOCK_OF_ABYSSIUM = register(
            "block_of_abyssium",
            Block::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.NETHERITE),
            60.0f, // Strength value
            true
    );

    public static AbstractBlock.Settings endStoneSettings = AbstractBlock.Settings.copy(Blocks.END_STONE);

    public static final Block INFESTED_END_STONE = register(
            "infested_end_stone",
            settings -> new EndermiteInfestedBlock(Blocks.END_STONE, settings),
            endStoneSettings,
            15.0f,
            true
    );

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, float strength, boolean shouldRegisterItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);

        // Apply strength to settings
        settings = settings.strength(strength);

        // *** Add this line to require a tool for harvesting ***
        settings = settings.requiresTool();

        Block block = blockFactory.apply(settings.registryKey(blockKey));

        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = keyOfItem(name);
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Abyssium_1_21_8.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Abyssium_1_21_8.MOD_ID, name));
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL)
                .register(itemGroup -> {
                    itemGroup.add(ModBlocks.ABYSSIUM_ORE);
                    itemGroup.add(ModBlocks.INFESTED_END_STONE);
                });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS)
                .register((itemGroup) -> itemGroup.add(ModBlocks.BLOCK_OF_ABYSSIUM));
    }
}
