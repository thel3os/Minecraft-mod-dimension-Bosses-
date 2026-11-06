package com.foreststalker;

import com.foreststalker.item.ItemStalkerBlade;
import com.foreststalker.item.ItemStalkerEgg;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Holds every item of the mod and registers them on the item registry event.
 */
@Mod.EventBusSubscriber(modid = ForestStalkerMod.MODID)
public final class ModItems {

    public static final CreativeTabs TAB = new CreativeTabs(ForestStalkerMod.MODID) {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(STALKER_BLADE);
        }
    };

    public static final ItemStalkerBlade STALKER_BLADE = new ItemStalkerBlade();
    public static final ItemStalkerEgg STALKER_EGG = new ItemStalkerEgg();

    private ModItems() {
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<net.minecraft.item.Item> event) {
        event.getRegistry().register(STALKER_BLADE);
        event.getRegistry().register(STALKER_EGG);
    }
}
