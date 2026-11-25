package vazkii.quark.world.client.model;

import com.google.common.collect.ImmutableSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.world.entity.EntityCrab;

public class ModelCrab extends ModelBase {
   private final ModelRenderer group;
   private final ModelRenderer leftThigh1;
   private final ModelRenderer leftLeg1;
   private final ModelRenderer leftThigh2;
   private final ModelRenderer leftLeg2;
   private final ModelRenderer leftThigh3;
   private final ModelRenderer leftLeg3;
   private final ModelRenderer leftThigh4;
   private final ModelRenderer leftLeg4;
   private final ModelRenderer rightThigh1;
   private final ModelRenderer rightLeg1;
   private final ModelRenderer rightThigh2;
   private final ModelRenderer rightLeg2;
   private final ModelRenderer rightThigh3;
   private final ModelRenderer rightLeg3;
   private final ModelRenderer rightThigh4;
   private final ModelRenderer rightLeg4;
   private final ModelRenderer leftArm;
   private final ModelRenderer leftForearm;
   private final ModelRenderer leftPincers;
   private final ModelRenderer leftUpperClaw;
   private final ModelRenderer leftLowerClaw;
   private final ModelRenderer rightArm;
   private final ModelRenderer rightForearm;
   private final ModelRenderer rightPincers;
   private final ModelRenderer rightUpperClaw;
   private final ModelRenderer rightLowerClaw;
   private final List<Runnable> resetFunctions = new LinkedList();
   private final Set<ModelRenderer> leftLegs;
   private final Set<ModelRenderer> rightLegs;

   public ModelCrab() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.group = new ModelRenderer(this);
      this.group.setRotationPoint(0.0F, 18.0F, 0.0F);
      this.setRotationAngle(this.group, 0.0F, 0.0F, 0.0F);
      this.group.cubeList.add(new ModelBox(this.group, 0, 0, -4.0F, -2.0F, -4.0F, 8, 4, 8, 0.0F, false));
      this.group.cubeList.add(new ModelBox(this.group, 0, 0, -2.0F, -4.0F, -4.0F, 1, 2, 1, 0.0F, false));
      this.group.cubeList.add(new ModelBox(this.group, 0, 0, 1.0F, -4.0F, -4.0F, 1, 2, 1, 0.0F, false));
      this.leftThigh1 = new ModelRenderer(this);
      this.leftThigh1.setRotationPoint(-4.0F, 1.0F, -3.0F);
      this.setRotationAngle(this.leftThigh1, 0.0F, -0.5236F, 0.0F);
      this.group.addChild(this.leftThigh1);
      this.leftThigh1.cubeList.add(new ModelBox(this.leftThigh1, 0, 12, -4.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));
      this.leftLeg1 = new ModelRenderer(this);
      this.leftLeg1.setRotationPoint(-4.0F, 1.0F, 0.0F);
      this.setRotationAngle(this.leftLeg1, 0.0F, 0.0F, 0.2618F);
      this.leftThigh1.addChild(this.leftLeg1);
      this.leftLeg1.cubeList.add(new ModelBox(this.leftLeg1, 4, 0, 0.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));
      this.leftThigh2 = new ModelRenderer(this);
      this.leftThigh2.setRotationPoint(-4.0F, 1.0F, -1.0F);
      this.setRotationAngle(this.leftThigh2, 0.0F, -0.2618F, 0.0F);
      this.group.addChild(this.leftThigh2);
      this.leftThigh2.cubeList.add(new ModelBox(this.leftThigh2, 0, 12, -4.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));
      this.leftLeg2 = new ModelRenderer(this);
      this.leftLeg2.setRotationPoint(-4.0F, 1.0F, 0.0F);
      this.setRotationAngle(this.leftLeg2, 0.0F, 0.0F, 0.5236F);
      this.leftThigh2.addChild(this.leftLeg2);
      this.leftLeg2.cubeList.add(new ModelBox(this.leftLeg2, 4, 0, 0.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));
      this.leftThigh3 = new ModelRenderer(this);
      this.leftThigh3.setRotationPoint(-4.0F, 1.0F, 1.0F);
      this.setRotationAngle(this.leftThigh3, 0.0F, 0.2618F, 0.0F);
      this.group.addChild(this.leftThigh3);
      this.leftThigh3.cubeList.add(new ModelBox(this.leftThigh3, 0, 12, -4.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));
      this.leftLeg3 = new ModelRenderer(this);
      this.leftLeg3.setRotationPoint(-4.0F, 1.0F, 0.0F);
      this.setRotationAngle(this.leftLeg3, 0.0F, 0.0F, 0.5236F);
      this.leftThigh3.addChild(this.leftLeg3);
      this.leftLeg3.cubeList.add(new ModelBox(this.leftLeg3, 4, 0, 0.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));
      this.leftThigh4 = new ModelRenderer(this);
      this.leftThigh4.setRotationPoint(-4.0F, 1.0F, 3.0F);
      this.setRotationAngle(this.leftThigh4, 0.0F, 0.5236F, 0.0F);
      this.group.addChild(this.leftThigh4);
      this.leftThigh4.cubeList.add(new ModelBox(this.leftThigh4, 0, 12, -4.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));
      this.leftLeg4 = new ModelRenderer(this);
      this.leftLeg4.setRotationPoint(-4.0F, 1.0F, 0.0F);
      this.setRotationAngle(this.leftLeg4, 0.0F, 0.0F, 0.2618F);
      this.leftThigh4.addChild(this.leftLeg4);
      this.leftLeg4.cubeList.add(new ModelBox(this.leftLeg4, 4, 0, 0.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));
      this.rightThigh1 = new ModelRenderer(this);
      this.rightThigh1.setRotationPoint(4.0F, 1.0F, -3.0F);
      this.setRotationAngle(this.rightThigh1, 0.0F, 0.5236F, 0.0F);
      this.group.addChild(this.rightThigh1);
      this.rightThigh1.cubeList.add(new ModelBox(this.rightThigh1, 0, 12, 0.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));
      this.rightLeg1 = new ModelRenderer(this);
      this.rightLeg1.setRotationPoint(4.0F, 1.0F, 0.0F);
      this.setRotationAngle(this.rightLeg1, 0.0F, 0.0F, -0.2618F);
      this.rightThigh1.addChild(this.rightLeg1);
      this.rightLeg1.cubeList.add(new ModelBox(this.rightLeg1, 4, 0, -1.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));
      this.rightThigh2 = new ModelRenderer(this);
      this.rightThigh2.setRotationPoint(4.0F, 1.0F, -1.0F);
      this.setRotationAngle(this.rightThigh2, 0.0F, 0.2618F, 0.0F);
      this.group.addChild(this.rightThigh2);
      this.rightThigh2.cubeList.add(new ModelBox(this.rightThigh2, 0, 12, 0.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));
      this.rightLeg2 = new ModelRenderer(this);
      this.rightLeg2.setRotationPoint(4.0F, 1.0F, 0.0F);
      this.setRotationAngle(this.rightLeg2, 0.0F, 0.0F, -0.5236F);
      this.rightThigh2.addChild(this.rightLeg2);
      this.rightLeg2.cubeList.add(new ModelBox(this.rightLeg2, 4, 0, -1.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));
      this.rightThigh3 = new ModelRenderer(this);
      this.rightThigh3.setRotationPoint(4.0F, 1.0F, 1.0F);
      this.setRotationAngle(this.rightThigh3, 0.0F, -0.2618F, 0.0F);
      this.group.addChild(this.rightThigh3);
      this.rightThigh3.cubeList.add(new ModelBox(this.rightThigh3, 0, 12, 0.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));
      this.rightLeg3 = new ModelRenderer(this);
      this.rightLeg3.setRotationPoint(4.0F, 1.0F, 0.0F);
      this.setRotationAngle(this.rightLeg3, 0.0F, 0.0F, -0.5236F);
      this.rightThigh3.addChild(this.rightLeg3);
      this.rightLeg3.cubeList.add(new ModelBox(this.rightLeg3, 4, 0, -1.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));
      this.rightThigh4 = new ModelRenderer(this);
      this.rightThigh4.setRotationPoint(4.0F, 1.0F, 3.0F);
      this.setRotationAngle(this.rightThigh4, 0.0F, -0.5236F, 0.0F);
      this.group.addChild(this.rightThigh4);
      this.rightThigh4.cubeList.add(new ModelBox(this.rightThigh4, 0, 12, 0.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));
      this.rightLeg4 = new ModelRenderer(this);
      this.rightLeg4.setRotationPoint(4.0F, 1.0F, 0.0F);
      this.setRotationAngle(this.rightLeg4, 0.0F, 0.0F, -0.2618F);
      this.rightThigh4.addChild(this.rightLeg4);
      this.rightLeg4.cubeList.add(new ModelBox(this.rightLeg4, 4, 0, -1.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));
      this.leftArm = new ModelRenderer(this);
      this.leftArm.setRotationPoint(-2.0F, 1.0F, -4.0F);
      this.setRotationAngle(this.leftArm, -0.5236F, 0.5236F, 0.0F);
      this.group.addChild(this.leftArm);
      this.leftArm.cubeList.add(new ModelBox(this.leftArm, 24, 0, -1.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F, false));
      this.leftForearm = new ModelRenderer(this);
      this.leftForearm.setRotationPoint(-1.0F, 0.0F, -4.0F);
      this.leftArm.addChild(this.leftForearm);
      this.leftForearm.cubeList.add(new ModelBox(this.leftForearm, 12, 12, -0.5F, -0.5F, -2.0F, 2, 2, 4, 0.0F, false));
      this.leftPincers = new ModelRenderer(this);
      this.leftPincers.setRotationPoint(-1.0F, 0.0F, -4.0F);
      this.leftArm.addChild(this.leftPincers);
      this.leftUpperClaw = new ModelRenderer(this);
      this.leftUpperClaw.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.setRotationAngle(this.leftUpperClaw, -0.2618F, 0.0F, 0.0F);
      this.leftPincers.addChild(this.leftUpperClaw);
      this.leftUpperClaw.cubeList.add(new ModelBox(this.leftUpperClaw, 12, 18, 0.0F, -1.0F, -4.0F, 1, 2, 4, 0.1F, false));
      this.leftLowerClaw = new ModelRenderer(this);
      this.leftLowerClaw.setRotationPoint(0.0F, 0.5F, 0.0F);
      this.leftPincers.addChild(this.leftLowerClaw);
      this.setRotationAngle(this.leftLowerClaw, 0.0F, 0.0F, 0.0F);
      this.leftLowerClaw.cubeList.add(new ModelBox(this.leftLowerClaw, 12, 24, 0.0F, 0.5F, -4.0F, 1, 1, 4, 0.0F, false));
      this.rightArm = new ModelRenderer(this);
      this.rightArm.setRotationPoint(2.0F, 1.0F, -4.0F);
      this.setRotationAngle(this.rightArm, -0.5236F, -0.5236F, 0.0F);
      this.group.addChild(this.rightArm);
      this.rightArm.cubeList.add(new ModelBox(this.rightArm, 24, 0, 0.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F, false));
      this.rightForearm = new ModelRenderer(this);
      this.rightForearm.setRotationPoint(1.0F, 0.0F, -4.0F);
      this.rightArm.addChild(this.rightForearm);
      this.rightForearm.cubeList.add(new ModelBox(this.rightForearm, 12, 12, -1.5F, -0.5F, -2.0F, 2, 2, 4, 0.0F, false));
      this.rightPincers = new ModelRenderer(this);
      this.rightPincers.setRotationPoint(-5.0F, 0.0F, -4.0F);
      this.rightArm.addChild(this.rightPincers);
      this.rightUpperClaw = new ModelRenderer(this);
      this.rightUpperClaw.setRotationPoint(6.0F, 0.0F, 0.0F);
      this.setRotationAngle(this.rightUpperClaw, -0.2618F, 0.0F, 0.0F);
      this.rightPincers.addChild(this.rightUpperClaw);
      this.rightUpperClaw.cubeList.add(new ModelBox(this.rightUpperClaw, 12, 18, -1.0F, -1.0F, -4.0F, 1, 2, 4, 0.1F, false));
      this.rightLowerClaw = new ModelRenderer(this);
      this.rightLowerClaw.setRotationPoint(6.0F, 0.5F, 0.0F);
      this.rightPincers.addChild(this.rightLowerClaw);
      this.setRotationAngle(this.rightLowerClaw, 0.0F, 0.0F, 0.0F);
      this.rightLowerClaw.cubeList.add(new ModelBox(this.rightLowerClaw, 12, 24, -1.0F, 0.5F, -4.0F, 1, 1, 4, 0.0F, false));
      this.leftLegs = ImmutableSet.of(this.leftLeg1, this.leftLeg2, this.leftLeg3, this.leftLeg4);
      this.rightLegs = ImmutableSet.of(this.rightLeg1, this.rightLeg2, this.rightLeg3, this.rightLeg4);
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      this.resetModel();
      this.leftLeg1.rotateAngleZ = 0.2618F + (-1.0F + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;
      this.leftLeg2.rotateAngleZ = 0.5236F + (-1.0F + MathHelper.cos(limbSwing * 0.6662F + 3.1415927F)) * 0.7F * limbSwingAmount;
      this.leftLeg3.rotateAngleZ = 0.5236F + (-1.0F + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;
      this.leftLeg4.rotateAngleZ = 0.2618F + (-1.0F + MathHelper.cos(limbSwing * 0.6662F + 3.1415927F)) * 0.7F * limbSwingAmount;
      this.rightLeg1.rotateAngleZ = -0.2618F + (1.0F + MathHelper.cos(limbSwing * 0.6662F + 3.1415927F)) * 0.7F * limbSwingAmount;
      this.rightLeg2.rotateAngleZ = -0.5236F + (1.0F + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;
      this.rightLeg3.rotateAngleZ = -0.5236F + (1.0F + MathHelper.cos(limbSwing * 0.6662F + 3.1415927F)) * 0.7F * limbSwingAmount;
      this.rightLeg4.rotateAngleZ = -0.2618F + (1.0F + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;
      if (entityIn instanceof EntityCrab && ((EntityCrab)entityIn).isRaving()) {
         float crabRaveBPM = 31.25F;
         float freq = 20.0F / crabRaveBPM;
         float tick = ageInTicks * freq;
         float sin = (float)(Math.sin((double)tick) * 0.5D + 0.5D);
         float legRot = sin * 1.4F;
         this.leftLegs.forEach((l) -> {
            l.rotateAngleZ = legRot;
         });
         this.rightLegs.forEach((l) -> {
            l.rotateAngleZ = -legRot;
         });
         float maxHeight = 0.15F;
         float horizontalOff = 0.2F;
         this.group.offsetY = sin * maxHeight;
         float slowSin = (float)Math.sin((double)(tick / 2.0F));
         this.group.offsetX = slowSin * horizontalOff;
         float armRot = sin * 0.5F - 1.2F;
         this.leftArm.rotateAngleX = armRot;
         this.rightArm.rotateAngleX = armRot;
         float pincerRot = sin * -0.3F;
         this.leftLowerClaw.rotateAngleX = pincerRot;
         this.rightLowerClaw.rotateAngleX = pincerRot;
      }

   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      EntityCrab crab = (EntityCrab)entity;
      GlStateManager.pushMatrix();
      float sizeModifier = crab.getSizeModifier();
      if (this.isChild) {
         sizeModifier /= 2.0F;
      }

      GlStateManager.translate(0.0D, 1.5D - (double)sizeModifier * 1.5D, 0.0D);
      GlStateManager.scale(sizeModifier, sizeModifier, sizeModifier);
      this.group.render(f5);
      GlStateManager.popMatrix();
   }

   private void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      float offX = modelRenderer.offsetX;
      float offY = modelRenderer.offsetY;
      float offZ = modelRenderer.offsetZ;
      this.resetFunctions.add(() -> {
         modelRenderer.rotateAngleX = x;
         modelRenderer.rotateAngleY = y;
         modelRenderer.rotateAngleZ = z;
         modelRenderer.offsetX = offX;
         modelRenderer.offsetY = offY;
         modelRenderer.offsetZ = offZ;
      });
   }

   private void resetModel() {
      this.resetFunctions.forEach(Runnable::run);
   }
}
