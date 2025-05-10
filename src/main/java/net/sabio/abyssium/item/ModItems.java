package net.sabio.abyssium.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.sabio.abyssium.Abyssium;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.function.Function;

public class ModItems {
    public static final Item ABYSSIUM = register("abyssium", Item::new, new Item.Settings());

    // Armor Items

    public static final Item ABYSSIUM_HELMET = register("abyssium_helmet", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.HELMET));
    public static final Item ABYSSIUM_CHESTPLATE = register("abyssium_chestplate", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.CHESTPLATE));
    public static final Item ABYSSIUM_LEGGINGS = register("abyssium_leggings", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.LEGGINGS));
    public static final Item ABYSSIUM_BOOTS = register("abyssium_boots", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.BOOTS));

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Abyssium.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(
            Registries.ITEM_GROUP.getKey(), Identifier.of(Abyssium.MOD_ID, "item_group"));

    public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.ABYSSIUM))
            .displayName(Text.translatable("itemGroup.abyssium"))
            .build();

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.ABYSSIUM);
                });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.ABYSSIUM_HELMET);
                    itemGroup.add(ModItems.ABYSSIUM_CHESTPLATE);
                    itemGroup.add(ModItems.ABYSSIUM_LEGGINGS);
                    itemGroup.add(ModItems.ABYSSIUM_BOOTS);
                });
    }
}