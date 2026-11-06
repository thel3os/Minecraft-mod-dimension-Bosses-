package com.foreststalker.item;

import com.foreststalker.ForestStalkerMod;
import com.foreststalker.ModItems;
import com.foreststalker.entity.EntityStalker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A simple spawn egg for the Forest Stalker (handy for testing and for map makers).
 */
public class ItemStalkerEgg extends Item {

    public ItemStalkerEgg() {
        this.setRegistryName(ForestStalkerMod.MODID, "stalker_egg");
        this.setUnlocalizedName(ForestStalkerMod.MODID + ".stalker_egg");
        this.setCreativeTab(ModItems.TAB);
        this.setMaxStackSize(64);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);

        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        }

        BlockPos spawnPos = pos.offset(facing);
        EntityStalker stalker = new EntityStalker(worldIn);
        stalker.setLocationAndAngles(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                worldIn.rand.nextFloat() * 360.0F, 0.0F);

        if (!worldIn.isAirBlock(spawnPos) || !worldIn.getCollisionBoxes(stalker, stalker.getEntityBoundingBox()).isEmpty()) {
            return EnumActionResult.FAIL;
        }

        worldIn.spawnEntity(stalker);
        stalker.onInitialSpawn(worldIn.getDifficultyForLocation(spawnPos), null);

        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }
}
