package net.sabio.abyssium.item;

import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.sabio.abyssium.Abyssium;

public class ModEquipmentAssetKeys {
    public static final RegistryKey<EquipmentAsset> ABYSSIUM_PLATED_ELYTRA = RegistryKey.of(
            EquipmentAssetKeys.REGISTRY_KEY,
            Identifier.of(Abyssium.MOD_ID, "abyssium_plated_elytra")
    );
}
