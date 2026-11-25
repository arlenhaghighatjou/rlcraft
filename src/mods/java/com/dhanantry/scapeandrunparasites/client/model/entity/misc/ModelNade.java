package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelNade extends ModelSRP {
   public ModelRenderer mainbody;

   public ModelNade() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.mainbody = new ModelRenderer(this, 0, 0);
      this.mainbody.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.mainbody.addBox(-8.0F, -12.0F, -8.0F, 16, 16, 16, 0.0F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      double scale = 0.5D;
      GlStateManager.pushMatrix();
      GlStateManager.translate(this.mainbody.offsetX, this.mainbody.offsetY, this.mainbody.offsetZ);
      GlStateManager.translate(this.mainbody.rotationPointX * f5, this.mainbody.rotationPointY * f5, this.mainbody.rotationPointZ * f5);
      GlStateManager.scale(scale, scale, scale);
      GlStateManager.translate(-this.mainbody.offsetX, -this.mainbody.offsetY, -this.mainbody.offsetZ);
      GlStateManager.translate(-this.mainbody.rotationPointX * f5, -this.mainbody.rotationPointY * f5, -this.mainbody.rotationPointZ * f5);
      this.mainbody.render(f5);
      GlStateManager.popMatrix();
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      this.mainbody.offsetY = 0.14F;
   }
}
