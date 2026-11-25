package com.oblivioussp.spartanshields.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelShield;

public class ModelShieldTower extends ModelShield {
   public ModelRenderer plateLeft;
   public ModelRenderer plateRight;

   public ModelShieldTower() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.plate = new ModelRenderer(this, 0, 0);
      this.plate.addBox(-6.0F, -11.0F, -2.0F, 12, 22, 1, 0.0F);
      this.handle = new ModelRenderer(this, 26, 0);
      this.handle.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 6, 0.0F);
      this.plateLeft = new ModelRenderer(this, 42, 0);
      this.plateLeft.setRotationPoint(-6.0F, 0.0F, -2.0F);
      this.plateLeft.rotateAngleY = 45.0F;
      this.plateLeft.addBox(-2.0F, -11.0F, 0.0F, 2, 22, 1, 0.0F);
      this.plateRight = new ModelRenderer(this, 48, 0);
      this.plateRight.setRotationPoint(6.0F, 0.0F, -2.0F);
      this.plateRight.rotateAngleY = -45.0F;
      this.plateRight.addBox(0.0F, -11.0F, 0.0F, 2, 22, 1, 0.0F);
   }

   public void render() {
      this.plate.render(0.0625F);
      this.plateLeft.render(0.0625F);
      this.plateRight.render(0.0625F);
      this.handle.render(0.0625F);
   }
}
