package com.foreststalker.entity;

import com.foreststalker.ModConfig;
import com.foreststalker.ModItems;
import com.foreststalker.entity.ai.EntityAITreeThrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * The Forest Stalker boss: a hulking, hunchbacked giant that carries a small
 * grove on its back and hurls whole trees at anything that enters the forest.
 *
 * <ul>
 *     <li>200 HP (100 hearts), 16 melee damage, 10 damage per thrown tree</li>
 *     <li>Throws trees at targets between 4.5 and 32 blocks away</li>
 *     <li>Shows a boss health bar to nearby players</li>
 *     <li>Always (by default) drops the Tree Blade on death</li>
 * </ul>
 */
public class EntityStalker extends EntityMob {

    /** Vanilla potions are stored as PotionType objects in 1.12.2, so grab the effect itself. */
    private static final Potion SLOWNESS = PotionTypes.SLOWNESS.getEffects().get(0).getPotion();

    /** Client-side animation timer for the throwing arm (synced once per throw). */
    private static final DataParameter<Integer> THROW_TICKS =
            EntityDataManager.createKey(EntityStalker.class, DataSerializers.VARINT);

    private final BossInfoServer bossInfo = new BossInfoServer(
            new TextComponentTranslation("entity.stalker.name"),
            BossInfo.Color.PINK,
            BossInfo.Overlay.NOTCHED_10);

    public EntityStalker(World worldIn) {
        super(worldIn);
        this.setSize(2.0F, 4.6F);
        this.stepHeight = 1.5F;
        this.experienceValue = 200;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(THROW_TICKS, 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ModConfig.bossHealth);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ModConfig.meleeDamage);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(6.0D);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAITreeThrow(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.15D, false));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 0.8D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 24.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote) {
            // The server only syncs the initial value; the client counts it down for the animation.
            int throwTicks = this.dataManager.get(THROW_TICKS);
            if (throwTicks > 0) {
                this.dataManager.set(THROW_TICKS, throwTicks - 1);
            }
            return;
        }

        if (this.ticksExisted % 20 == 0) {
            this.updateBossBar();
        }
    }

    /** Keeps the boss bar in sync with health and nearby players. Server side only. */
    private void updateBossBar() {
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());

        List<EntityPlayer> nearby = this.world.getPlayers(EntityPlayer.class,
                player -> player.isEntityAlive() && player.getDistanceSq(this) <= 64.0D * 64.0D);

        for (EntityPlayer player : nearby) {
            if (player instanceof EntityPlayerMP) {
                this.bossInfo.addPlayer((EntityPlayerMP) player);
            }
        }

        for (EntityPlayerMP player : new ArrayList<>(this.bossInfo.getPlayers())) {
            if (!player.isEntityAlive() || player.getDistanceSq(this) > 80.0D * 80.0D) {
                this.bossInfo.removePlayer(player);
            }
        }

        this.bossInfo.setVisible(!nearby.isEmpty());
    }

    /**
     * Shoots a tree at the given target. The little upward bias compensates
     * for gravity so the boss can actually hit players at long range.
     */
    public void throwTreeAt(EntityLivingBase target) {
        EntityThrownTree tree = new EntityThrownTree(this.world, this);

        double dx = target.posX - this.posX;
        double dz = target.posZ - this.posZ;
        double dy = target.posY + (double) target.height * 0.5D - tree.posY;
        double horizontal = MathHelper.sqrt(dx * dx + dz * dz);

        tree.shoot(dx, dy + horizontal * 0.10D, dz, 1.45F, 3.0F);
        this.world.spawnEntity(tree);

        this.dataManager.set(THROW_TICKS, 16);
        this.world.playSound(null, this.posX, this.posY, this.posZ,
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.HOSTILE, 1.6F, 0.45F);
    }

    public int getThrowTicks() {
        return this.dataManager.get(THROW_TICKS);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean hit = super.attackEntityAsMob(entityIn);
        if (hit && entityIn instanceof EntityLivingBase
                && this.rand.nextDouble() < ModConfig.slownessChance) {
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(SLOWNESS, 100, 1));
        }
        return hit;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if (this.rand.nextDouble() < ModConfig.bladeDropChance) {
            this.entityDropItem(new ItemStack(ModItems.STALKER_BLADE), 1.0F);
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!this.world.isRemote) {
            this.bossInfo.setVisible(false);
        }
    }

    @Override
    public void setDead() {
        super.setDead();
        if (!this.world.isRemote) {
            this.bossInfo.setVisible(false);
        }
    }

    @Override
    protected boolean isValidLightLevel() {
        // The boss only rises in the dark, but accepts a dark cave under a forest too.
        BlockPos pos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
        return this.world.getLightFromNeighbors(pos) <= this.rand.nextInt(8);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 1;
    }

    @Override
    protected boolean canDespawn() {
        // A boss should not vanish while the player is looking for it.
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    @Override
    protected float getSoundPitch() {
        return 0.4F;
    }

    @Override
    protected float getSoundVolume() {
        return 1.6F;
    }
}
