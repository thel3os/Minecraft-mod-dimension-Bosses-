package com.foreststalker;

import com.foreststalker.entity.EntityStalker;
import com.foreststalker.entity.EntityThrownTree;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Registers the stalker entity, its thrown trees and natural spawning.
 */
public final class ModEntities {

    private static int entityId = 0;

    private ModEntities() {
    }

    public static void registerEntities() {
        EntityRegistry.registerModEntity(
                new ResourceLocation(ForestStalkerMod.MODID, "stalker"),
                EntityStalker.class,
                "stalker",
                entityId++,
                ForestStalkerMod.instance,
                64,   // tracking range
                3,    // update frequency
                true  // send velocity updates
        );

        EntityRegistry.registerModEntity(
                new ResourceLocation(ForestStalkerMod.MODID, "thrown_tree"),
                EntityThrownTree.class,
                "thrown_tree",
                entityId++,
                ForestStalkerMod.instance,
                64,   // tracking range
                5,    // update frequency
                true  // send velocity updates
        );
    }

    /** Called in postInit, when the biome dictionary is fully populated. */
    public static void registerSpawns() {
        if (ModConfig.spawnWeight <= 0) {
            return;
        }

        Biome[] forests = BiomeDictionary.getBiomes(BiomeDictionary.Type.FOREST).toArray(new Biome[0]);
        if (forests.length == 0) {
            ForestStalkerMod.logger.warn("No forest biomes were found, the stalker will not spawn naturally.");
            return;
        }

        EntityRegistry.addSpawn(
                EntityStalker.class,
                ModConfig.spawnWeight,
                ModConfig.minGroupSize,
                ModConfig.maxGroupSize,
                EnumCreatureType.MONSTER,
                forests
        );
        ForestStalkerMod.logger.info("Registered stalker spawning in " + forests.length + " forest biome(s).");
    }
}
