//package net.sabio.abyssium_1_21_8.world.sentry;
//
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.math.BlockPos;
//import net.sabio.abyssium_1_21_8.entity.EndermanSentryEntity;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public final class SentryCoordinator {
//    private static final Map<UUID, SentryData> sentryByUuid = new ConcurrentHashMap<>();
//    private static final Map<ServerWorld, List<UUID>> sentriesByWorld = new ConcurrentHashMap<>();
//
//    public static void registerSentry(ServerWorld world, EndermanSentryEntity sentry) {
//        if (world == null || sentry == null) return;
//        UUID id = sentry.getUuid();
//        SentryData existing = sentryByUuid.get(id);
//        if (existing != null) return;
//        BlockPos home = sentry.getHomePos();
//        if (home == null) return;
//        SentryData data = new SentryData(home, computePatrolPoints(world, home));
//        sentryByUuid.put(id, data);
//        sentriesByWorld.computeIfAbsent(world, w -> new ArrayList<>()).add(id);
//    }
//
//    public static BlockPos getPatrolTarget(EndermanSentryEntity sentry) {
//        if (sentry == null) return null;
//        SentryData data = sentryByUuid.get(sentry.getUuid());
//        if (data == null) {
//            registerSentry((ServerWorld) sentry.getWorld(), sentry);
//            data = sentryByUuid.get(sentry.getUuid());
//            if (data == null) return null;
//        }
//        BlockPos[] pts = data.points;
//        if (pts == null || pts.length == 0) return null;
//        int idx = Math.abs(data.counter.getAndIncrement()) % pts.length;
//        return pts[idx];
//    }
//
//    public static void deregisterSentry(EndermanSentryEntity sentry) {
//        if (sentry == null) return;
//        UUID id = sentry.getUuid();
//        SentryData removed = sentryByUuid.remove(id);
//        if (removed == null) return;
//        ServerWorld world = (ServerWorld) sentry.getWorld();
//        List<UUID> list = sentriesByWorld.get(world);
//        if (list != null) list.remove(id);
//    }
//
//    private static BlockPos[] computePatrolPoints(ServerWorld world, BlockPos home) {
//        int floors = 3;
//        int pointsPerFloor = 6;
//        List<BlockPos> pts = new ArrayList<>(floors * pointsPerFloor);
//        ThreadLocalRandom rnd = ThreadLocalRandom.current();
//        for (int f = 0; f < floors; f++) {
//            int y = home.getY() + f * 4;
//            int radiusBase = 5 + f * 3;
//            for (int i = 0; i < pointsPerFloor; i++) {
//                double angle = (Math.PI * 2.0 * i) / pointsPerFloor + rnd.nextDouble(-0.2, 0.2);
//                int rx = (int) Math.round(Math.cos(angle) * radiusBase);
//                int rz = (int) Math.round(Math.sin(angle) * radiusBase);
//                BlockPos attempt = new BlockPos(home.getX() + rx, y, home.getZ() + rz);
//                pts.add(attempt);
//            }
//        }
//        return pts.toArray(new BlockPos[0]);
//    }
//
//    private static final class SentryData {
//        final BlockPos home;
//        final BlockPos[] points;
//        final AtomicInteger counter = new AtomicInteger();
//
//        SentryData(BlockPos home, BlockPos[] points) {
//            this.home = home;
//            this.points = points;
//            this.counter.set(ThreadLocalRandom.current().nextInt());
//        }
//    }
//}
