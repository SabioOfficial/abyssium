package net.sabio.abyssium_1_21_8.item;

import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.sabio.abyssium_1_21_8.Abyssium_1_21_8;

import java.util.Map;

public class ModArmorMaterials {
    public static final int BASE_DURABILITY = 50;

    public static final TagKey<Item> REPAIRS_ABYSSIUM_ARMOR = TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "repairs_abyssium_armor"));

    public static final RegistryKey<EquipmentAsset> ABYSSIUM_ARMOR_MATERIAL_KEY = RegistryKey.of(
            EquipmentAssetKeys.REGISTRY_KEY,
            Identifier.of(Abyssium_1_21_8.MOD_ID, "abyssium")
    );

    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    EquipmentType.HELMET, 3,
                    EquipmentType.CHESTPLATE, 8,
                    EquipmentType.LEGGINGS, 6,
                    EquipmentType.BOOTS, 3
            ),
            5,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            4.0F,
            0.2F,
            REPAIRS_ABYSSIUM_ARMOR,
            ABYSSIUM_ARMOR_MATERIAL_KEY
    );
}
