package net.sabio.abyssium_1_21_8.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import net.sabio.abyssium_1_21_8.Abyssium_1_21_8;

import java.util.List;
import java.util.function.Function;

import static net.sabio.abyssium_1_21_8.item.ModToolMaterials.ABYSSIUM_TOOL_MATERIAL;

public final class ModItems {
    public static final Item ABYSSIUM_INGOT = register("abyssium_ingot", Item::new, new Item.Settings());
    public static final Item ABYSSIUM_SHARD = register("abyssium_shard", Item::new, new Item.Settings());

    // Armor Items

    public static final Item ABYSSIUM_HELMET = register("abyssium_helmet", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.HELMET));
    public static final Item ABYSSIUM_CHESTPLATE = register("abyssium_chestplate", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.CHESTPLATE));
    public static final Item ABYSSIUM_LEGGINGS = register("abyssium_leggings", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.LEGGINGS));
    public static final Item ABYSSIUM_BOOTS = register("abyssium_boots", Item::new, new Item.Settings().armor(ModArmorMaterials.INSTANCE, EquipmentType.BOOTS));

    // Tools

    public static final Item ABYSSIUM_SWORD = register("abyssium_sword", Item::new, new Item.Settings().sword(ABYSSIUM_TOOL_MATERIAL, 4F, -2.4F).fireproof());
    public static final Item ABYSSIUM_AXE = register(
            "abyssium_axe", settings -> new AxeItem(ABYSSIUM_TOOL_MATERIAL, 6.0F, -3.0F, settings), new Item.Settings()
    );
    public static final Item ABYSSIUM_PICKAXE = register(
            "abyssium_pickaxe",
            Item::new,
            new Item.Settings().pickaxe(ABYSSIUM_TOOL_MATERIAL, 1.0F, -2.8F).fireproof()
    );
    public static final Item ABYSSIUM_SHOVEL = register(
            "abyssium_shovel", settings -> new ShovelItem(ABYSSIUM_TOOL_MATERIAL, -1.5F, -3.0F, settings), new Item.Settings()
    );
    public static final Item ABYSSIUM_HOE = register(
            "abyssium_hoe", settings -> new HoeItem(ABYSSIUM_TOOL_MATERIAL, -5.0F, 0.0F, settings), new Item.Settings()
    );

    // Utility

    public static final Item ABYSSIUM_PLATED_ELYTRA = register(
            "abyssium_plated_elytra",
            Item::new,
            new Item.Settings()
                    .maxDamage(756)
                    .rarity(Rarity.EPIC)
                    .component(DataComponentTypes.GLIDER, Unit.INSTANCE)
                    .component(
                            DataComponentTypes.EQUIPPABLE,
                            EquippableComponent.builder(EquipmentSlot.CHEST)
                                    .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA)
                                    .model(ModEquipmentAssetKeys.ABYSSIUM_PLATED_ELYTRA)
                                    .damageOnHurt(false)
                                    .build()
                    )
                    .repairable(ABYSSIUM_INGOT)
    );

    // Armor Trims

    // thanks to https://github.com/CoolerProYT/MoreGears/blob/1.21.5-fabric/src/main/java/com/coolerpromc/moregears/item/MGItems.java
    // roses are red violets are blue
    // ctrl c ctrl v go f ck you
    // all jokes aside, this update wouldn't be possible without that.
    // yeah the fabric docs are f cking outdated RAHHHHHHH

    public static final SmithingTemplateItem ABYSSIUM_UPGRADE_SMITHING_TEMPLATE =
            (SmithingTemplateItem) register(
                    "abyssium_upgrade_smithing_template",
                    settings -> new SmithingTemplateItem(
                            Text.translatable("item.abyssium_mod.abyssium_upgrade_smithing_template.equipment_info")
                                    .fillStyle(Style.EMPTY.withColor(Formatting.BLUE)),
                            Text.translatable("item.abyssium_mod.abyssium_upgrade_smithing_template.ingredient")
                                    .fillStyle(Style.EMPTY.withColor(Formatting.BLUE)),
                            Text.translatable("item.abyssium_mod.abyssium_upgrade_smithing_template.upgrade_description")
                                    .fillStyle(Style.EMPTY.withColor(Formatting.DARK_BLUE)),
                            Text.translatable("item.abyssium_mod.abyssium_upgrade_smithing_template.additions_slot_description"),
                            List.of(
                                    Identifier.ofVanilla("container/slot/helmet"),
                                    Identifier.ofVanilla("container/slot/chestplate"),
                                    Identifier.ofVanilla("container/slot/leggings"),
                                    Identifier.ofVanilla("container/slot/boots"),
                                    Identifier.ofVanilla("container/slot/hoe"),
                                    Identifier.ofVanilla("container/slot/axe"),
                                    Identifier.ofVanilla("container/slot/sword"),
                                    Identifier.ofVanilla("container/slot/shovel"),
                                    Identifier.ofVanilla("container/slot/pickaxe"),
                                    Identifier.of(Abyssium_1_21_8.MOD_ID, "container/slot/elytra")
                            ),
                            List.of(Identifier.ofVanilla("container/slot/ingot")),
                            settings
                    ),
                    new Item.Settings()
                            .rarity(Rarity.RARE)
            );

//    public static final Item ABYSSIUM_PLATED_SHIELD = register(
//            "abyssium_plated_shield",
//            Item::new,
//            new Item.Settings()
//                    .maxDamage(1334)
//                    .rarity(Rarity.EPIC)
//                    .component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
//                    .repairable(ABYSSIUM_INGOT)
//                    .equippableUnswappable(EquipmentSlot.OFFHAND)
//                    .component(
//                            DataComponentTypes.BLOCKS_ATTACKS,
//                            new BlocksAttacksComponent(
//                                    0.25F,
//                                    1.0F,
//                                    List.of(new BlocksAttacksComponent.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
//                                    new BlocksAttacksComponent.ItemDamage(3.0F, 1.0F, 1.0F),
//                                    Optional.of(DamageTypeTags.BYPASSES_SHIELD),
//                                    Optional.of(SoundEvents.ITEM_SHIELD_BLOCK),
//                                    Optional.of(SoundEvents.ITEM_SHIELD_BREAK)
//                            )
//                    )
//                    .component(DataComponentTypes.BREAK_SOUND, SoundEvents.ITEM_SHIELD_BREAK)
//    );


    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Abyssium_1_21_8.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register(itemGroup -> {
                    itemGroup.add(ModItems.ABYSSIUM_SHARD);
                    itemGroup.add(ModItems.ABYSSIUM_INGOT);
                    itemGroup.add(ModItems.ABYSSIUM_UPGRADE_SMITHING_TEMPLATE);
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
                    itemGroup.add(ModItems.ABYSSIUM_SWORD);
                    itemGroup.add(ModItems.ABYSSIUM_AXE);
                    itemGroup.add(ModItems.ABYSSIUM_PICKAXE);
                    itemGroup.add(ModItems.ABYSSIUM_SHOVEL);
                    itemGroup.add(ModItems.ABYSSIUM_HOE);
                    itemGroup.add(ModItems.ABYSSIUM_PLATED_ELYTRA);
                });
    }
}