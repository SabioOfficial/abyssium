package net.sabio.abyssium_1_21_8.world;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.structure.Structure;
import net.sabio.abyssium_1_21_8.Abyssium_1_21_8;
import net.sabio.abyssium_1_21_8.entity.EndermanSentryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class EndCitySentryChunkHandler {
    private static final Queue<PendingChunk> pendingQueue = new ConcurrentLinkedQueue<>();

    private static final Map<Long, Long> lastSpawnTimeByChunk = new ConcurrentHashMap<>();
    private static final long COOLDOWN_MS = 5_000L;

    private static final int SENTRIES_PER_PIECE = 4;

    private static final int MAX_PROCESS_PER_TICK = 24;

    public static volatile boolean ENABLED = true;

    public static void register() {
        ServerChunkEvents.CHUNK_LOAD.register((ServerWorld world, WorldChunk chunk) -> {
            if (!ENABLED) return;
            pendingQueue.add(new PendingChunk(world, chunk.getPos().x, chunk.getPos().z));
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!ENABLED) return;

            int processedThisTick = 0;
            while (processedThisTick < MAX_PROCESS_PER_TICK) {
                PendingChunk p = pendingQueue.poll();
                if (p == null) break;
                processedThisTick++;

                try {
                    ServerWorld world = p.world;
                    int chunkX = p.chunkX;
                    int chunkZ = p.chunkZ;

                    if (world == null || world.getServer() == null) continue;

                    long chunkKey = (((long) chunkX) << 32) | (chunkZ & 0xffffffffL);
                    long now = System.currentTimeMillis();
                    Long last = lastSpawnTimeByChunk.get(chunkKey);
                    if (last != null && now - last < COOLDOWN_MS) {
                        continue;
                    }

                    WorldChunk chunk = world.getChunk(chunkX, chunkZ);
                    if (chunk == null) continue;

                    Map<Structure, StructureStart> starts = chunk.getStructureStarts();
                    if (starts == null || starts.isEmpty()) continue;

                    boolean spawnedAnyInChunk = false;

                    for (StructureStart start : starts.values()) {
                        if (start == null || !start.hasChildren()) continue;

                        List<StructurePiece> pieces = start.getChildren();
                        if (pieces == null || pieces.isEmpty()) continue;

                        for (StructurePiece piece : pieces) {
                            try {
                                if (!(piece instanceof EndCityGenerator.Piece)) continue;

                                BlockBox box = piece.getBoundingBox();
                                if (box == null) continue;

                                List<BlockPos> candidates = computeCandidates(box, world);

                                for (BlockPos cand : candidates) {
                                    if (cand == null) continue;

                                    BlockPos spawnPos = findValidGround(world, cand, box);
                                    if (spawnPos == null) continue;

                                    Box checkBox = new Box(
                                            spawnPos.getX() - 3.0, spawnPos.getY() - 2.0, spawnPos.getZ() - 3.0,
                                            spawnPos.getX() + 3.0, spawnPos.getY() + 2.0, spawnPos.getZ() + 3.0
                                    );

                                    List<EndermanSentryEntity> existing = world.getEntitiesByClass(EndermanSentryEntity.class, checkBox, e -> true);
                                    if (!existing.isEmpty()) continue;

                                    EntityType<EndermanSentryEntity> type = Abyssium_1_21_8.ENDERMAN_SENTRY;
                                    if (type == null) continue;

                                    EndermanSentryEntity sentry = null;
                                    try {
                                        sentry = new EndermanSentryEntity(type, world);
                                    } catch (Throwable t1) {
                                        try {
                                            sentry = type.create(world, null);
                                        } catch (Throwable ignored) {}
                                    }

                                    if (sentry == null) continue;

                                    float yaw = world.random.nextFloat() * 360.0F;
                                    sentry.refreshPositionAndAngles(
                                            spawnPos.getX() + 0.5D,
                                            spawnPos.getY(),
                                            spawnPos.getZ() + 0.5D,
                                            yaw,
                                            0.0F
                                    );

                                    sentry.setHomePos(spawnPos);
                                    try {
                                        sentry.initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.STRUCTURE, null);
                                    } catch (Throwable ignored) {}

                                    try {
                                        world.spawnEntity(sentry);
                                        spawnedAnyInChunk = true;
                                    } catch (Throwable ignored) {}
                                }
                            } catch (Throwable ignored) {}
                        }
                    }

                    if (spawnedAnyInChunk) {
                        lastSpawnTimeByChunk.put(chunkKey, now);
                    }
                } catch (Throwable ignored) {}
            }
        });
    }

    private static List<BlockPos> computeCandidates(BlockBox box, ServerWorld world) {
        List<BlockPos> list = new ArrayList<>(SENTRIES_PER_PIECE);
        int minX = box.getMinX(), maxX = box.getMaxX();
        int minZ = box.getMinZ(), maxZ = box.getMaxZ();
        int centerX = (minX + maxX) / 2;
        int centerZ = (minZ + maxZ) / 2;
        int baseY = box.getMinY() + 1;

        list.add(new BlockPos(centerX, baseY, centerZ));

        int width = Math.max(1, maxX - minX);
        int depth = Math.max(1, maxZ - minZ);
        int dx = Math.max(3, width / 4);
        int dz = Math.max(3, depth / 4);

        if (SENTRIES_PER_PIECE >= 2) list.add(new BlockPos(centerX + dx, baseY, centerZ));
        if (SENTRIES_PER_PIECE >= 3) list.add(new BlockPos(centerX - dx, baseY, centerZ));
        if (SENTRIES_PER_PIECE >= 4) list.add(new BlockPos(centerX, baseY, centerZ + dz));
        if (SENTRIES_PER_PIECE >= 5) list.add(new BlockPos(centerX, baseY, centerZ - dz));
        if (SENTRIES_PER_PIECE >= 6) {
            list.add(new BlockPos(centerX + dx, baseY, centerZ + dz));
        }

        return list;
    }

    private static BlockPos findValidGround(ServerWorld world, BlockPos startPos, BlockBox boundingBox) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int y = startPos.getY(); y >= boundingBox.getMinY(); y--) {
            mutablePos.set(startPos.getX(), y, startPos.getZ());
            if (world.getBlockState(mutablePos).isSolidBlock(world, mutablePos)) {
                BlockPos potential = mutablePos.up();
                if (world.getBlockState(potential).isAir() && world.getBlockState(potential.up()).isAir()) {
                    return potential.toImmutable();
                }
            }
        }

        for (int y = startPos.getY(); y <= Math.min(startPos.getY() + 10, boundingBox.getMaxY()); y++) {
            mutablePos.set(startPos.getX(), y, startPos.getZ());
            if (world.getBlockState(mutablePos).isSolidBlock(world, mutablePos)) {
                BlockPos potential = mutablePos.up();
                if (world.getBlockState(potential).isAir() && world.getBlockState(potential.up()).isAir()) {
                    return potential.toImmutable();
                }
            }
        }

        return null;
    }

    private static final class PendingChunk {
        final ServerWorld world;
        final int chunkX;
        final int chunkZ;

        PendingChunk(ServerWorld world, int chunkX, int chunkZ) {
            this.world = world;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }
    }
}
