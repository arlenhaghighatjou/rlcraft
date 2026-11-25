package com.oblivioussp.spartanshields.client.model;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelShield;

public class ModelShieldKite extends ModelShield {
   public ModelRenderer plateLeft;
   public ModelRenderer plateRight;

   public ModelShieldKite() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.plate = new ModelRenderer(this);
      this.plate.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.plate.cubeList.add(new ModelBox(this.plate, 0, 0, -6.0F, -11.0F, -2.0F, 12, 22, 1, 0.0F, false));
      this.handle = new ModelRenderer(this);
      this.handle.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.handle.cubeList.add(new ModelBox(this.handle, 26, 0, -1.0F, -3.0F, -1.0F, 2, 6, 6, 0.0F, false));
      this.plateLeft = new ModelRenderer(this);
      this.plateLeft.setRotationPoint(-6.0F, 0.0F, 0.0F);
      this.setRotationAngle(this.plateLeft, 0.0F, 0.0F, -0.1963F);
      this.plateLeft.cubeList.add(new ModelBox(this.plateLeft, 52, 0, -1.85F, -10.788F, -1.99F, 4, 20, 1, 0.0F, false));
      this.plateRight = new ModelRenderer(this);
      this.plateRight.setRotationPoint(6.0F, 0.0F, 0.0F);
      this.setRotationAngle(this.plateRight, 0.0F, 0.0F, 0.1963F);
      this.plateRight.cubeList.add(new ModelBox(this.plateRight, 42, 0, -2.15F, -10.788F, -1.99F, 4, 20, 1, 0.0F, false));
   }

   public void render() {
      this.plate.render(0.0625F);
      this.plateLeft.render(0.0625F);
      this.plateRight.render(0.0625F);
      this.handle.render(0.0625F);
   }

   protected void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
