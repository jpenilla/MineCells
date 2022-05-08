package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.world.MineCellsExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class KamikazeEntity extends MineCellsEntity {

    private static final TrackedData<Integer> FUSE = DataTracker.registerData(KamikazeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public KamikazeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 0, true);
        this.setNoGravity(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FUSE, -1);
        this.setAttackState("sleeping");
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(3, new KamikazeFlyGoal(this, 1.0D));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));

        this.goalSelector.add(0, new KamikazeAttackGoal(this, 1.0D, 3.0D));
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setPos(this.getX(), this.getY() + 0.25D, this.getZ());
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void tick() {
        super.tick();
        int fuse = this.getFuse();
        if (fuse == 0 && this.isAlive()) {
            this.explode(4.0F);
            this.discard();
        }
        System.out.println(fuse);
        this.setFuse(fuse - 1);
    }

    public void explode(float power) {
        if (!this.world.isClient()) {
            MineCellsExplosion.explode((ServerWorld)this.world, this, this.getPos(), power);
        }
    }

    public int getFuse() {
        return this.dataTracker.get(FUSE);
    }

    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    public static DefaultAttributeContainer.Builder createKamikazeAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 3.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 15.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 2.0D);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation navigation = new BirdNavigation(this, world);
        navigation.setCanPathThroughDoors(false);
        navigation.setCanSwim(false);
        navigation.setCanEnterOpenDoors(true);
        return navigation;
    }

    public static class KamikazeFlyGoal extends FlyGoal {

        protected final KamikazeEntity entity;

        public KamikazeFlyGoal(KamikazeEntity entity, double d) {
            super(entity, d);
            this.entity = entity;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !this.entity.getAttackState().equals("sleeping");
        }
    }

    public static class KamikazeAttackGoal extends Goal {

        protected final KamikazeEntity entity;
        protected final double speed;
        protected final double distance;

        public KamikazeAttackGoal(KamikazeEntity entity, double speed, double distance) {
            this.entity = entity;
            this.speed = speed;
            this.distance = distance;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return this.entity.getTarget() != null;
        }

        @Override
        public boolean shouldContinue() {
            return this.entity.getTarget() != null && this.entity.getTarget().isAlive();
        }

        @Override
        public void start() {
            this.entity.setAttackState("attack");
        }

        @Override
        public void stop() {
            this.entity.setAttackState("none");
        }

        @Override
        public void tick() {
            Entity target = this.entity.getTarget();
            if (target != null) {
                Vec3d pos = target.getPos();
                this.entity.getMoveControl().moveTo(pos.x, pos.y + 1.5D, pos.z, this.speed);
                this.entity.getLookControl().lookAt(target);
                if (this.entity.distanceTo(target) <= this.distance && this.entity.getFuse() < 0) {
                    this.entity.setFuse(30);
                }
            }
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }
    }
}
