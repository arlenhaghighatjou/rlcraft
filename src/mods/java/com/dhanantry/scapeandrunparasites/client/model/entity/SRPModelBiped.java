package com.dhanantry.scapeandrunparasites.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SRPModelBiped extends ModelBiped {
   private byte parent;
   public ModelRenderer jointH;
   public ModelRenderer jointHW;
   public ModelRenderer body;
   public ModelRenderer rightA;
   public ModelRenderer leftA;
   public ModelRenderer rightL;
   public ModelRenderer leftL;
   public SRPModelBiped.ArmPose leftAP;
   public SRPModelBiped.ArmPose rightAP;
   public boolean isSneak;

   public SRPModelBiped() {
      this(0.0F);
   }

   public SRPModelBiped(float modelSize) {
      this(modelSize, 0.0F, 64, 32);
   }

   public SRPModelBiped(float modelSize, float p_i1149_2_, int textureWidthIn, int textureHeightIn) {
      this.leftAP = SRPModelBiped.ArmPose.EMPTY;
      this.rightAP = SRPModelBiped.ArmPose.EMPTY;
      this.textureWidth = textureWidthIn;
      this.textureHeight = textureHeightIn;
      this.jointH = new ModelRenderer(this, 0, 0);
      this.jointH.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
      this.jointH.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.jointHW = new ModelRenderer(this, 32, 0);
      this.jointHW.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
      this.jointHW.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.body = new ModelRenderer(this, 16, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
      this.body.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.rightA = new ModelRenderer(this, 40, 16);
      this.rightA.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
      this.rightA.setRotationPoint(-5.0F, 2.0F + p_i1149_2_, 0.0F);
      this.leftA = new ModelRenderer(this, 40, 16);
      this.leftA.mirror = true;
      this.leftA.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
      this.leftA.setRotationPoint(5.0F, 2.0F + p_i1149_2_, 0.0F);
      this.rightL = new ModelRenderer(this, 0, 16);
      this.rightL.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
      this.rightL.setRotationPoint(-1.9F, 12.0F + p_i1149_2_, 0.0F);
      this.leftL = new ModelRenderer(this, 0, 16);
      this.leftL.mirror = true;
      this.leftL.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
      this.leftL.setRotationPoint(1.9F, 12.0F + p_i1149_2_, 0.0F);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netjointHYaw, float jointHPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netjointHYaw, jointHPitch, scale, entityIn);
      GlStateManager.pushMatrix();
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.scale(0.75F, 0.75F, 0.75F);
         GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
         this.jointH.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         this.body.render(scale);
         this.rightA.render(scale);
         this.leftA.render(scale);
         this.rightL.render(scale);
         this.leftL.render(scale);
         this.jointHW.render(scale);
      } else {
         if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         this.jointH.render(scale);
         this.body.render(scale);
         this.rightA.render(scale);
         this.leftA.render(scale);
         this.rightL.render(scale);
         this.leftL.render(scale);
         this.jointHW.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netjointHYaw, float jointHPitch, float scaleFactor, Entity entityIn) {
      this.jointH.offsetY = 0.0F;
      this.jointH.offsetZ = 0.0F;
      this.jointH.rotateAngleX = 0.0F;
      this.leftL.rotateAngleX = 0.0F;
      this.rightL.rotateAngleX = 0.0F;
      switch(this.parent) {
      case 1:
         this.jointH.offsetY = -0.25F;
         this.jointH.offsetZ = 0.24000001F;
         float f4 = MathHelper.cos(ageInTicks * 0.08F) * 0.07F;
         this.jointH.rotateAngleX = 0.45F + f4;
         float GS0 = 1.0F;
         if (entityIn.prevPosX != entityIn.posX || entityIn.prevPosZ != entityIn.posZ) {
            float GS = 1.75F;
            float GD = 0.55F;
            GS0 = GS0 + 0.45F;
            this.swingX(this.leftL, 0.3F * GS, 5.0F * GD, -1, limbSwing, limbSwingAmount);
            this.swingX(this.rightL, 0.3F * GS, 5.0F * GD, 1, limbSwing, limbSwingAmount);
         }
      default:
      }
   }

   public void setParent(byte in) {
      this.parent = in;
   }

   public void setModelAttributes(ModelBase model) {
      super.setModelAttributes(model);
      if (model instanceof SRPModelBiped) {
         SRPModelBiped modelbiped = (SRPModelBiped)model;
         this.leftAP = modelbiped.leftAP;
         this.rightAP = modelbiped.rightAP;
         this.isSneak = modelbiped.isSneak;
      }

   }

   public void setVisible(boolean visible) {
      this.jointH.showModel = visible;
      this.jointHW.showModel = visible;
      this.body.showModel = visible;
      this.rightA.showModel = visible;
      this.leftA.showModel = visible;
      this.rightL.showModel = visible;
      this.leftL.showModel = visible;
   }

   public void postRenderArm(float scale, EnumHandSide side) {
      this.getArmForSide(side).postRender(scale);
   }

   protected ModelRenderer getArmForSide(EnumHandSide side) {
      return side == EnumHandSide.LEFT ? this.leftA : this.rightA;
   }

   protected EnumHandSide getMainHand(Entity entityIn) {
      if (entityIn instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)entityIn;
         EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
         return entitylivingbase.swingingHand == EnumHand.MAIN_HAND ? enumhandside : enumhandside.opposite();
      } else {
         return EnumHandSide.RIGHT;
      }
   }

   public void swingX(ModelRenderer modelRenderer, float speed, float degree, int invert, float limbSwing, float limbSwingAmount) {
      modelRenderer.rotateAngleX = (float)((double)((float)invert * limbSwingAmount * degree) * Math.cos((double)(limbSwing * speed)) * (double)limbSwingAmount);
   }

   @SideOnly(Side.CLIENT)
   public static enum ArmPose {
      EMPTY,
      ITEM,
      BLOCK,
      BOW_AND_ARROW;

      private ArmPose() {
      }
   }
}
