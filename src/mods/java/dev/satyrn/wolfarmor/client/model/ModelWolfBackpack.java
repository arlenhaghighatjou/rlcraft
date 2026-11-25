package dev.satyrn.wolfarmor.client.model;

import javax.annotation.Nonnull;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWolfBackpack extends ModelBase {
   public ModelRenderer backpackRightTop = new ModelRenderer(this, 0, 0);
   public ModelRenderer backpackRightBottom;
   public ModelRenderer backpackLeftTop;
   public ModelRenderer backpackLeftBottom;
   private static final float INITIAL_Z_ROTATION = 0.139626F;

   public ModelWolfBackpack(float scale) {
      this.backpackRightTop.setTextureSize(16, 32);
      this.backpackRightTop.addBox(3.0F, -2.0F, 0.0F, 2, 2, 5, scale);
      this.backpackRightTop.setRotationPoint(0.0F, 14.0F, 2.0F);
      this.backpackRightBottom = new ModelRenderer(this, 0, 7);
      this.backpackRightBottom.setTextureSize(16, 32);
      this.backpackRightBottom.addBox(3.0F, 0.0F, 0.0F, 3, 4, 5, scale);
      this.backpackRightBottom.setRotationPoint(0.0F, 14.0F, 2.0F);
      this.backpackLeftTop = new ModelRenderer(this, 0, 16);
      this.backpackLeftTop.setTextureSize(16, 32);
      this.backpackLeftTop.addBox(-5.0F, -2.0F, 0.0F, 2, 2, 5, scale);
      this.backpackLeftTop.setRotationPoint(0.0F, 14.0F, 2.0F);
      this.backpackLeftTop.mirror = true;
      this.backpackLeftBottom = new ModelRenderer(this, 0, 23);
      this.backpackLeftBottom.setTextureSize(16, 32);
      this.backpackLeftBottom.addBox(-6.0F, 0.0F, 0.0F, 3, 4, 5, scale);
      this.backpackLeftBottom.setRotationPoint(0.0F, 14.0F, 2.0F);
      this.backpackLeftBottom.mirror = true;
      setRotation(this.backpackRightTop, 0.0F, 0.0F, -0.139626F);
      setRotation(this.backpackRightBottom, 0.0F, 0.0F, -0.139626F);
      setRotation(this.backpackLeftTop, 0.0F, 0.0F, 0.139626F);
      setRotation(this.backpackLeftBottom, 0.0F, 0.0F, 0.139626F);
   }

   public void render(@Nonnull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
      if (this.isChild) {
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         this.backpackRightTop.renderWithRotation(scale);
         this.backpackRightBottom.renderWithRotation(scale);
         this.backpackLeftTop.renderWithRotation(scale);
         this.backpackLeftBottom.renderWithRotation(scale);
         GlStateManager.popMatrix();
      } else {
         this.backpackRightTop.renderWithRotation(scale);
         this.backpackRightBottom.renderWithRotation(scale);
         this.backpackLeftTop.renderWithRotation(scale);
         this.backpackLeftBottom.renderWithRotation(scale);
      }

   }

   public void setLivingAnimations(@Nonnull EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
      if (entity instanceof EntityWolf) {
         EntityWolf entityWolfArmored = (EntityWolf)entity;
         float rotationPointY = 14.0F;
         float rotationPointZ = 2.0F;
         float rotateAngleX = 0.0F;
         if (entityWolfArmored.isSitting()) {
            rotationPointY = 18.0F;
            rotationPointZ = 0.0F;
            rotateAngleX = -0.7853982F;
         }

         this.backpackRightTop.setRotationPoint(0.0F, rotationPointY, rotationPointZ);
         this.backpackRightBottom.setRotationPoint(0.0F, rotationPointY, rotationPointZ);
         this.backpackLeftTop.setRotationPoint(0.0F, rotationPointY, rotationPointZ);
         this.backpackLeftBottom.setRotationPoint(0.0F, rotationPointY, rotationPointZ);
         setRotation(this.backpackRightTop, rotateAngleX, 0.0F, entityWolfArmored.getShakeAngle(partialTickTime, -0.16F) - 0.139626F + MathHelper.cos(limbSwing * 1.2F) * 0.15F * limbSwingAmount);
         setRotation(this.backpackRightBottom, rotateAngleX, 0.0F, entityWolfArmored.getShakeAngle(partialTickTime, -0.16F) - 0.139626F + MathHelper.cos(limbSwing * 1.2F) * 0.15F * limbSwingAmount);
         setRotation(this.backpackLeftTop, rotateAngleX, 0.0F, entityWolfArmored.getShakeAngle(partialTickTime, -0.16F) + 0.139626F + MathHelper.sin(limbSwing * 1.2F) * 0.15F * limbSwingAmount);
         setRotation(this.backpackLeftBottom, rotateAngleX, 0.0F, entityWolfArmored.getShakeAngle(partialTickTime, -0.16F) + 0.139626F + MathHelper.sin(limbSwing * 1.2F) * 0.15F * limbSwingAmount);
      }

   }

   protected static void setRotation(@Nonnull ModelRenderer model, float xRotation, float yRotation, float zRotation) {
      model.rotateAngleX = xRotation;
      model.rotateAngleY = yRotation;
      model.rotateAngleZ = zRotation;
   }
}
