package net.sabio.abyssium_1_21_8.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.sabio.abyssium_1_21_8.Abyssium_1_21_8;
import net.sabio.abyssium_1_21_8.entity.EndermanSentryEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleStructurePiece.class)
public abstract class EndCitySentrySpawnMixin {

    @Unique
    protected BlockBox boundingBox;

    @Inject(
            method = "generate",
            at = @At("TAIL")
    )
    private void onGenerate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot, CallbackInfo ci) {
        boolean isEndCityPiece = (Object)this instanceof EndCityGenerator.Piece;

        if (!isEndCityPiece) {
            return;
        }

        if (this.boundingBox == null) {
            return;
        }

        boolean intersects = this.boundingBox.intersects(chunkBox);
        if (!intersects) return;

        EntityType<EndermanSentryEntity> type = Abyssium_1_21_8.ENDERMAN_SENTRY;
        if (type == null) {
            return;
        }

        BlockPos spawnPos = new BlockPos(
                (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2,
                this.boundingBox.getMinY() + 1,
                (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2
        );

        BlockPos actualSpawnPos = findValidGround(world, spawnPos, this.boundingBox);
        if (actualSpawnPos == null) {
            return;
        }

        try {
            EndermanSentryEntity sentry;
            try {
                sentry = new EndermanSentryEntity(type, world.toServerWorld());
            } catch (Throwable t) {
                try {
                    sentry = type.create(world.toServerWorld(), null);
                } catch (Throwable t2) {
                    sentry = null;
                }
            }

            if (sentry == null) {
                return;
            }

            sentry.refreshPositionAndAngles(
                    actualSpawnPos.getX() + 0.5D,
                    actualSpawnPos.getY(),
                    actualSpawnPos.getZ() + 0.5D,
                    random.nextFloat() * 360.0F,
                    0.0F
            );

            sentry.setHomePos(actualSpawnPos);
            try {
                sentry.initialize(world, world.getLocalDifficulty(actualSpawnPos), SpawnReason.STRUCTURE, null);
            } catch (Throwable ignored) {}

            try {
                world.spawnEntity(sentry);
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {}
    }

    @Unique
    private BlockPos findValidGround(StructureWorldAccess world, BlockPos startPos, BlockBox boundingBox) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int y = startPos.getY(); y >= boundingBox.getMinY(); y--) {
            mutablePos.set(startPos.getX(), y, startPos.getZ());
            if (world.getBlockState(mutablePos).isSolidBlock(world, mutablePos)) {
                BlockPos potentialSpawn = mutablePos.up();
                if (world.getBlockState(potentialSpawn).isAir() && world.getBlockState(potentialSpawn.up()).isAir()) {
                    return potentialSpawn.toImmutable();
                }
            }
        }

        return startPos;
    }
}
