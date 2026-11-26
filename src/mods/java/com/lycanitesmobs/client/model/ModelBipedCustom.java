package com.lycanitesmobs.client.model;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import java.util.Map.Entry;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBipedCustom extends ModelCustom {
   public int heldItemLeft;
   public int heldItemRight;
   public ModelRenderer head;
   public ModelRenderer body;
   public ModelRenderer leftarm;
   public ModelRenderer rightarm;
   public ModelRenderer leftleg;
   public ModelRenderer rightleg;

   public ModelBipedCustom() {
      this(1.0F);
   }

   public ModelBipedCustom(float shadowSize) {
      this.heldItemLeft = 0;
      this.heldItemRight = 0;
      this.textureWidth = 128;
      this.textureHeight = 128;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8);
      this.head.setRotationPoint(0.0F, -21.0F, 0.0F);
      this.head.setTextureSize(128, 128);
      this.setRotation(this.head, 0.0F, 0.0F, 0.0F);
      this.body = new ModelRenderer(this, 0, 18);
      this.body.addBox(-8.0F, 0.0F, -2.0F, 16, 24, 7);
      this.body.setRotationPoint(0.0F, -21.0F, 0.0F);
      this.body.setTextureSize(128, 128);
      this.setRotation(this.body, 0.0F, 0.0F, 0.0F);
      this.leftarm = new ModelRenderer(this, 80, 0);
      this.leftarm.addBox(0.0F, -2.0F, -3.0F, 5, 21, 6);
      this.leftarm.setRotationPoint(8.0F, -16.0F, 1.0F);
      this.leftarm.setTextureSize(128, 128);
      this.setRotation(this.leftarm, 0.0F, 0.0F, 0.0F);
      this.rightarm = new ModelRenderer(this, 80, 0);
      this.rightarm.mirror = true;
      this.rightarm.addBox(-5.0F, -2.0F, -3.0F, 5, 21, 6);
      this.rightarm.setRotationPoint(-8.0F, -16.0F, 1.0F);
      this.rightarm.setTextureSize(128, 128);
      this.setRotation(this.rightarm, 0.0F, 0.0F, 0.0F);
      this.rightarm.mirror = false;
      this.leftleg = new ModelRenderer(this, 0, 49);
      this.leftleg.addBox(-4.0F, 0.0F, -3.0F, 8, 20, 7);
      this.leftleg.setRotationPoint(4.0F, 3.0F, 1.0F);
      this.leftleg.setTextureSize(128, 128);
      this.setRotation(this.leftleg, 0.0F, 0.0F, 0.0F);
      this.rightleg = new ModelRenderer(this, 0, 49);
      this.rightleg.mirror = true;
      this.rightleg.addBox(-4.0F, 0.0F, -3.0F, 8, 20, 7);
      this.rightleg.setRotationPoint(-4.0F, 3.0F, 1.0F);
      this.rightleg.setTextureSize(128, 128);
      this.setRotation(this.rightleg, 0.0F, 0.0F, 0.0F);
      this.rightleg.mirror = false;
   }

   public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.render(entity, time, distance, loop, lookY, lookX, scale);
      if (this.isChild) {
         GlStateManager.translate(0.0F, 0.5F, 0.25F);
      }

      this.head.render(scale);
      if (this.isChild) {
         GlStateManager.translate(0.0F, 0.25F, -0.25F);
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
      }

      this.body.render(scale);
      this.leftarm.render(scale);
      this.rightarm.render(scale);
      this.leftleg.render(scale);
      this.rightleg.render(scale);
   }

   public void setAngles(EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      for(Entry<ModelRenderer, float[]> initRotation : this.initRotations.entrySet()) {
         float[] rotations = (float[])initRotation.getValue();
         this.setRotation((ModelRenderer)initRotation.getKey(), rotations[0], rotations[1], rotations[2]);
      }

      this.head.rotateAngleX += lookX / 57.295776F;
      this.head.rotateAngleY += lookY / 57.295776F;
      this.leftarm.rotateAngleZ += -MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F;
      this.leftarm.rotateAngleX += -MathHelper.sin(loop * 0.067F) * 0.05F;
      this.rightarm.rotateAngleZ += MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F;
      this.rightarm.rotateAngleX += MathHelper.sin(loop * 0.067F) * 0.05F;
      this.rightarm.rotateAngleX += MathHelper.cos(time * 0.6662F + 3.1415927F) * 2.0F * distance * 0.5F;
      this.leftarm.rotateAngleX += MathHelper.cos(time * 0.6662F) * 2.0F * distance * 0.5F;
      this.leftleg.rotateAngleX += MathHelper.cos(time * 0.6662F + 3.1415927F) * 1.4F * distance;
      this.leftleg.rotateAngleY += 0.0F;
      this.rightleg.rotateAngleX += MathHelper.cos(time * 0.6662F) * 1.4F * distance;
      this.rightleg.rotateAngleY += 0.0F;
      if (this.isRiding) {
         this.leftarm.rotateAngleX += -0.62831855F;
         this.rightarm.rotateAngleX += -0.62831855F;
         this.leftleg.rotateAngleX += -1.2566371F;
         this.leftleg.rotateAngleY += -0.31415927F;
         this.rightleg.rotateAngleX += -1.2566371F;
         this.rightleg.rotateAngleY += 0.31415927F;
      }

      if (this.heldItemLeft != 0) {
         this.leftarm.rotateAngleX += this.leftarm.rotateAngleX * 0.5F - 0.31415927F * (float)this.heldItemLeft;
      }

      if (this.heldItemRight != 0) {
         this.rightarm.rotateAngleX += this.rightarm.rotateAngleX * 0.5F - 0.31415927F * (float)this.heldItemRight;
      }

   }

   public void animate(EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
   }
}
