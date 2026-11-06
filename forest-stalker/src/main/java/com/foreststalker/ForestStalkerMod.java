package com.foreststalker;

import com.foreststalker.command.CommandStalkerInfo;
import com.foreststalker.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

/**
 * Main mod class.
 *
 * Mod id: foreststalker
 * Minecraft: 1.12.2 / Forge 14.23.5.2860
 */
@Mod(
        modid = ForestStalkerMod.MODID,
        name = ForestStalkerMod.NAME,
        version = ForestStalkerMod.VERSION,
        acceptedMinecraftVersions = "[1.12,1.12.2]",
        useMetadata = true
)
public class ForestStalkerMod {

    public static final String MODID = "foreststalker";
    public static final String NAME = "Forest Stalker";
    public static final String VERSION = "2.0.0";

    @Mod.Instance(MODID)
    public static ForestStalkerMod instance;

    @SidedProxy(clientSide = "com.foreststalker.proxy.ClientProxy", serverSide = "com.foreststalker.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("Starting " + NAME + " " + VERSION);

        ModConfig.load(event.getSuggestedConfigurationFile());
        ModEntities.registerEntities();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ModEntities.registerSpawns();
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandStalkerInfo());
    }
}
