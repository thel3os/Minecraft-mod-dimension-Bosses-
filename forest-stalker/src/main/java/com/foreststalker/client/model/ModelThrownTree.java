package com.foreststalker.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * A small flying tree used by the thrown-tree projectile.
 * The tree is vertical (along +Y) and centred on the projectile position.
 */
public class ModelThrownTree extends ModelBase {

    private final ModelRenderer trunk;
    private final ModelRenderer upperCrown;
    private final ModelRenderer lowerCrown;

    public ModelThrownTree() {
        this.textureWidth = 128;
        this.textureHeight = 128;

        this.trunk = new ModelRenderer(this, 80, 48);
        this.trunk.setTextureSize(128, 128);
        this.trunk.addBox(-2.5F, -13.0F, -2.5F, 5, 26, 5);

        this.upperCrown = new ModelRenderer(this, 80, 0);
        this.upperCrown.setTextureSize(128, 128);
        this.upperCrown.addBox(-6.0F, 1.0F, -6.0F, 12, 12, 12);

        this.lowerCrown = new ModelRenderer(this, 80, 26);
        this.lowerCrown.setTextureSize(128, 128);
        this.lowerCrown.addBox(-4.5F, -8.0F, -4.5F, 9, 8, 9);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.trunk.render(scale);
        this.upperCrown.render(scale);
        this.lowerCrown.render(scale);
    }
}
