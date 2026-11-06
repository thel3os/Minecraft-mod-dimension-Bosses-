package com.foreststalker.client.render;

import com.foreststalker.ForestStalkerMod;
import com.foreststalker.client.model.ModelForestStalker;
import com.foreststalker.entity.EntityStalker;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * Renders the hunchbacked Forest Stalker boss with its custom model.
 */
public class RenderStalker extends RenderLiving<EntityStalker> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ForestStalkerMod.MODID, "textures/entity/stalker.png");

    /** The model is about 2.75 blocks tall at scale 1.0; this makes the boss ~4.6 blocks tall. */
    private static final float MODEL_SCALE = 1.7F;

    public RenderStalker(RenderManager renderManager) {
        super(renderManager, new ModelForestStalker(), 1.6F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityStalker entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityStalker entity, float partialTickTime) {
        GlStateManager.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
    }
}
