package net.sabio.abyssium.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import net.sabio.abyssium.Abyssium;
import net.minecraft.item.Item;

import java.util.function.Function;

import static net.sabio.abyssium.item.ModToolMaterials.ABYSSIUM_TOOL_MATERIAL;

public class ModItems {
    public static final Item ABYSSIUM_INGOT = register("abyssium_ingot", Item::new, new Item.Settings());
    public static final Item ABYSSIUM_SHARD = register("abyssium_shard", Item::new, new Item.Settings());
    // public static final Item ABYSSIUM_UPGRADE_SMITHING_TEMPLATE = register(
            // "abyssium_upgrade_smithing_template", ModSmithingTemplateItem::createAbyssiumUpgrade, new Item.Settings().rarity(Rarity.UNCOMMON)
    // );

    // Armor Items

    public static final Item ABYSSIUM_HELMET = register("abyssium_helmet", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.HELMET));
    public static final Item ABYSSIUM_CHESTPLATE = register("abyssium_chestplate", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.CHESTPLATE));
    public static final Item ABYSSIUM_LEGGINGS = register("abyssium_leggings", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.LEGGINGS));
    public static final Item ABYSSIUM_BOOTS = register("abyssium_boots", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.BOOTS));

    // Tools

    public static final Item ABYSSIUM_SWORD = register("abyssium_sword", Item::new, new Item.Settings().sword(ABYSSIUM_TOOL_MATERIAL, 4F, -2.4F).fireproof());
    public static final Item ABYSSIUM_AXE = register(
            "abyssium_axe", settings -> new AxeItem(ABYSSIUM_TOOL_MATERIAL, 6.0F, -3.0F, settings), new Item.Settings().fireproof()
    );
    public static final Item ABYSSIUM_PICKAXE = register("abyssium_pickaxe", Item::new, new Item.Settings().pickaxe(ABYSSIUM_TOOL_MATERIAL, 1.0F, -2.8F).fireproof());
    public static final Item ABYSSIUM_SHOVEL = register(
            "abyssium_shovel", settings -> new ShovelItem(ABYSSIUM_TOOL_MATERIAL, -1.5F, -3.0F, settings), new Item.Settings().fireproof()
    );
    public static final Item ABYSSIUM_HOE = register(
            "abyssium_hoe", settings -> new HoeItem(ABYSSIUM_TOOL_MATERIAL, -5.0F, 0.0F, settings), new Item.Settings().fireproof()
    );

    // Utility

    public static final Item ABYSSIUM_PLATED_ELYTRA = register(
            "abyssium_plated_elytra.json",
            Item::new,
            new Item.Settings()
                    .maxDamage(1728)
                    .rarity(Rarity.EPIC)
                    .component(DataComponentTypes.GLIDER, Unit.INSTANCE)
                    .component(
                            DataComponentTypes.EQUIPPABLE,
                            EquippableComponent.builder(EquipmentSlot.CHEST)
                                    .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA)
                                    .model(EquipmentAssetKeys.ELYTRA)
                                    .damageOnHurt(false)
                                    .build()
                    )
                    .repairable(ABYSSIUM_INGOT)
    );

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Abyssium.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register(itemGroup -> {
                    itemGroup.add(ModItems.ABYSSIUM_SHARD);
                    itemGroup.add(ModItems.ABYSSIUM_INGOT);
                });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.ABYSSIUM_HELMET);
                    itemGroup.add(ModItems.ABYSSIUM_CHESTPLATE);
                    itemGroup.add(ModItems.ABYSSIUM_LEGGINGS);
                    itemGroup.add(ModItems.ABYSSIUM_BOOTS);
                    itemGroup.add(ModItems.ABYSSIUM_SWORD);
                    itemGroup.add(ModItems.ABYSSIUM_AXE);
                });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.ABYSSIUM_AXE);
                    itemGroup.add(ModItems.ABYSSIUM_PICKAXE);
                    itemGroup.add(ModItems.ABYSSIUM_SHOVEL);
                    itemGroup.add(ModItems.ABYSSIUM_HOE);
                    itemGroup.add(ModItems.ABYSSIUM_PLATED_ELYTRA);
                });
    }
}