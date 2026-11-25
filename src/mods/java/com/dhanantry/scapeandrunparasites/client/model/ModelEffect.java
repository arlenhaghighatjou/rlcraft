package com.dhanantry.scapeandrunparasites.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public abstract class ModelEffect extends ModelSRP {
   public ModelEffect() {
   }

   public void renderTwo(ModelRenderer in, float ageInTicks, float f5, float width, float height, double n1, float n2, double n3, double n4) {
      double f1 = n1 + (double)MathHelper.sin(ageInTicks * n2) * n3 + n4;
      GlStateManager.pushMatrix();
      GlStateManager.translate(in.offsetX, in.offsetY, in.offsetZ);
      GlStateManager.translate(in.rotationPointX * f5, in.rotationPointY * f5, in.rotationPointZ * f5);
      GlStateManager.scale(f1 + (double)width, f1 + (double)height, f1 + (double)width);
      GlStateManager.translate(-in.offsetX, -in.offsetY, -in.offsetZ);
      GlStateManager.translate(-in.rotationPointX * f5, -in.rotationPointY * f5, -in.rotationPointZ * f5);
      in.render(f5);
      GlStateManager.popMatrix();
   }
}
