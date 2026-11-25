package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelEffect;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBombOmboo extends ModelEffect {
   public ModelRenderer mainbody;
   public ModelRenderer dec;
   public ModelRenderer dec_1;
   public ModelRenderer dec_2;
   public ModelRenderer dec_3;
   public ModelRenderer dec_4;

   public ModelBombOmboo() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.mainbody = new ModelRenderer(this, 0, 0);
      this.mainbody.setRotationPoint(0.0F, 17.0F, 0.0F);
      this.mainbody.addBox(-3.5F, 0.0F, -5.0F, 7, 7, 10, 0.0F);
      this.dec = new ModelRenderer(this, 24, 0);
      this.dec.setRotationPoint(4.0F, 4.0F, 0.0F);
      this.dec.addBox(-2.0F, -2.0F, -3.0F, 4, 4, 6, 0.0F);
      this.setRotateAngle(this.dec, 0.0F, 0.0F, 0.43633232F);
      this.dec_2 = new ModelRenderer(this, 30, 14);
      this.dec_2.setRotationPoint(0.0F, 4.0F, -5.5F);
      this.dec_2.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
      this.setRotateAngle(this.dec_2, 0.4712389F, 0.0F, 0.0F);
      this.dec_1 = new ModelRenderer(this, 38, 4);
      this.dec_1.setRotationPoint(-4.0F, 4.0F, 0.0F);
      this.dec_1.addBox(-2.0F, -2.0F, -3.0F, 4, 4, 6, 0.0F);
      this.setRotateAngle(this.dec_1, 0.0F, 0.0F, -0.43633232F);
      this.dec_4 = new ModelRenderer(this, 0, 17);
      this.dec_4.setRotationPoint(0.0F, 0.3F, 0.0F);
      this.dec_4.addBox(-2.0F, -2.0F, -3.0F, 4, 4, 6, 0.0F);
      this.setRotateAngle(this.dec_4, 0.0F, 0.0F, 0.7853982F);
      this.dec_3 = new ModelRenderer(this, 46, 14);
      this.dec_3.setRotationPoint(0.0F, 4.0F, 5.5F);
      this.dec_3.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
      this.setRotateAngle(this.dec_3, -0.4712389F, 0.0F, 0.0F);
      this.mainbody.addChild(this.dec);
      this.mainbody.addChild(this.dec_2);
      this.mainbody.addChild(this.dec_1);
      this.mainbody.addChild(this.dec_4);
      this.mainbody.addChild(this.dec_3);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float f5) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5);
      this.renderTwo(this.mainbody, ageInTicks, f5, 1.0F, 1.0F, 0.1D, 0.8F, 0.05D, 0.1D);
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
   }
}
