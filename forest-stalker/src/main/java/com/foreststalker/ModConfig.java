package com.foreststalker;

import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.io.File;

/**
 * Simple Forge configuration. Everything is read once during preInit,
 * before entities are registered, so the values are safe to use everywhere.
 */
public final class ModConfig {

    public static final String CATEGORY_SPAWN = "spawning";
    public static final String CATEGORY_COMBAT = "combat";
    public static final String CATEGORY_GEAR = "gear";

    /** How often the boss is picked when a monster wants to spawn in a forest (0 = never). */
    public static int spawnWeight = 8;
    /** Minimum group size. A boss should walk alone. */
    public static int minGroupSize = 1;
    /** Maximum group size. */
    public static int maxGroupSize = 1;

    /** Health of the Forest Stalker boss. */
    public static double bossHealth = 200.0D;
    /** Damage of its melee attack (16 = 8 hearts). */
    public static double meleeDamage = 16.0D;
    /** Damage of one thrown tree (10 = 5 hearts). */
    public static double treeDamage = 10.0D;
    /** Ticks between the boss's tree throws (80 = 4 seconds). */
    public static int bossThrowCooldown = 80;
    /** Ticks between player tree throws with the Tree Blade (25 = 1.25 seconds). */
    public static int playerThrowCooldown = 25;

    /** Total attack damage of the Tree Blade, including the player's bare-hand damage. */
    public static double bladeDamage = 30.0D;
    /** Chance (0..1) that a killed boss drops its blade. */
    public static double bladeDropChance = 1.0D;
    /** Chance (0..1) that a melee hit applies Slowness to the victim. */
    public static double slownessChance = 0.35D;

    private ModConfig() {
    }

    public static void load(File file) {
        Configuration config = new Configuration(file);
        try {
            config.load();

            spawnWeight = config.getInt("spawnWeight", CATEGORY_SPAWN, spawnWeight, 0, 200,
                    "Spawn weight for forest biomes. Vanilla zombie is 100, so 8 is fairly rare. "
                            + "0 disables natural spawning.");
            minGroupSize = config.getInt("minGroupSize", CATEGORY_SPAWN, minGroupSize, 1, 16,
                    "Minimum amount of stalkers in a spawn group.");
            maxGroupSize = config.getInt("maxGroupSize", CATEGORY_SPAWN, maxGroupSize, 1, 16,
                    "Maximum amount of stalkers in a spawn group.");

            bossHealth = config.getFloat("bossHealth", CATEGORY_COMBAT, (float) bossHealth, 1.0F, 4096.0F,
                    "Health of the Forest Stalker boss (200 by default = 100 hearts).");
            meleeDamage = config.getFloat("meleeDamage", CATEGORY_COMBAT, (float) meleeDamage, 0.0F, 2048.0F,
                    "Melee attack damage of the boss (16 by default = 8 hearts).");
            treeDamage = config.getFloat("treeDamage", CATEGORY_COMBAT, (float) treeDamage, 0.0F, 2048.0F,
                    "Damage of one thrown tree (10 by default = 5 hearts).");
            bossThrowCooldown = config.getInt("bossThrowCooldown", CATEGORY_COMBAT, bossThrowCooldown, 10, 1200,
                    "Ticks between the boss's tree throws (20 ticks = 1 second).");
            playerThrowCooldown = config.getInt("playerThrowCooldown", CATEGORY_COMBAT, playerThrowCooldown, 0, 1200,
                    "Ticks between tree throws with the Tree Blade (20 ticks = 1 second).");

            bladeDamage = config.getFloat("bladeDamage", CATEGORY_GEAR, (float) bladeDamage, 1.0F, 2048.0F,
                    "Melee attack damage of the Tree Blade (30 by default).");
            bladeDropChance = config.getFloat("bladeDropChance", CATEGORY_GEAR, (float) bladeDropChance, 0.0F, 1.0F,
                    "Chance that the boss drops its blade on death.");
            slownessChance = config.getFloat("slownessChance", CATEGORY_GEAR, (float) slownessChance, 0.0F, 1.0F,
                    "Chance that a melee hit slows the victim down.");
        } catch (Exception e) {
            ForestStalkerMod.logger.log(Level.ERROR, "Failed to load the config file", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }

        if (minGroupSize > maxGroupSize) {
            minGroupSize = maxGroupSize;
        }
        if (ForestStalkerMod.logger != null) {
            ForestStalkerMod.logger.info("Config loaded: spawnWeight=" + spawnWeight
                    + ", group=" + minGroupSize + "-" + maxGroupSize
                    + ", bossHealth=" + bossHealth
                    + ", treeDamage=" + treeDamage
                    + ", bladeDamage=" + bladeDamage);
        }
    }
}
