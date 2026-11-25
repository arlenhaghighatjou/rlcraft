package boni.dummy.client;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import boni.dummy.EntityDummy;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelDummy extends ModelBiped {
   public ModelRenderer standPlate;

   public ModelDummy(float modelSize) {
      this(modelSize, 0.0F);
   }

   public ModelDummy(float size, float p_i1149_2_) {
      this(size, p_i1149_2_, 64, 64);
   }

   public ModelDummy(float size, float p_i1149_2_, int xw, int yw) {
      super(size, p_i1149_2_, xw, yw);
      this.bipedRightArm = new ModelRenderer(this, 40, 16);
      this.bipedRightArm.addBox(-3.01F, 1.0F, -2.01F, 4, 8, 4, size);
      this.bipedRightArm.setRotationPoint(-2.5F, 2.0F + p_i1149_2_, 0.0F);
      this.bipedLeftArm = new ModelRenderer(this, 40, 16);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.addBox(-1.01F, 1.0F, -2.01F, 4, 8, 4, size);
      this.bipedLeftArm.setRotationPoint(2.5F, 2.0F + p_i1149_2_, 0.0F);
      this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
      this.bipedLeftLeg.addBox(-2.0F, -12.0F, -2.0F, 4, 12, 4, size);
      this.bipedLeftLeg.setRotationPoint(0.0F, 24.0F + p_i1149_2_, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 0, 0);
      this.standPlate = new ModelRenderer(this, 0, 32);
      this.standPlate.addBox(-8.0F, 11.5F, -8.0F, 16, 1, 16, size);
      this.standPlate.setRotationPoint(0.0F, 12.0F + p_i1149_2_, 0.0F);
      this.bipedBody = new ModelRenderer(this, 16, 16);
      this.bipedBody.addBox(-4.0F, -24.0F, -2.0F, 8, 12, 4, size);
      this.bipedBody.setRotationPoint(0.0F, 24.0F + p_i1149_2_, 0.0F);
      this.bipedHeadwear = new ModelRenderer(this, 32, 0);
      this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, size + 0.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      if (this.isChild) {
         float f6 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scale(1.5F / f6, 1.5F / f6, 1.5F / f6);
         GlStateManager.translate(0.0F, 16.0F * p_78088_7_, 0.0F);
         this.bipedHead.render(p_78088_7_);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
         GlStateManager.translate(0.0F, 24.0F * p_78088_7_, 0.0F);
         this.bipedBody.render(p_78088_7_);
         this.bipedRightArm.render(p_78088_7_);
         this.bipedLeftArm.render(p_78088_7_);
         this.standPlate.render(p_78088_7_);
         this.bipedLeftLeg.render(p_78088_7_);
         GlStateManager.popMatrix();
      } else {
         this.bipedHead.render(p_78088_7_);
         this.bipedBody.render(p_78088_7_);
         this.bipedRightArm.render(p_78088_7_);
         this.bipedLeftArm.render(p_78088_7_);
         this.standPlate.render(p_78088_7_);
         this.bipedLeftLeg.render(p_78088_7_);
         this.bipedHeadwear.render(p_78088_7_);
      }

   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      this.bipedHead.rotateAngleY = p_78087_4_ / 57.295776F;
      this.bipedHead.rotateAngleX = p_78087_5_ / 57.295776F;
      this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
      this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleX = 0.0F;
      this.bipedRightArm.rotateAngleX = 0.0F;
      this.bipedRightArm.rotateAngleY = 0.0F;
      this.bipedLeftArm.rotateAngleY = 0.0F;
      this.bipedBody.rotateAngleX = 0.0F;
      this.bipedHead.rotationPointY = 0.0F;
      this.bipedHeadwear.rotationPointY = 0.0F;
      this.bipedRightArm.rotateAngleZ = 1.5707964F;
      this.bipedLeftArm.rotateAngleZ = -1.5707964F;
      float phase = ((EntityDummy)p_78087_7_).shakeAnimation;
      float shake = ((EntityDummy)p_78087_7_).shake;
      float r = 0.0F;
      float r2 = 0.0F;
      if (shake > 0.0F) {
         r = (float)(-((double)MathHelper.sin(phase) * 3.141592653589793D / 100.0D * (double)shake));
         r2 = (float)((double)MathHelper.cos(phase) * 3.141592653589793D / 20.0D);
      }

      this.bipedLeftLeg.rotateAngleX = r / 2.0F;
      this.bipedBody.rotateAngleX = r / 2.0F;
      this.bipedLeftArm.rotateAngleX = r * 1.1F;
      this.bipedRightArm.rotateAngleX = r * 1.1F;
      this.bipedHead.rotateAngleX = -r;
      this.bipedHead.rotateAngleZ = r2;
   }
}
