package com.fantasticsource.mctools.animation;

import com.fantasticsource.tools.Tools;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelBipedEdit extends ModelBiped {
   public float[] headScale = null;
   public float[] chestScale = null;
   public float[] leftArmScale = null;
   public float[] rightArmScale = null;
   public float[] leftLegScale = null;
   public float[] rightLegScale = null;

   public ModelBipedEdit(float modelSize) {
      super(modelSize);
   }

   public ModelBipedEdit(ModelBiped oldModel) {
      this.leftArmPose = oldModel.leftArmPose;
      this.rightArmPose = oldModel.rightArmPose;
      this.textureWidth = oldModel.textureWidth;
      this.textureHeight = oldModel.textureHeight;
      this.bipedHead = oldModel.bipedHead;
      this.bipedBody = oldModel.bipedBody;
      this.bipedLeftArm = oldModel.bipedLeftArm;
      this.bipedRightArm = oldModel.bipedRightArm;
      this.bipedLeftLeg = oldModel.bipedLeftLeg;
      this.bipedRightLeg = oldModel.bipedRightLeg;
      this.bipedHeadwear = oldModel.bipedHeadwear;
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      boolean flag = entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).getTicksElytraFlying() > 4;
      this.bipedHead.rotateAngleY = netHeadYaw * 0.017453292F;
      if (flag) {
         this.bipedHead.rotateAngleX = -0.7853982F;
      } else {
         this.bipedHead.rotateAngleX = headPitch * 0.017453292F;
      }

      this.bipedBody.rotateAngleY = 0.0F;
      this.bipedRightArm.rotationPointZ = 0.0F;
      this.bipedRightArm.rotationPointX = -5.0F;
      this.bipedLeftArm.rotationPointZ = 0.0F;
      this.bipedLeftArm.rotationPointX = 5.0F;
      float f = 1.0F;
      if (flag) {
         f = (float)(entityIn.motionX * entityIn.motionX + entityIn.motionY * entityIn.motionY + entityIn.motionZ * entityIn.motionZ);
         f = f / 0.2F;
         f = f * f * f;
      }

      if (f < 1.0F) {
         f = 1.0F;
      }

      this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 2.0F * limbSwingAmount * 0.5F / f;
      this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
      this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount / f;
      this.bipedRightLeg.rotateAngleY = 0.0F;
      this.bipedLeftLeg.rotateAngleY = 0.0F;
      this.bipedRightLeg.rotateAngleZ = 0.0F;
      this.bipedLeftLeg.rotateAngleZ = 0.0F;
      if (this.isRiding) {
         this.bipedRightArm.rotateAngleX += -0.62831855F;
         this.bipedLeftArm.rotateAngleX += -0.62831855F;
         this.bipedRightLeg.rotateAngleX = -1.4137167F;
         this.bipedRightLeg.rotateAngleY = 0.31415927F;
         this.bipedRightLeg.rotateAngleZ = 0.07853982F;
         this.bipedLeftLeg.rotateAngleX = -1.4137167F;
         this.bipedLeftLeg.rotateAngleY = -0.31415927F;
         this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
      }

      this.bipedRightArm.rotateAngleY = 0.0F;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      switch(this.leftArmPose) {
      case EMPTY:
         this.bipedLeftArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedLeftArm.rotateAngleY = 0.5235988F;
         break;
      case ITEM:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.31415927F;
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      switch(this.rightArmPose) {
      case EMPTY:
         this.bipedRightArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedRightArm.rotateAngleY = -0.5235988F;
         break;
      case ITEM:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.31415927F;
         this.bipedRightArm.rotateAngleY = 0.0F;
      }

      if (this.swingProgress > 0.0F) {
         EnumHandSide enumhandside = this.getMainHand(entityIn);
         ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
         float f1 = this.swingProgress;
         this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * 6.2831855F) * 0.2F;
         if (enumhandside == EnumHandSide.LEFT) {
            this.bipedBody.rotateAngleY *= -1.0F;
         }

         this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
         this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
         this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
         f1 = 1.0F - this.swingProgress;
         f1 = f1 * f1;
         f1 = f1 * f1;
         f1 = 1.0F - f1;
         float f2 = MathHelper.sin(f1 * 3.1415927F);
         float f3 = MathHelper.sin(this.swingProgress * 3.1415927F) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
         modelrenderer.rotateAngleX = (float)((double)modelrenderer.rotateAngleX - ((double)f2 * 1.2D + (double)f3));
         modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
         modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * 3.1415927F) * -0.4F;
      }

      if (this.isSneak) {
         this.bipedBody.rotateAngleX = 0.5F;
         this.bipedRightArm.rotateAngleX += 0.4F;
         this.bipedLeftArm.rotateAngleX += 0.4F;
         this.bipedRightLeg.rotationPointZ = 4.0F;
         this.bipedLeftLeg.rotationPointZ = 4.0F;
         this.bipedRightLeg.rotationPointY = 9.0F;
         this.bipedLeftLeg.rotationPointY = 9.0F;
         this.bipedHead.rotationPointY = 1.0F;
      } else {
         this.bipedBody.rotateAngleX = 0.0F;
         this.bipedRightLeg.rotationPointZ = 0.1F;
         this.bipedLeftLeg.rotationPointZ = 0.1F;
         this.bipedRightLeg.rotationPointY = 12.0F;
         this.bipedLeftLeg.rotationPointY = 12.0F;
         this.bipedHead.rotationPointY = 0.0F;
      }

      this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      if (this.rightArmPose == ArmPose.BOW_AND_ARROW) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
         this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
      } else if (this.leftArmPose == ArmPose.BOW_AND_ARROW) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
         this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
      }

      long millis = System.currentTimeMillis();
      CBipedAnimation animation = CBipedAnimation.getCurrent(entityIn);
      if (animation.head.xPath.path != null) {
         this.bipedHead.offsetX = (float)animation.head.xPath.getRelativePosition(millis).values[0];
      }

      if (animation.head.yPath.path != null) {
         this.bipedHead.offsetY = (float)animation.head.yPath.getRelativePosition(millis).values[0];
      }

      if (animation.head.zPath.path != null) {
         this.bipedHead.offsetZ = (float)animation.head.zPath.getRelativePosition(millis).values[0];
      }

      if (animation.head.xRotPath.path != null) {
         this.bipedHead.rotateAngleX = (float)Tools.posMod(animation.head.xRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.head.yRotPath.path != null) {
         this.bipedHead.rotateAngleY = (float)Tools.posMod(animation.head.yRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.head.zRotPath.path != null) {
         this.bipedHead.rotateAngleZ = (float)Tools.posMod(animation.head.zRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.chest.xPath.path != null) {
         this.bipedBody.offsetX = (float)animation.chest.xPath.getRelativePosition(millis).values[0];
      }

      if (animation.chest.yPath.path != null) {
         this.bipedBody.offsetY = (float)animation.chest.yPath.getRelativePosition(millis).values[0];
      }

      if (animation.chest.zPath.path != null) {
         this.bipedBody.offsetZ = (float)animation.chest.zPath.getRelativePosition(millis).values[0];
      }

      if (animation.chest.xRotPath.path != null) {
         this.bipedBody.rotateAngleX = (float)Tools.posMod(animation.chest.xRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.chest.yRotPath.path != null) {
         this.bipedBody.rotateAngleY = (float)Tools.posMod(animation.chest.yRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.chest.zRotPath.path != null) {
         this.bipedBody.rotateAngleZ = (float)Tools.posMod(animation.chest.zRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.leftArm.xPath.path != null) {
         this.bipedLeftArm.offsetX = (float)animation.leftArm.xPath.getRelativePosition(millis).values[0];
      }

      if (animation.leftArm.yPath.path != null) {
         this.bipedLeftArm.offsetY = (float)animation.leftArm.yPath.getRelativePosition(millis).values[0];
      }

      if (animation.leftArm.zPath.path != null) {
         this.bipedLeftArm.offsetZ = (float)animation.leftArm.zPath.getRelativePosition(millis).values[0];
      }

      if (animation.leftArm.xRotPath.path != null) {
         this.bipedLeftArm.rotateAngleX = (float)Tools.posMod(animation.leftArm.xRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.leftArm.yRotPath.path != null) {
         this.bipedLeftArm.rotateAngleY = (float)Tools.posMod(animation.leftArm.yRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.leftArm.zRotPath.path != null) {
         this.bipedLeftArm.rotateAngleZ = (float)Tools.posMod(animation.leftArm.zRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.rightArm.xPath.path != null) {
         this.bipedRightArm.offsetX = (float)animation.rightArm.xPath.getRelativePosition(millis).values[0];
      }

      if (animation.rightArm.yPath.path != null) {
         this.bipedRightArm.offsetY = (float)animation.rightArm.yPath.getRelativePosition(millis).values[0];
      }

      if (animation.rightArm.zPath.path != null) {
         this.bipedRightArm.offsetZ = (float)animation.rightArm.zPath.getRelativePosition(millis).values[0];
      }

      if (animation.rightArm.xRotPath.path != null) {
         this.bipedRightArm.rotateAngleX = (float)Tools.posMod(animation.rightArm.xRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.rightArm.yRotPath.path != null) {
         this.bipedRightArm.rotateAngleY = (float)Tools.posMod(animation.rightArm.yRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.rightArm.zRotPath.path != null) {
         this.bipedRightArm.rotateAngleZ = (float)Tools.posMod(animation.rightArm.zRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.leftLeg.xPath.path != null) {
         this.bipedLeftLeg.offsetX = (float)animation.leftLeg.xPath.getRelativePosition(millis).values[0];
      }

      if (animation.leftLeg.yPath.path != null) {
         this.bipedLeftLeg.offsetY = (float)animation.leftLeg.yPath.getRelativePosition(millis).values[0];
      }

      if (animation.leftLeg.zPath.path != null) {
         this.bipedLeftLeg.offsetZ = (float)animation.leftLeg.zPath.getRelativePosition(millis).values[0];
      }

      if (animation.leftLeg.xRotPath.path != null) {
         this.bipedLeftLeg.rotateAngleX = (float)Tools.posMod(animation.leftLeg.xRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.leftLeg.yRotPath.path != null) {
         this.bipedLeftLeg.rotateAngleY = (float)Tools.posMod(animation.leftLeg.yRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.leftLeg.zRotPath.path != null) {
         this.bipedLeftLeg.rotateAngleZ = (float)Tools.posMod(animation.leftLeg.zRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.rightLeg.xPath.path != null) {
         this.bipedRightLeg.offsetX = (float)animation.rightLeg.xPath.getRelativePosition(millis).values[0];
      }

      if (animation.rightLeg.yPath.path != null) {
         this.bipedRightLeg.offsetY = (float)animation.rightLeg.yPath.getRelativePosition(millis).values[0];
      }

      if (animation.rightLeg.zPath.path != null) {
         this.bipedRightLeg.offsetZ = (float)animation.rightLeg.zPath.getRelativePosition(millis).values[0];
      }

      if (animation.rightLeg.xRotPath.path != null) {
         this.bipedRightLeg.rotateAngleX = (float)Tools.posMod(animation.rightLeg.xRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.rightLeg.yRotPath.path != null) {
         this.bipedRightLeg.rotateAngleY = (float)Tools.posMod(animation.rightLeg.yRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      if (animation.rightLeg.zRotPath.path != null) {
         this.bipedRightLeg.rotateAngleZ = (float)Tools.posMod(animation.rightLeg.zRotPath.getRelativePosition(millis).values[0], 6.283185307179586D);
      }

      copyModelData(this.bipedHead, this.bipedHeadwear);
   }

   public static void copyModelData(ModelRenderer from, ModelRenderer to) {
      to.offsetX = from.offsetX;
      to.offsetY = from.offsetY;
      to.offsetZ = from.offsetZ;
      copyModelAngles(from, to);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      long millis = System.currentTimeMillis();
      CBipedAnimation animation = CBipedAnimation.getCurrent(entityIn);
      if (animation.head.xScalePath.path != null) {
         this.headScale = new float[]{(float)animation.head.xScalePath.getRelativePosition(millis).values[0], 1.0F, 1.0F};
      }

      if (animation.head.yScalePath.path != null) {
         if (this.headScale == null) {
            this.headScale = new float[]{1.0F, (float)animation.head.yScalePath.getRelativePosition(millis).values[0], 1.0F};
         } else {
            this.headScale[1] = (float)animation.head.yScalePath.getRelativePosition(millis).values[0];
         }
      }

      if (animation.head.zScalePath.path != null) {
         if (this.headScale == null) {
            this.headScale = new float[]{1.0F, 1.0F, (float)animation.head.zScalePath.getRelativePosition(millis).values[0]};
         } else {
            this.headScale[2] = (float)animation.head.zScalePath.getRelativePosition(millis).values[0];
         }
      }

      if (animation.chest.xScalePath.path != null) {
         this.chestScale = new float[]{(float)animation.chest.xScalePath.getRelativePosition(millis).values[0], 1.0F, 1.0F};
      }

      if (animation.chest.yScalePath.path != null) {
         if (this.chestScale == null) {
            this.chestScale = new float[]{1.0F, (float)animation.chest.yScalePath.getRelativePosition(millis).values[0], 1.0F};
         } else {
            this.chestScale[1] = (float)animation.chest.yScalePath.getRelativePosition(millis).values[0];
         }
      }

      if (animation.chest.zScalePath.path != null) {
         if (this.chestScale == null) {
            this.chestScale = new float[]{1.0F, 1.0F, (float)animation.chest.zScalePath.getRelativePosition(millis).values[0]};
         } else {
            this.chestScale[2] = (float)animation.chest.zScalePath.getRelativePosition(millis).values[0];
         }
      }

      if (animation.leftArm.xScalePath.path != null) {
         this.leftArmScale = new float[]{(float)animation.leftArm.xScalePath.getRelativePosition(millis).values[0], 1.0F, 1.0F};
      }

      if (animation.leftArm.yScalePath.path != null) {
         if (this.leftArmScale == null) {
            this.leftArmScale = new float[]{1.0F, (float)animation.leftArm.yScalePath.getRelativePosition(millis).values[0], 1.0F};
         } else {
            this.leftArmScale[1] = (float)animation.leftArm.yScalePath.getRelativePosition(millis).values[0];
         }
      }

      if (animation.leftArm.zScalePath.path != null) {
         if (this.leftArmScale == null) {
            this.leftArmScale = new float[]{1.0F, 1.0F, (float)animation.leftArm.zScalePath.getRelativePosition(millis).values[0]};
         } else {
            this.leftArmScale[2] = (float)animation.leftArm.zScalePath.getRelativePosition(millis).values[0];
         }
      }

      if (animation.rightArm.xScalePath.path != null) {
         this.rightArmScale = new float[]{(float)animation.rightArm.xScalePath.getRelativePosition(millis).values[0], 1.0F, 1.0F};
      }

      if (animation.rightArm.yScalePath.path != null) {
         if (this.rightArmScale == null) {
            this.rightArmScale = new float[]{1.0F, (float)animation.rightArm.yScalePath.getRelativePosition(millis).values[0], 1.0F};
         } else {
            this.rightArmScale[1] = (float)animation.rightArm.yScalePath.getRelativePosition(millis).values[0];
         }
      }

      if (animation.rightArm.zScalePath.path != null) {
         if (this.rightArmScale == null) {
            this.rightArmScale = new float[]{1.0F, 1.0F, (float)animation.rightArm.zScalePath.getRelativePosition(millis).values[0]};
         } else {
            this.rightArmScale[2] = (float)animation.rightArm.zScalePath.getRelativePosition(millis).values[0];
         }
      }

      if (animation.leftLeg.xScalePath.path != null) {
         this.leftLegScale = new float[]{(float)animation.leftLeg.xScalePath.getRelativePosition(millis).values[0], 1.0F, 1.0F};
      }

      if (animation.leftLeg.yScalePath.path != null) {
         if (this.leftLegScale == null) {
            this.leftLegScale = new float[]{1.0F, (float)animation.leftLeg.yScalePath.getRelativePosition(millis).values[0], 1.0F};
         } else {
            this.leftLegScale[1] = (float)animation.leftLeg.yScalePath.getRelativePosition(millis).values[0];
         }
      }

      if (animation.leftLeg.zScalePath.path != null) {
         if (this.leftLegScale == null) {
            this.leftLegScale = new float[]{1.0F, 1.0F, (float)animation.leftLeg.zScalePath.getRelativePosition(millis).values[0]};
         } else {
            this.leftLegScale[2] = (float)animation.leftLeg.zScalePath.getRelativePosition(millis).values[0];
         }
      }

      if (animation.rightLeg.xScalePath.path != null) {
         this.rightLegScale = new float[]{(float)animation.rightLeg.xScalePath.getRelativePosition(millis).values[0], 1.0F, 1.0F};
      }

      if (animation.rightLeg.yScalePath.path != null) {
         if (this.rightLegScale == null) {
            this.rightLegScale = new float[]{1.0F, (float)animation.rightLeg.yScalePath.getRelativePosition(millis).values[0], 1.0F};
         } else {
            this.rightLegScale[1] = (float)animation.rightLeg.yScalePath.getRelativePosition(millis).values[0];
         }
      }

      if (animation.rightLeg.zScalePath.path != null) {
         if (this.rightLegScale == null) {
            this.rightLegScale = new float[]{1.0F, 1.0F, (float)animation.rightLeg.zScalePath.getRelativePosition(millis).values[0]};
         } else {
            this.rightLegScale[2] = (float)animation.rightLeg.zScalePath.getRelativePosition(millis).values[0];
         }
      }

      GlStateManager.pushMatrix();
      if (this.isChild) {
         GlStateManager.scale(0.75F, 0.75F, 0.75F);
         GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
         if (this.headScale != null) {
            GlStateManager.scale(this.headScale[0], this.headScale[1], this.headScale[2]);
         }

         this.bipedHead.render(scale);
         this.bipedHeadwear.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         if (this.chestScale != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.chestScale[0], this.chestScale[1], this.chestScale[2]);
         }

         this.bipedBody.render(scale);
         if (this.chestScale != null) {
            GlStateManager.popMatrix();
         }

         if (this.leftArmScale != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.leftArmScale[0], this.leftArmScale[1], this.leftArmScale[2]);
         }

         this.bipedLeftArm.render(scale);
         if (this.leftArmScale != null) {
            GlStateManager.popMatrix();
         }

         if (this.rightArmScale != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.rightArmScale[0], this.rightArmScale[1], this.rightArmScale[2]);
         }

         this.bipedRightArm.render(scale);
         if (this.rightArmScale != null) {
            GlStateManager.popMatrix();
         }

         if (this.leftLegScale != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.leftLegScale[0], this.leftLegScale[1], this.leftLegScale[2]);
         }

         this.bipedLeftLeg.render(scale);
         if (this.leftLegScale != null) {
            GlStateManager.popMatrix();
         }

         if (this.rightLegScale != null) {
            GlStateManager.scale(this.rightLegScale[0], this.rightLegScale[1], this.rightLegScale[2]);
         }

         this.bipedRightLeg.render(scale);
      } else {
         if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         if (this.headScale != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.headScale[0], this.headScale[1], this.headScale[2]);
         }

         this.bipedHead.render(scale);
         this.bipedHeadwear.render(scale);
         if (this.headScale != null) {
            GlStateManager.popMatrix();
         }

         if (this.chestScale != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.chestScale[0], this.chestScale[1], this.chestScale[2]);
         }

         this.bipedBody.render(scale);
         if (this.chestScale != null) {
            GlStateManager.popMatrix();
         }

         if (this.leftArmScale != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.leftArmScale[0], this.leftArmScale[1], this.leftArmScale[2]);
         }

         this.bipedLeftArm.render(scale);
         if (this.leftArmScale != null) {
            GlStateManager.popMatrix();
         }

         if (this.rightArmScale != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.rightArmScale[0], this.rightArmScale[1], this.rightArmScale[2]);
         }

         this.bipedRightArm.render(scale);
         if (this.rightArmScale != null) {
            GlStateManager.popMatrix();
         }

         if (this.leftLegScale != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.leftLegScale[0], this.leftLegScale[1], this.leftLegScale[2]);
         }

         this.bipedLeftLeg.render(scale);
         if (this.leftLegScale != null) {
            GlStateManager.popMatrix();
         }

         if (this.rightLegScale != null) {
            GlStateManager.scale(this.rightLegScale[0], this.rightLegScale[1], this.rightLegScale[2]);
         }

         this.bipedRightLeg.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public void postRenderArm(float scale, EnumHandSide side) {
      if (side == EnumHandSide.LEFT) {
         GlStateManager.scale(this.leftArmScale[0], this.leftArmScale[1], this.leftArmScale[2]);
      } else {
         GlStateManager.scale(this.rightArmScale[0], this.rightArmScale[1], this.rightArmScale[2]);
      }

      ModelRenderer armRenderer = this.getArmForSide(side);
      GlStateManager.translate(armRenderer.offsetX, armRenderer.offsetY, armRenderer.offsetZ);
      this.getArmForSide(side).postRender(scale);
   }
}
