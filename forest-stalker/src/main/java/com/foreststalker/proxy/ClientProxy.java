package com.foreststalker.proxy;

import com.foreststalker.ForestStalkerMod;
import com.foreststalker.ModItems;
import com.foreststalker.client.render.RenderStalker;
import com.foreststalker.client.render.RenderThrownTree;
import com.foreststalker.entity.EntityStalker;
import com.foreststalker.entity.EntityThrownTree;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Client-only setup: entity renderers and item models.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        RenderingRegistry.registerEntityRenderingHandler(EntityStalker.class, RenderStalker::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownTree.class, RenderThrownTree::new);
    }

    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent event) {
        registerItemModel(ModItems.STALKER_BLADE, "stalker_blade");
        registerItemModel(ModItems.STALKER_EGG, "stalker_egg");
    }

    private static void registerItemModel(net.minecraft.item.Item item, String name) {
        ModelLoader.setCustomModelResourceLocation(
                item,
                0,
                new ModelResourceLocation(new ResourceLocation(ForestStalkerMod.MODID, name), "inventory")
        );
    }
}
