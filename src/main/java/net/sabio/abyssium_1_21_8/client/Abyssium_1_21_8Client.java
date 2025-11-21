package net.sabio.abyssium_1_21_8.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.sabio.abyssium_1_21_8.Abyssium_1_21_8;
import net.sabio.abyssium_1_21_8.entity.EndermanSentryEntityModel;
import net.sabio.abyssium_1_21_8.entity.EndermanSentryRenderer;

@Environment(EnvType.CLIENT)
public class Abyssium_1_21_8Client implements ClientModInitializer {
    public static final EntityModelLayer ENDERMAN_SENTRY_LAYER =
            new EntityModelLayer(Identifier.of(Abyssium_1_21_8.MOD_ID, "enderman_sentry"), "main");

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(ENDERMAN_SENTRY_LAYER, EndermanSentryEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(Abyssium_1_21_8.ENDERMAN_SENTRY, (context) ->
                new EndermanSentryRenderer(context, context.getPart(ENDERMAN_SENTRY_LAYER))
        );
    }
}