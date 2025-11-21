package net.sabio.abyssium_1_21_8.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

public class EndermanSentryEntityRenderState extends BipedEntityRenderState {
    public BlockState carriedBlock;
    public boolean angry;

    public EndermanSentryEntityRenderState() {
        super();
    }
}