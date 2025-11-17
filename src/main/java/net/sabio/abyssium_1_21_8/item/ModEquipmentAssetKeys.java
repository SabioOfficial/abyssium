package net.sabio.abyssium_1_21_8.item;

import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.sabio.abyssium_1_21_8.Abyssium_1_21_8;

public class ModEquipmentAssetKeys {
    public static final RegistryKey<EquipmentAsset> ABYSSIUM_PLATED_ELYTRA = RegistryKey.of(
            EquipmentAssetKeys.REGISTRY_KEY,
            Identifier.of(Abyssium_1_21_8.MOD_ID, "abyssium_plated_elytra")
    );
}
