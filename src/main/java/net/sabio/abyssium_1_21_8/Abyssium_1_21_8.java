package net.sabio.abyssium_1_21_8;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.sabio.abyssium_1_21_8.block.ModBlocks;
import net.sabio.abyssium_1_21_8.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModBlocks.initialize();
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_SINGLE_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_CLUMP_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_DECORATION, END_STONE_INFESTED_KEY);

        EntityElytraEvents.CUSTOM.register((LivingEntity entity, boolean tickElytra) -> {
            ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);

            if (chest == null || chest.isEmpty() || chest.getItem() != ModItems.ABYSSIUM_PLATED_ELYTRA) {
                return false;
            }

            boolean isFlying = false;
            if (entity instanceof PlayerEntity player) {
                isFlying = player.isGliding();
            } else {
                try {
                    String poseName = entity.getPose().name();
                    isFlying = poseName.equalsIgnoreCase("GLIDING") || poseName.toLowerCase(java.util.Locale.ROOT).contains("glid");
                } catch (Throwable ignored) {
                    isFlying = false;
                }
            }

            if (!isFlying) {
                return true;
            }

            if (tickElytra && !entity.getWorld().isClient) {
                if (entity.age % 20 == 0) {
                    Item captured = chest.getItem();
                    chest.damage(1, entity, EquipmentSlot.CHEST);
                    if (chest.isEmpty() && entity instanceof PlayerEntity player) {
                        player.sendEquipmentBreakStatus(captured, EquipmentSlot.CHEST);
                    }
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

            if (curHor < 0.05 && startBoost > 0.0) {
                vanillaTarget = Math.max(vanillaTarget, startBoost);
            }

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
            if (horDiff > 0.01) {
                newY -= horDiff * downwardCompFactor;
            }

            if (newY > 1.0) newY = 1.0;

            entity.setVelocity(newX, newY, newZ);

            if (!entity.getWorld().isClient && entity instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
            }

            return true;
        });
    }
}
