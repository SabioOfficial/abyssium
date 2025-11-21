package net.sabio.abyssium_1_21_8.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.EnumSet;

public class EndermanSentryEntity extends HostileEntity {
    private boolean guarding = false;

    public EndermanSentryEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
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
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2D, true));
        this.targetSelector.add(1, new SentryTargetGoal(this, 8.0D, 4.0D));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        super.initGoals();
    }

    @Override
    public void tick() {
        super.tick();

        boolean hasTarget = this.getTarget() != null;

        if (hasTarget && this.guarding) {
            this.guarding = false;
        } else if (!hasTarget && !this.guarding) {
            this.guarding = true;
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
                if (player.isSpectator()) continue;
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
