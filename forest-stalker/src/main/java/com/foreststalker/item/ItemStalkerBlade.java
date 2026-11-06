package com.foreststalker.item;

import com.foreststalker.ForestStalkerMod;
import com.foreststalker.ModConfig;
import com.foreststalker.ModItems;
import com.foreststalker.entity.EntityThrownTree;
import com.google.common.collect.Multimap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The Tree Blade dropped by the Forest Stalker.
 *
 * The melee damage is not baked into the sword material: it is applied through
 * the ATTACK_DAMAGE attribute modifier so that the value in the config is always
 * the real, final damage shown in the tooltip (30 by default).
 *
 * Right-clicking hurls a whole tree, which deals {@link ModConfig#treeDamage}
 * (10 = 5 hearts) to whatever it hits.
 */
public class ItemStalkerBlade extends ItemSword {

    public ItemStalkerBlade() {
        super(ToolMaterial.DIAMOND);
        this.setRegistryName(ForestStalkerMod.MODID, "stalker_blade");
        this.setUnlocalizedName(ForestStalkerMod.MODID + ".stalker_blade");
        this.setCreativeTab(ModItems.TAB);
        this.setMaxDamage(2031);
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> modifiers = super.getItemAttributeModifiers(equipmentSlot);
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            modifiers.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
            modifiers.put(
                    SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", getBladeDamage() - 1.0D, 0)
            );
        }
        return modifiers;
    }

    /** Final melee damage of the blade when held by a player with the default 1.0 base damage. */
    public static double getBladeDamage() {
        return Math.max(1.0D, ModConfig.bladeDamage);
    }

    @Override
    public float getAttackDamage() {
        return (float) (getBladeDamage() - 1.0D);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (playerIn.getCooldownTracker().hasCooldown(this)) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        if (!worldIn.isRemote) {
            Vec3d look = playerIn.getLookVec();
            EntityThrownTree tree = new EntityThrownTree(worldIn, playerIn);
            tree.shoot(look.x, look.y, look.z, 1.55F, 1.0F);
            worldIn.spawnEntity(tree);

            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.9F, 0.55F);
        }

        playerIn.getCooldownTracker().setCooldown(this, Math.max(0, ModConfig.playerThrowCooldown));
        playerIn.swingArm(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public int getItemEnchantability() {
        return 25;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("item." + ForestStalkerMod.MODID + ".stalker_blade.tooltip"));
        tooltip.add(TextFormatting.GRAY + I18n.format("item." + ForestStalkerMod.MODID + ".stalker_blade.tooltip2"));
    }
}
