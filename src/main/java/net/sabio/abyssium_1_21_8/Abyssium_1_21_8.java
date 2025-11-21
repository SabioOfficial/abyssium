package net.sabio.abyssium_1_21_8;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.sabio.abyssium_1_21_8.block.ModBlocks;
import net.sabio.abyssium_1_21_8.entity.EndermanSentryEntity;
import net.sabio.abyssium_1_21_8.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class Abyssium_1_21_8 implements ModInitializer {
    public static final String MOD_ID = "abyssium_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final double SPEED_MULTIPLIER = 1.25;
    private static final double MAX_HORIZONTAL_SPEED = 0.81;
    private static final double FORWARD_START_BOOST = 0.05;

    public static final RegistryKey<PlacedFeature> ORE_SINGLE_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("abyssium_mod", "ore_abyssium_ore_single"));

    public static final RegistryKey<PlacedFeature> ORE_CLUMP_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("abyssium_mod", "ore_abyssium_ore_clump"));

    public static final RegistryKey<PlacedFeature> END_STONE_INFESTED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("abyssium_mod", "end_stone_infested"));

    public static <T extends Entity>EntityType<T> registerEntity(String namespace, String id, EntityType.Builder<T> type) {
        RegistryKey<EntityType<?>> registryKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(namespace, id));
        return Registry.register(Registries.ENTITY_TYPE, registryKey, type.build(registryKey));
    }

//    public static final EntityType<EndermanSentryEntity> ENDERMAN_SENTRY = Registry.register(
//            Registries.ENTITY_TYPE,
//            Identifier.of("abyssium_mod", "enderman_sentry"),
//            EntityType.Builder.create(EndermanSentryEntity::new, SpawnGroup.MONSTER)
//                    .dimensions(0.6F, 2.9F)
//                    .eyeHeight(2.55F)
//                    .passengerAttachments(2.80625F)
//                    .maxTrackingRange(8)
//                    .build()
//    );

    public static final EntityType<EndermanSentryEntity> ENDERMAN_SENTRY = registerEntity(
            "abyssium_mod",
            "enderman_sentry",
            EntityType.Builder.create(EndermanSentryEntity::new, SpawnGroup.MONSTER)
                .dimensions(0.6F, 2.9F)
                .eyeHeight(2.55F)
                .passengerAttachments(2.80625F)
                .maxTrackingRange(8)
    );

    private static void damageElytra(ItemStack stack, LivingEntity holder) {
        if (stack == null || stack.isEmpty()) return;

        try {
            Method m = stack.getClass().getMethod("damage", int.class, LivingEntity.class, EquipmentSlot.class);
            m.invoke(stack, 1, holder, EquipmentSlot.CHEST);
            if (stack.isEmpty() && holder instanceof PlayerEntity p) {
                p.sendEquipmentBreakStatus(stack.getItem(), EquipmentSlot.CHEST);
            }
            return;
        } catch (Throwable ignored) {/*stay empty my child*/}

        try {
            Method m = stack.getClass().getMethod("damage", int.class, LivingEntity.class, Consumer.class);
            Consumer<LivingEntity> consumer = (LivingEntity e) -> {
                if (e instanceof PlayerEntity p) p.sendEquipmentBreakStatus(stack.getItem(), EquipmentSlot.CHEST);
            };
            m.invoke(stack, 1, holder, consumer);
            return;
        } catch (Throwable ignored) {/*stay empty my child*/}

        try {
            Method getD = stack.getClass().getMethod("getDamage");
            Method setD = stack.getClass().getMethod("setDamage", int.class);
            Object curObj = getD.invoke(stack);
            int cur = (curObj instanceof Integer) ? (Integer) curObj : 0;
            int next = cur + 1;
            int max = stack.getMaxDamage();
            setD.invoke(stack, next);
            if (next >= max && holder instanceof PlayerEntity p) {
                try {
                    Method setCount = stack.getClass().getMethod("setCount", int.class);
                    setCount.invoke(stack, 0);
                } catch (Throwable ignored2) {}
                p.sendEquipmentBreakStatus(stack.getItem(), EquipmentSlot.CHEST);
            }
            return;
        } catch (Throwable ignored) {/*stay empty my child*/}
    }

    private static void sendEquipmentUpdatePacket(ServerPlayerEntity player, LivingEntity target, EquipmentSlot slot, ItemStack stackSnapshot) {
        try {
            Class<?> cls = Class.forName("net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket");
            Constructor<?> ctor = cls.getConstructor(int.class, List.class);
            Pair<EquipmentSlot, ItemStack> pair = Pair.of(slot, stackSnapshot);
            Object packet = ctor.newInstance(target.getId(), List.of(pair));
            player.networkHandler.sendPacket((Packet<?>) packet);
            return;
        } catch (Throwable e) {/*stay empty my child*/}

        try {
            Class<?> alt = Class.forName("net.minecraft.network.packet.s2c.play.EntityEquipmentPacket"); // hypothetical alternative uwu
            Constructor<?> ctorAlt = alt.getConstructor(int.class, List.class);
            Pair<EquipmentSlot, ItemStack> pair = Pair.of(slot, stackSnapshot);
            Object pkt = ctorAlt.newInstance(target.getId(), List.of(pair));
            player.networkHandler.sendPacket((Packet<?>) pkt);
            return;
        } catch (Throwable ignored) {/*stay empty my child*/}
    }

//    private static void sendVelocityPacket(ServerPlayerEntity player, LivingEntity target) {
//        try {
//            Class<?> cls = Class.forName("net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket");
//            Constructor<?> ctor = cls.getConstructor(LivingEntity.class);
//            Object packet = ctor.newInstance(target);
//            player.networkHandler.sendPacket((Packet<?>) packet);
//            return;
//        } catch (Throwable e) {/*stay empty my child*/}
//
//        try {
//            Class<?> alt = Class.forName("net.minecraft.network.packet.s2c.play.EntityVelocityS2CPacket");
//            Constructor<?> ctor = alt.getConstructor(LivingEntity.class);
//            Object packet = ctor.newInstance(target);
//            player.networkHandler.sendPacket((Packet<?>) packet);
//        } catch (Throwable ignored) {}
//    }

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModBlocks.initialize();
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_SINGLE_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_CLUMP_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_DECORATION, END_STONE_INFESTED_KEY);

        FabricDefaultAttributeRegistry.register(ENDERMAN_SENTRY, EndermanSentryEntity.createEndermanSentryAttributes());

        EntityElytraEvents.CUSTOM.register((LivingEntity entity, boolean tickElytra) -> {
            ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);

            if (chest == null || chest.isEmpty() || chest.getItem() != ModItems.ABYSSIUM_PLATED_ELYTRA) {
                return false;
            }

            boolean isFlying = false;
            if (entity instanceof PlayerEntity player) {
                try {
                    isFlying = player.isGliding();
                } catch (NoSuchMethodError ignored) {
                    try {
                        String poseName = player.getPose().name();
                        isFlying = poseName.equalsIgnoreCase("GLIDING") || poseName.toLowerCase(Locale.ROOT).contains("glid");
                    } catch (Throwable ignored2) {
                        isFlying = false;
                    }
                }
            } else {
                try {
                    String poseName = entity.getPose().name();
                    isFlying = poseName.equalsIgnoreCase("GLIDING") || poseName.toLowerCase(java.util.Locale.ROOT).contains("glid");
                } catch (Throwable ignored) {
                    isFlying = false;
                }
            }

            if (!isFlying) return true; // wow optimized code omg

            if (entity.age % 20 == 0) {
                Item captured = chest.getItem();

                chest.damage(1, entity, EquipmentSlot.CHEST);

                if (!entity.getWorld().isClient && entity instanceof ServerPlayerEntity serverPlayer) {
                    Pair<EquipmentSlot, ItemStack> pair = Pair.of(EquipmentSlot.CHEST, chest.copy());
                    serverPlayer.networkHandler.sendPacket(new EntityEquipmentUpdateS2CPacket(entity.getId(), List.of(pair)));
                }

                if (chest.isEmpty() && entity instanceof PlayerEntity player) {
                    player.sendEquipmentBreakStatus(captured, EquipmentSlot.CHEST);
                }
            }

            Vec3d vel = entity.getVelocity();
            double curHor = Math.sqrt(vel.x * vel.x + vel.z * vel.z);

            double multiplier = Math.max(0.0, SPEED_MULTIPLIER);
            double cap = (MAX_HORIZONTAL_SPEED > 0.0) ? MAX_HORIZONTAL_SPEED : Double.POSITIVE_INFINITY;
            double startBoost = Math.max(0.0, FORWARD_START_BOOST);
            double lerpFactor = 0.06;
            double horizontalDrag = 0.993;
            double downwardCompFactor = 0.02;

            double vanillaTarget = curHor * multiplier;
            if (curHor < 0.05 && startBoost > 0.0) vanillaTarget = Math.max(vanillaTarget, startBoost); // wowie more optimized lines??!?!?!?!
            if (vanillaTarget > cap) vanillaTarget = cap;

            double newHor = curHor + (vanillaTarget - curHor) * lerpFactor;
            newHor *= horizontalDrag;

            double yawRad = Math.toRadians(entity.getYaw());
            double pitchRad = Math.toRadians(entity.getPitch());
            double lx = -Math.sin(yawRad) * Math.cos(pitchRad);
            double lz =  Math.cos(yawRad) * Math.cos(pitchRad);
            Vec3d lookHoriz = new Vec3d(lx, 0.0D, lz);
            Vec3d forward;
            if (lookHoriz.length() < 1e-6) {
                if (curHor > 1e-6) forward = new Vec3d(vel.x / curHor, 0.0D, vel.z / curHor);
                else forward = new Vec3d(0.0D, 0.0D, 1.0D);
            } else {
                forward = lookHoriz.normalize();
            }

            double newX = forward.x * newHor;
            double newZ = forward.z * newHor;
            double newY = vel.y;

            double horDiff = newHor - curHor;
            if (horDiff > 0.01) newY -= horDiff * downwardCompFactor;
            if (newY > 1.0) newY = 1.0;

            entity.setVelocity(newX, newY, newZ);

            if (!entity.getWorld().isClient && entity instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
            }

            return true;
        });
    }
}
