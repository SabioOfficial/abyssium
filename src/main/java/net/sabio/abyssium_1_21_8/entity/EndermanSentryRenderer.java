package net.sabio.abyssium_1_21_8.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.sabio.abyssium_1_21_8.client.Abyssium_1_21_8Client;

public class EndermanSentryRenderer extends MobEntityRenderer<EndermanSentryEntity, EndermanSentryEntityRenderState, EndermanSentryEntityModel> {
//    public EndermanSentryRenderer(EntityRendererFactory.Context context, ModelPart part) {
//        super(context, new EndermanSentryEntityModel(root), 0.5F);
//        // this.addFeature(new EndermanEyesFeatureRenderer(this));
//        // this.addFeature(new EndermanBlockFeatureRenderer(this, context.getBlockRenderManager()));
//    }

    public EndermanSentryRenderer(EntityRendererFactory.Context context, ModelPart part) {
        super(context, new EndermanSentryEntityModel(context.getPart(Abyssium_1_21_8Client.ENDERMAN_SENTRY_LAYER)), 0.5F);
    }

    @Override
    public Identifier getTexture(EndermanSentryEntityRenderState state) {
        return Identifier.ofVanilla("textures/entity/enderman/enderman.png");
    }

    @Override
    public EndermanSentryEntityRenderState createRenderState() {
        return new EndermanSentryEntityRenderState();
    }
}
