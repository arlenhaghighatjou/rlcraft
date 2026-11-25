package vazkii.quark.world.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.world.entity.EntityFrog;

public class ModelFrog extends ModelBase {
   public final ModelRenderer headTop;
   public final ModelRenderer headBottom;
   public final ModelRenderer body;
   public final ModelRenderer rightArm;
   public final ModelRenderer leftArm;
   public final ModelRenderer rightEye;
   public final ModelRenderer leftEye;

   public ModelFrog() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.rightArm = new ModelRenderer(this, 33, 7);
      this.rightArm.mirror = true;
      this.rightArm.setRotationPoint(6.5F, 22.0F, 1.0F);
      this.rightArm.addBox(-1.0F, -1.0F, -5.0F, 3, 3, 6, 0.0F);
      this.leftArm = new ModelRenderer(this, 33, 7);
      this.leftArm.setRotationPoint(-6.5F, 22.0F, 1.0F);
      this.leftArm.addBox(-2.0F, -1.0F, -5.0F, 3, 3, 6, 0.0F);
      this.body = new ModelRenderer(this, 0, 7);
      this.body.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.body.addBox(-5.5F, -3.0F, 0.0F, 11, 7, 11, 0.0F);
      this.headTop = new ModelRenderer(this, 0, 0);
      this.headTop.setRotationPoint(0.0F, 18.0F, 0.0F);
      this.headTop.addBox(-5.5F, -1.0F, -5.0F, 11, 2, 5, 0.0F);
      this.headBottom = new ModelRenderer(this, 32, 0);
      this.headBottom.setRotationPoint(0.0F, 18.0F, 0.0F);
      this.headBottom.addBox(-5.5F, 1.0F, -5.0F, 11, 2, 5, 0.0F);
      this.rightEye = new ModelRenderer(this, 0, 0);
      this.rightEye.mirror = true;
      this.rightEye.setRotationPoint(0.0F, 18.0F, 0.0F);
      this.rightEye.addBox(1.5F, -1.5F, -4.0F, 1, 1, 1, 0.0F);
      this.leftEye = new ModelRenderer(this, 0, 0);
      this.leftEye.setRotationPoint(0.0F, 18.0F, 0.0F);
      this.leftEye.addBox(-2.5F, -1.5F, -4.0F, 1, 1, 1, 0.0F);
   }

   public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
      EntityFrog frog = (EntityFrog)entity;
      int rawTalkTime = frog.getTalkTime();
      this.headBottom.rotateAngleX = 0.02617994F;
      if (rawTalkTime != 0) {
         float talkTime = (float)rawTalkTime - partialTickTime;
         int speed = 10;
         this.headBottom.rotateAngleX = (float)((double)this.headBottom.rotateAngleX + 0.39269908169872414D * (double)(1.0F - MathHelper.cos(talkTime * 3.1415927F * 2.0F / (float)speed)));
      }

   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      this.rightArm.rotateAngleX = MathHelper.cos(limbSwing * 2.0F / 3.0F) * 1.0F * limbSwingAmount;
      this.leftArm.rotateAngleX = MathHelper.cos(limbSwing * 2.0F / 3.0F) * 1.0F * limbSwingAmount;
      this.headTop.rotateAngleX = headPitch * 3.1415927F / 180.0F;
      this.rightEye.rotateAngleX = this.leftEye.rotateAngleX = this.headTop.rotateAngleX;
      this.headBottom.rotateAngleX += headPitch * 3.1415927F / 180.0F;
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      EntityFrog frog = (EntityFrog)entity;
      GlStateManager.pushMatrix();
      float sizeModifier = frog.getSizeModifier();
      GlStateManager.translate(0.0D, 1.5D - (double)sizeModifier * 1.5D, 0.0D);
      GlStateManager.scale(sizeModifier, sizeModifier, sizeModifier);
      if (this.isChild) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0D, 0.6D, 0.0D);
         GlStateManager.scale(0.625D, 0.625D, 0.625D);
      }

      this.headTop.render(scale);
      this.headBottom.render(scale);
      this.rightEye.render(scale);
      this.leftEye.render(scale);
      if (this.isChild) {
         GlStateManager.popMatrix();
         GlStateManager.scale(0.5D, 0.5D, 0.5D);
         GlStateManager.translate(0.0D, 1.5D, 0.0D);
      }

      this.rightArm.render(scale);
      this.leftArm.render(scale);
      this.body.render(scale);
      GlStateManager.popMatrix();
   }
}
