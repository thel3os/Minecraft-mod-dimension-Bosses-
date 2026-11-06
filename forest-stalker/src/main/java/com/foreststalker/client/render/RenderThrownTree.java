package com.foreststalker.client.render;

import com.foreststalker.ForestStalkerMod;
import com.foreststalker.client.model.ModelThrownTree;
import com.foreststalker.entity.EntityThrownTree;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Draws the flying tree, aligned with its flight direction and slowly spinning
 * around its own trunk.
 */
public class RenderThrownTree extends Render<EntityThrownTree> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ForestStalkerMod.MODID, "textures/entity/stalker.png");

    private final ModelThrownTree model = new ModelThrownTree();

    public RenderThrownTree(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.6F;
    }

    @Override
    public void doRender(EntityThrownTree entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        this.bindEntityTexture(entity);

        float yaw = entity.prevRotationYaw
                + MathHelper.wrapDegrees(entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        double horizontal = Math.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ);
        float tilt = (float) Math.toDegrees(Math.atan2(horizontal, entity.motionY));

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(tilt, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(entity.ticksExisted * 14.0F + partialTicks * 14.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(1.15F, 1.15F, 1.15F);
        this.model.render(entity, 0.0F, 0.0F, entity.ticksExisted, 0.0F, 0.0F, 0.0625F);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityThrownTree entity) {
        return TEXTURE;
    }
}
