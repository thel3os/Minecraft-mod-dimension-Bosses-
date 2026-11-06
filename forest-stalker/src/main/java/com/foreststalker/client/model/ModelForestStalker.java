package com.foreststalker.client.model;

import com.foreststalker.entity.EntityStalker;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Procedural model of the Forest Stalker boss.
 *
 * The creature is a hunched giant: a huge torso, a raised hump on its back
 * and three small trees growing out of that hump. The whole model is built
 * from boxes and textured with one 128x128 atlas (bark, leaves, wood, eyes).
 */
public class ModelForestStalker extends ModelBase {

    private final ModelRenderer body;
    private final ModelRenderer hump;
    private final ModelRenderer head;
    private final ModelRenderer jaw;
    private final ModelRenderer rightEye;
    private final ModelRenderer leftEye;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;

    private final ModelRenderer[] trunks = new ModelRenderer[3];
    private final ModelRenderer[] crowns = new ModelRenderer[3];

    public ModelForestStalker() {
        this.textureWidth = 128;
        this.textureHeight = 128;

        // Massive forward-leaning torso.
        this.body = part(0, 0, 0.0F, 0.0F, 0.0F);
        this.body.addBox(-10.0F, 0.0F, -7.0F, 20, 12, 14);

        // The hump on the back.
        this.hump = part(0, 32, 0.0F, 0.0F, 0.0F);
        this.hump.addBox(-9.0F, -7.0F, 2.0F, 18, 9, 10);

        // Small head jutting out low in front of the hump.
        this.head = part(0, 56, 0.0F, 2.0F, -5.0F);
        this.head.addBox(-5.0F, -2.0F, -8.0F, 10, 8, 8);

        this.jaw = part(40, 56, 0.0F, 2.0F, -5.0F);
        this.jaw.addBox(-4.0F, 6.0F, -9.0F, 8, 3, 7);

        // Glowing eyes use their own tiny texture region.
        this.rightEye = part(100, 100, 0.0F, 2.0F, -5.0F);
        this.rightEye.addBox(-4.0F, 2.0F, -8.5F, 2, 2, 1);
        this.leftEye = part(100, 100, 0.0F, 2.0F, -5.0F);
        this.leftEye.addBox(2.0F, 2.0F, -8.5F, 2, 2, 1);

        // Long ape-like arms.
        this.rightArm = part(0, 80, -11.0F, 2.0F, 0.0F);
        this.rightArm.addBox(-2.0F, 0.0F, -2.0F, 4, 18, 4);
        this.leftArm = part(0, 80, 11.0F, 2.0F, 0.0F);
        this.leftArm.addBox(-2.0F, 0.0F, -2.0F, 4, 18, 4);

        // Thick legs.
        this.rightLeg = part(40, 80, -4.0F, 12.0F, 0.0F);
        this.rightLeg.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6);
        this.leftLeg = part(40, 80, 4.0F, 12.0F, 0.0F);
        this.leftLeg.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6);

        // Three trees growing out of the hump: {x, z, tilt}.
        float[][] treePositions = {
                {-6.0F, 7.0F, -0.15F},
                {0.0F, 8.5F, 0.0F},
                {6.0F, 7.0F, 0.18F}
        };

        for (int i = 0; i < treePositions.length; i++) {
            float x = treePositions[i][0];
            float z = treePositions[i][1];
            float tilt = treePositions[i][2];

            ModelRenderer trunk = part(80, 48, x, -2.0F, z);
            trunk.addBox(-1.5F, -12.0F, -1.5F, 3, 13, 3);
            trunk.rotateAngleZ = tilt;
            this.trunks[i] = trunk;

            ModelRenderer crown = part(80, 0, x, -2.0F, z);
            crown.addBox(-5.0F, -17.0F, -5.0F, 10, 7, 10);
            crown.rotateAngleZ = tilt;
            this.crowns[i] = crown;
        }
    }

    private ModelRenderer part(int u, int v, float x, float y, float z) {
        ModelRenderer part = new ModelRenderer(this, u, v);
        part.setTextureSize(128, 128);
        part.setRotationPoint(x, y, z);
        return part;
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        this.body.render(scale);
        this.hump.render(scale);
        this.head.render(scale);
        this.jaw.render(scale);
        this.rightEye.render(scale);
        this.leftEye.render(scale);
        this.rightArm.render(scale);
        this.leftArm.render(scale);
        this.rightLeg.render(scale);
        this.leftLeg.render(scale);

        for (int i = 0; i < this.trunks.length; i++) {
            this.trunks[i].render(scale);
            this.crowns[i].render(scale);
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        float headYaw = netHeadYaw * 0.017453292F;
        float headPitchRad = headPitch * 0.017453292F;

        this.head.rotateAngleY = headYaw;
        this.head.rotateAngleX = headPitchRad;
        this.jaw.rotateAngleY = headYaw;
        this.jaw.rotateAngleX = headPitchRad;
        this.rightEye.rotateAngleY = headYaw;
        this.leftEye.rotateAngleY = headYaw;
        this.rightEye.rotateAngleX = headPitchRad;
        this.leftEye.rotateAngleX = headPitchRad;

        float swing = MathHelper.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
        this.rightArm.rotateAngleX = -swing;
        this.leftArm.rotateAngleX = swing;
        this.rightArm.rotateAngleY = 0.0F;
        this.rightLeg.rotateAngleX = swing;
        this.leftLeg.rotateAngleX = -swing;

        if (entityIn instanceof EntityStalker) {
            int throwTicks = ((EntityStalker) entityIn).getThrowTicks();
            if (throwTicks > 0) {
                float raise = Math.min(1.0F, throwTicks / 12.0F);
                this.rightArm.rotateAngleX = -2.6F * raise;
                this.rightArm.rotateAngleY = 0.45F * raise;
            }
        }
    }
}
