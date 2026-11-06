package com.foreststalker.entity.ai;

import com.foreststalker.ModConfig;
import com.foreststalker.entity.EntityStalker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * Makes the boss stop at range and hurl a tree at its target.
 *
 * The task does not use any mutex bits, so the melee AI can keep walking
 * towards the target while the boss is throwing.
 */
public class EntityAITreeThrow extends EntityAIBase {

    private final EntityStalker stalker;
    private int cooldown;
    private int seeTime;

    public EntityAITreeThrow(EntityStalker stalker) {
        this.stalker = stalker;
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.stalker.getAttackTarget();
        return target != null && target.isEntityAlive();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void startExecuting() {
        this.cooldown = 25;
        this.seeTime = 0;
    }

    @Override
    public void resetTask() {
        this.seeTime = 0;
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = this.stalker.getAttackTarget();
        if (target == null) {
            return;
        }

        double distanceSq = this.stalker.getDistanceSq(target);
        boolean canSee = this.stalker.getEntitySenses().canSee(target);
        if (canSee) {
            this.seeTime++;
        } else {
            this.seeTime = 0;
        }

        this.stalker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);

        if (this.cooldown > 0) {
            this.cooldown--;
        }

        if (this.cooldown <= 0 && this.seeTime >= 8
                && distanceSq > 20.0D && distanceSq < 32.0D * 32.0D) {
            this.stalker.throwTreeAt(target);
            this.cooldown = Math.max(20, ModConfig.bossThrowCooldown);
            this.seeTime = 0;
        }
    }
}
