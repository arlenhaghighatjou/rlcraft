package me.ichun.mods.ichunutil.client.module.tabula.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;

public class ModelRotationPoint extends ModelBase {
   public ModelRenderer cube1;
   public ModelRenderer cube2;
   public ModelRenderer cube3;

   public ModelRotationPoint() {
      float size = 1.0F;
      this.cube1 = new ModelRenderer(this, 0, 0);
      this.cube1.addBox(-(size / 2.0F), -(size / 2.0F), -(size / 2.0F), (int)size, (int)size, (int)size);
      this.cube1.rotateAngleX = (float)Math.toRadians(45.0D);
      this.cube2 = new ModelRenderer(this, 0, 0);
      this.cube2.addBox(-(size / 2.0F), -(size / 2.0F), -(size / 2.0F), (int)size, (int)size, (int)size);
      this.cube2.rotateAngleY = (float)Math.toRadians(45.0D);
      this.cube3 = new ModelRenderer(this, 0, 0);
      this.cube3.addBox(-(size / 2.0F), -(size / 2.0F), -(size / 2.0F), (int)size, (int)size, (int)size);
      this.cube3.rotateAngleZ = (float)Math.toRadians(45.0D);
   }

   public void render(float f5) {
      this.cube1.render(f5);
      this.cube2.render(f5);
      this.cube3.render(f5);
   }

   public void destroy() {
      if (this.cube1.compiled) {
         GLAllocation.deleteDisplayLists(this.cube1.displayList);
      }

      if (this.cube2.compiled) {
         GLAllocation.deleteDisplayLists(this.cube2.displayList);
      }

      if (this.cube3.compiled) {
         GLAllocation.deleteDisplayLists(this.cube3.displayList);
      }

   }
}
