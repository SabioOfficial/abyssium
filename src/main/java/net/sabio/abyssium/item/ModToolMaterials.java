package net.sabio.abyssium.item;

import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public class ModToolMaterials {
    public static final ToolMaterial ABYSSIUM_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2525,
            10.0F,
            5.0F,
            22,
            ModArmorMaterials.REPAIRS_ABYSSIUM_ARMOR
    );
}
