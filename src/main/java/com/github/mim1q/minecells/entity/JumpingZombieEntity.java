package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.ai.goal.AnimatedMeleeAttackGoal;
import com.github.mim1q.minecells.entity.interfaces.AnimatedMeleeAttackEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class JumpingZombieEntity extends HostileEntity implements IAnimatable, AnimatedMeleeAttackEntity {
    AnimationFactory factory = new AnimationFactory(this);

    public JumpingZombieEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    public static final TrackedData<String> ATTACK_STATE = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.STRING);

    @Override
    public void initGoals() {
        this.goalSelector.add(1, new LookAroundGoal(this));
        this.goalSelector.add(1, new WanderAroundGoal(this, 1.0d));
        this.goalSelector.add(1, new WanderAroundFarGoal(this, 1.0d));

        this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, false));

        this.goalSelector.add(1, new AnimatedMeleeAttackGoal<>(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(ATTACK_STATE, "none");
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "movementController", 10, this::movementPredicate));
        data.addAnimationController(new AnimationController<>(this, "attackController", 0, this::attackPredicate));
    }

    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        boolean isMoving = event.getLimbSwingAmount() > 0.05f || event.getLimbSwingAmount() < -0.05f;

        if(isMoving)
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.walking"));
        else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.idle"));
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState attackPredicate(AnimationEvent<E> event) {

        if (this.getAttackState().equals("melee")) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.attack.melee"));
            return PlayState.CONTINUE;
        }

        event.getController().markNeedsReload();
        return PlayState.CONTINUE;
    }


    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public static DefaultAttributeContainer.Builder createJumpingZombieAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3d)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 30.0d)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0d)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0d)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0d);
    }

    @Override
    public int getAttackTickCount(String attackName) {
        return 15;
    }

    @Override
    public void setAttackState(String attackName) {
        this.dataTracker.set(ATTACK_STATE, attackName);
    }

    @Override
    public String getAttackState() {
        return this.dataTracker.get(ATTACK_STATE);
    }

    @Override
    public int getAttackCooldown(String attackName) {
        return 30;
    }

    @Override
    public void stopAnimations() {
        setAttackState("none");
    }

}
