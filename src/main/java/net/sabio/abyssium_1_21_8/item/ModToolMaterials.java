package net.sabio.abyssium_1_21_8.item;

import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public class ModToolMaterials {
    public static final ToolMaterial ABYSSIUM_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2750,
            10.5F,
            5.5F,
            26,
            ModArmorMaterials.REPAIRS_ABYSSIUM_ARMOR
    );
}