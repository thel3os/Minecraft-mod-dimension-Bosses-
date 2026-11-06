package com.foreststalker.entity;

import com.foreststalker.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * A whole tree thrown by the Forest Stalker or by a player wielding the Tree Blade.
 * Deals {@link ModConfig#treeDamage} (10 = 5 hearts) on impact.
 */
public class EntityThrownTree extends EntityThrowable {

    public EntityThrownTree(World worldIn) {
        super(worldIn);
        this.setSize(1.1F, 1.1F);
    }

    public EntityThrownTree(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        this.setSize(1.1F, 1.1F);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.02F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // Point the tree along its flight path.
        if (this.ticksExisted > 1) {
            double horizontal = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            this.rotationPitch = (float) (MathHelper.atan2(this.motionY, horizontal) * (180D / Math.PI));
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null && result.entityHit != this.getThrower()
                && result.entityHit instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase) result.entityHit;
            target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()),
                    (float) ModConfig.treeDamage);

            double dx = this.motionX;
            double dz = this.motionZ;
            double length = MathHelper.sqrt(dx * dx + dz * dz);
            if (length > 0.0001D) {
                target.motionX += dx / length * 0.9D;
                target.motionZ += dz / length * 0.9D;
            }
            target.motionY += 0.35D;
            target.velocityChanged = true;
        }

        if (!this.world.isRemote) {
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.NEUTRAL, 1.4F, 0.6F);

            int stateId = Block.getStateId(Blocks.LOG.getDefaultState());
            for (int i = 0; i < 14; i++) {
                this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                        this.posX, this.posY + 0.3D, this.posZ,
                        0.6D, 0.6D, 0.6D, stateId);
            }

            this.setDead();
        }
    }
}
