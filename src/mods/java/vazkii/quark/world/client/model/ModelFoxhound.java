package vazkii.quark.world.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.world.entity.EntityFoxhound;

public class ModelFoxhound extends ModelBase {
   public final ModelRenderer head;
   public final ModelRenderer rightFrontLeg;
   public final ModelRenderer leftFrontLeg;
   public final ModelRenderer rightBackLeg;
   public final ModelRenderer leftBackLeg;
   public final ModelRenderer body;
   public final ModelRenderer snout;
   public final ModelRenderer rightEar;
   public final ModelRenderer leftEar;
   public final ModelRenderer tail;
   public final ModelRenderer fluff;

   public ModelFoxhound() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.leftBackLeg = new ModelRenderer(this, 36, 32);
      this.leftBackLeg.setRotationPoint(3.0F, 12.0F, 9.5F);
      this.leftBackLeg.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3, 0.0F);
      this.rightFrontLeg = new ModelRenderer(this, 0, 32);
      this.rightFrontLeg.setRotationPoint(-2.0F, 12.0F, 2.0F);
      this.rightFrontLeg.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3, 0.0F);
      this.rightEar = new ModelRenderer(this, 0, 47);
      this.rightEar.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.rightEar.addBox(-4.0F, -5.0F, -5.0F, 2, 2, 3, 0.0F);
      this.tail = new ModelRenderer(this, 36, 16);
      this.tail.setRotationPoint(0.0F, 0.0F, 1.5F);
      this.tail.addBox(-2.0F, -4.0F, 0.0F, 4, 5, 10, 0.0F);
      this.setRotateAngle(this.tail, -1.3089969F, 0.0F, 0.0F);
      this.body = new ModelRenderer(this, 0, 2);
      this.body.setRotationPoint(0.0F, 17.0F, 12.0F);
      this.body.addBox(-4.0F, -12.0F, 0.0F, 8, 12, 6, 0.0F);
      this.setRotateAngle(this.body, 1.5707964F, 0.0F, 0.0F);
      this.fluff = new ModelRenderer(this, 28, 0);
      this.fluff.setRotationPoint(0.0F, -13.0F, 3.0F);
      this.fluff.addBox(-5.0F, 0.0F, -4.0F, 10, 8, 8, 0.05F);
      this.leftFrontLeg = new ModelRenderer(this, 12, 32);
      this.leftFrontLeg.setRotationPoint(2.0F, 12.0F, 2.0F);
      this.leftFrontLeg.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3, 0.0F);
      this.rightBackLeg = new ModelRenderer(this, 24, 32);
      this.rightBackLeg.setRotationPoint(-3.0F, 12.0F, 9.5F);
      this.rightBackLeg.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3, 0.0F);
      this.leftEar = new ModelRenderer(this, 10, 47);
      this.leftEar.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.leftEar.addBox(2.0F, -5.0F, -5.0F, 2, 2, 3, 0.0F);
      this.head = new ModelRenderer(this, 0, 20);
      this.head.setRotationPoint(0.0F, 14.5F, 0.0F);
      this.head.addBox(-4.0F, -3.0F, -6.0F, 8, 6, 6, 0.0F);
      this.snout = new ModelRenderer(this, 29, 18);
      this.snout.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.snout.addBox(-2.0F, 1.0F, -10.0F, 4, 2, 4, 0.0F);
      this.head.addChild(this.rightEar);
      this.body.addChild(this.tail);
      this.body.addChild(this.fluff);
      this.head.addChild(this.leftEar);
      this.head.addChild(this.snout);
   }

   public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
      EntityFoxhound hound = (EntityFoxhound)entity;
      if (!hound.isSitting() && !hound.isAngry()) {
         this.tail.rotateAngleX = -1.3089969F + MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
      } else {
         this.tail.rotateAngleX = -0.65449846F;
      }

      this.head.rotateAngleY = hound.getInterestedAngle(partialTickTime) - hound.getShakeAngle(partialTickTime, 0.0F);
      this.head.rotateAngleX = 0.0F;
      this.body.rotateAngleY = hound.getShakeAngle(partialTickTime, -0.16F);
      this.tail.rotateAngleY = hound.getShakeAngle(partialTickTime, -0.2F);
      if (hound.isSleeping()) {
         this.head.setRotationPoint(1.0F, 20.5F, 0.0F);
         this.setRotateAngle(this.head, 0.0F, 0.7853982F, -0.04363323F);
         this.body.setRotationPoint(0.0F, 20.0F, 12.0F);
         this.setRotateAngle(this.body, 1.5707964F, 0.0F, 1.5707964F);
         this.tail.setRotationPoint(0.0F, -1.0F, 1.0F);
         this.setRotateAngle(this.tail, 2.5497515F, -0.22759093F, 0.0F);
         this.rightFrontLeg.setRotationPoint(0.0F, 18.0F, 2.0F);
         this.leftFrontLeg.setRotationPoint(2.0F, 21.0F, 1.0F);
         this.rightBackLeg.setRotationPoint(0.0F, 22.0F, 11.0F);
         this.leftBackLeg.setRotationPoint(3.0F, 20.0F, 10.0F);
         this.setRotateAngle(this.rightFrontLeg, 0.21816616F, 0.43633232F, 1.3089969F);
         this.setRotateAngle(this.leftFrontLeg, 0.0F, 0.0F, 1.3962634F);
         this.setRotateAngle(this.rightBackLeg, -1.0471976F, -0.08726646F, 1.4835298F);
         this.setRotateAngle(this.leftBackLeg, -0.7853982F, 0.0F, 1.2217305F);
      } else if (hound.isSitting()) {
         this.head.setRotationPoint(0.0F, 12.0F, 2.0F);
         this.body.setRotationPoint(0.0F, 23.0F, 7.0F);
         this.setRotateAngle(this.body, 0.7853982F, this.body.rotateAngleY, 0.0F);
         this.tail.setRotationPoint(0.0F, 0.0F, -2.0F);
         this.setRotateAngle(this.tail, -0.5235988F, -0.72431165F, 0.0F);
         this.rightFrontLeg.setRotationPoint(-2.0F, 12.0F, 1.25F);
         this.leftFrontLeg.setRotationPoint(2.0F, 12.0F, 1.25F);
         this.rightBackLeg.setRotationPoint(-3.0F, 21.0F, 10.0F);
         this.leftBackLeg.setRotationPoint(3.0F, 21.0F, 10.0F);
         this.setRotateAngle(this.rightFrontLeg, 0.0F, 0.0F, 0.0F);
         this.setRotateAngle(this.leftFrontLeg, 0.0F, 0.0F, 0.0F);
         this.setRotateAngle(this.rightBackLeg, -1.3089969F, 0.3926991F, 0.0F);
         this.setRotateAngle(this.leftBackLeg, -1.3089969F, -0.3926991F, 0.0F);
      } else {
         this.head.setRotationPoint(0.0F, 14.5F, 0.0F);
         this.body.setRotationPoint(0.0F, 17.0F, 12.0F);
         this.setRotateAngle(this.body, 1.5707964F, this.body.rotateAngleY, 0.0F);
         this.tail.setRotationPoint(0.0F, 0.0F, 1.5F);
         this.rightFrontLeg.setRotationPoint(-2.0F, 12.0F, 2.0F);
         this.leftFrontLeg.setRotationPoint(2.0F, 12.0F, 2.0F);
         this.rightBackLeg.setRotationPoint(-3.0F, 12.0F, 9.5F);
         this.leftBackLeg.setRotationPoint(3.0F, 12.0F, 9.5F);
         this.setRotateAngle(this.rightFrontLeg, MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount, 0.0F, 0.0F);
         this.setRotateAngle(this.leftFrontLeg, MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount, 0.0F, 0.0F);
         this.setRotateAngle(this.rightBackLeg, MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount, 0.0F, 0.0F);
         this.setRotateAngle(this.leftBackLeg, MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount, 0.0F, 0.0F);
      }

   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      if (!((EntityFoxhound)entityIn).isSleeping()) {
         this.head.rotateAngleY += netHeadYaw * 0.017453292F;
         this.head.rotateAngleX += headPitch * 0.017453292F;
      } else {
         this.head.rotateAngleY += MathHelper.cos(ageInTicks / 30.0F) / 20.0F;
      }

   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0D, 0.0D, ((EntityFoxhound)entity).isSitting() ? -0.25D : -0.35D);
      GlStateManager.pushMatrix();
      if (this.isChild) {
         GlStateManager.translate(0.0F, 5.0F * scale, 0.0F);
      }

      this.head.render(scale);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      if (this.isChild) {
         GlStateManager.translate(0.0F, 12.0F * scale, 0.0F);
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
      }

      this.leftBackLeg.render(scale);
      this.rightFrontLeg.render(scale);
      this.body.render(scale);
      this.leftFrontLeg.render(scale);
      this.rightBackLeg.render(scale);
      GlStateManager.popMatrix();
      GlStateManager.popMatrix();
   }

   public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
