package com.foreststalker.command;

import com.foreststalker.ModConfig;
import com.foreststalker.entity.EntityStalker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;

/**
 * Small admin/debug command that prints the stats of the nearest Forest Stalker.
 * Usage: /stalkerinfo
 */
public class CommandStalkerInfo extends CommandBase {

    @Override
    public String getName() {
        return "stalkerinfo";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/stalkerinfo - stats of the nearest Forest Stalker";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();

        List<EntityStalker> stalkers = world.getEntities(EntityStalker.class, EntityStalker::isEntityAlive);
        if (stalkers.isEmpty()) {
            sender.sendMessage(new TextComponentString("No Forest Stalker is loaded in this world."));
            return;
        }

        Vec3d pos = sender.getPositionVector();
        stalkers.sort((a, b) -> Double.compare(a.getDistanceSq(pos.x, pos.y, pos.z),
                b.getDistanceSq(pos.x, pos.y, pos.z)));
        EntityStalker stalker = stalkers.get(0);
        double melee = stalker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();

        sender.sendMessage(new TextComponentString(String.format(Locale.ROOT,
                "Forest Stalker: HP %.1f/%.1f | melee %.1f | thrown tree %.1f | throw cooldown %d ticks | loaded bosses: %d",
                stalker.getHealth(), stalker.getMaxHealth(), melee,
                ModConfig.treeDamage, ModConfig.bossThrowCooldown, stalkers.size())));
    }
}
