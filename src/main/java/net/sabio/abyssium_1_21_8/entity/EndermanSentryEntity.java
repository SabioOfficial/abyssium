package net.sabio.abyssium_1_21_8.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.EnumSet;

public class EndermanSentryEntity extends HostileEntity {
    private boolean guarding = false;

    private int guardTeleportCooldown;
    private int attackTeleportCooldown;

    public static final int GUARD_TELEPORT_TICKS = 20 * 30;
    private static final int ATTACK_TELEPORT_TICKS = 20 * 7;

    private BlockPos homePos = null;
    private static final double MAX_HOME_DISTANCE = 50.0D;
    private static final double MAX_HOME_DISTANCE_SQ = MAX_HOME_DISTANCE * MAX_HOME_DISTANCE;

    public EndermanSentryEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        Random r = this.random;
        this.guardTeleportCooldown = r.nextInt(GUARD_TELEPORT_TICKS);
        this.attackTeleportCooldown = r.nextInt(ATTACK_TELEPORT_TICKS);
    }

    public void setHomePos(BlockPos pos) {
        this.homePos = pos;
    }

    public BlockPos getHomePos() {
        return this.homePos;
    }

    public static DefaultAttributeContainer.Builder createEndermanSentryAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 55.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.35F)
                .add(EntityAttributes.ATTACK_DAMAGE, 8.5)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0)
                .add(EntityAttributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.targetSelector.add(1, new SentryTargetGoal(this, 8.0D, 4.0D));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            boolean hasTarget = this.getTarget() != null;

            if (hasTarget && this.guarding) {
                this.guarding = false;
            } else if (!hasTarget && !this.guarding) {
                this.guarding = true;
            }

            if (this.guardTeleportCooldown > 0) this.guardTeleportCooldown--;
            if (this.attackTeleportCooldown > 0) this.attackTeleportCooldown--;

            if (this.homePos != null) {
                double homeDistSq = this.squaredDistanceTo(this.homePos.getX() + 0.5, this.homePos.getY(), this.homePos.getZ() + 0.5);
                if (homeDistSq > MAX_HOME_DISTANCE_SQ) {
                    this.getNavigation().startMovingTo(this.homePos.getX() + 0.5, this.homePos.getY(), this.homePos.getZ() + 0.5, 1.0D);
                }
            }

            if (this.guarding && this.guardTeleportCooldown <= 0 && this.getWorld().getRegistryKey() == World.END) {
                attemptRandomTeleport(8.0D, 3);
                this.guardTeleportCooldown = GUARD_TELEPORT_TICKS + this.random.nextInt(40);
            }

            if (hasTarget && this.attackTeleportCooldown <= 0 && this.getTarget() instanceof PlayerEntity target) {
                if (target.isAlive() && target.getWorld() == this.getWorld()) {
                    attemptTeleportNearTarget(target, 1.0D, 3);
                    this.attackTeleportCooldown = ATTACK_TELEPORT_TICKS + this.random.nextInt(20);
                }
            }
        }
    }

    private void attemptRandomTeleport(double radius, int attempts) {
        double cx = this.getX();
        double cy = this.getY();
        double cz = this.getZ();
        Random r = this.random;

        for (int i = 0; i < attempts; i++) {
            double dx = (r.nextDouble() * 2.0 - 1.0) * radius;
            double dy = (r.nextDouble() * 2.0 - 1.0) * radius;
            double dz = (r.nextDouble() * 2.0 - 1.0) * radius;

            double tx = cx + dx;
            double ty = Math.max(1.0D, cy + dy);
            double tz = cz + dz;

            if (this.homePos != null) {
                double dxh = tx - (this.homePos.getX() + 0.5);
                double dyh = ty - (this.homePos.getY() + 0.5);
                double dzh = tz - (this.homePos.getZ() + 0.5);
                double distSq = dxh * dxh + dyh * dyh + dzh * dzh;
                if (distSq > MAX_HOME_DISTANCE_SQ) continue;
            }

            if (this.teleportToSafe(tx, ty, tz)) {
                return;
            }
        }
    }

    private void attemptTeleportNearTarget(PlayerEntity target, double nearRadius, int attempts) {
        double px = target.getX();
        double py = target.getY();
        double pz = target.getZ();
        Random r = this.random;

        for (int i = 0; i < attempts; i++) {
            double angle = r.nextDouble() * Math.PI * 2.0;
            double distance = nearRadius * (0.5 + r.nextDouble() * 0.5);
            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;
            double tx = px + offsetX;
            double ty = py + 0.5D + r.nextDouble() * 1.5D;
            double tz = pz + offsetZ;

            if (this.homePos != null) {
                double dxh = tx - (this.homePos.getX() + 0.5);
                double dyh = ty - (this.homePos.getY() + 0.5);
                double dzh = tz - (this.homePos.getZ() + 0.5);
                double distSq = dxh * dxh + dyh * dyh + dzh * dzh;
                if (distSq > MAX_HOME_DISTANCE_SQ) continue;
            }

            if (this.teleportToSafe(tx, ty, tz)) {
                return;
            }
        }
    }

    private boolean teleportToSafe(double x, double y, double z) {
        return this.teleport(x, y, z, true);
    }

    public void addAdditionalSaveData(NbtCompound nbt) {
        if (this.homePos != null) {
            nbt.putInt("HomeX", this.homePos.getX());
            nbt.putInt("HomeY", this.homePos.getY());
            nbt.putInt("HomeZ", this.homePos.getZ());
        }
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.homePos != null) {
            nbt.putInt("HomeX", this.homePos.getX());
            nbt.putInt("HomeY", this.homePos.getY());
            nbt.putInt("HomeZ", this.homePos.getZ());
        }
    }

    public void readAdditionalSaveData(NbtCompound nbt) {
        if (nbt.contains("HomeX")) {
            int x = nbt.getInt("HomeX", 0);
            int y = nbt.getInt("HomeY", 0);
            int z = nbt.getInt("HomeZ", 0);
            this.homePos = new BlockPos(x, y, z);
        } else {
            this.homePos = null;
        }
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("HomeX")) {
            int x = nbt.getInt("HomeX", 0);
            int y = nbt.getInt("HomeY", 0);
            int z = nbt.getInt("HomeZ", 0);
            this.homePos = new BlockPos(x, y, z);
        } else {
            this.homePos = null;
        }
    }

    public boolean isGuarding() {
        return this.guarding;
    }

    private static class SentryTargetGoal extends ActiveTargetGoal<PlayerEntity> {
        private final EndermanSentryEntity owner;
        private final double sightRange;
        private final double listenRange;
        private PlayerEntity selectedPlayer;

        public SentryTargetGoal(EndermanSentryEntity owner, double sightRange, double listenRange) {
            super(owner, PlayerEntity.class, true);
            this.owner = owner;
            this.sightRange = sightRange;
            this.listenRange = listenRange;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (this.owner.getTarget() != null) return false;
            var box = this.owner.getBoundingBox().expand(this.sightRange, 2.0D, this.sightRange);
            for (PlayerEntity player : this.owner.getWorld().getEntitiesByClass(PlayerEntity.class, box, p -> !p.isSpectator())) {
                if (player.isSpectator() || player.isCreative()) continue;
                double dSq = this.owner.squaredDistanceTo(player);
                if (dSq <= this.listenRange * this.listenRange) {
                    if (player.getVelocity().lengthSquared() < 0.01D) {
                        this.selectedPlayer = player;
                        return true;
                    }
                }

                if (dSq <= this.sightRange * this.sightRange && this.owner.canSee(player)) {
                    this.selectedPlayer = player;
                    return true;
                }
            }
            this.selectedPlayer = null;
            return false;
        }

        @Override
        public void start() {
            if (this.selectedPlayer != null) {
                this.owner.setTarget(this.selectedPlayer);
            }
            super.start();
        }

        @Override
        public boolean shouldContinue() {
            if (this.owner.getTarget() == null) return false;
            if (!(this.owner.getTarget() instanceof PlayerEntity p)) return false;
            if (!p.isAlive()) return false;
            double maxKeepRange = this.sightRange * 1.5D;
            return !(this.owner.squaredDistanceTo(p) > maxKeepRange * maxKeepRange);
        }

        @Override
        public void stop() {
            this.selectedPlayer = null;
            super.stop();
        }
    }
}