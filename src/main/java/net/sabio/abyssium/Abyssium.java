package net.sabio.abyssium;

import net.fabricmc.api.ModInitializer;
import net.sabio.abyssium.item.ModItems;
import net.sabio.abyssium.block.ModBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Abyssium implements ModInitializer {
    public static final String MOD_ID = "abyssium_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModBlocks.initialize();
    }
}
