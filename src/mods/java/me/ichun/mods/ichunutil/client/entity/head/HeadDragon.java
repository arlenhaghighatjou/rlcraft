package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.EntityDragon;

public class HeadDragon extends HeadBase<EntityDragon> {
   public float yaw;
   public float pitch;
   public float roll;

   public HeadDragon() {
      this.eyeOffset = new float[]{0.0F, 0.25F, 0.625F};
      this.eyeScale = 2.0F;
      this.halfInterpupillaryDistance = 0.3125F;
      this.pupilColour = new float[]{0.8F, 0.0F, 0.98039216F};
   }

   public float getHeadYaw(EntityDragon living, float partialTick, int eye) {
      return this.yaw;
   }

   public float getHeadPitch(EntityDragon living, float partialTick, int eye) {
      return this.pitch;
   }

   public float[] getHeadJointOffset(EntityDragon living, float partialTick, int eye) {
      float f = living.prevAnimTime + (living.animTime - living.prevAnimTime) * partialTick;
      float f1 = (float)(Math.sin((double)(f * 6.2831855F - 1.0F)) + 1.0D);
      f1 = (f1 * f1 + f1 * 2.0F) * 0.05F;
      GlStateManager.translate(0.0F, f1 - 2.0F, -3.0F);
      GlStateManager.rotate(f1 * 2.0F, 1.0F, 0.0F, 0.0F);
      float f2 = 20.0F;
      float f4 = 0.0F;
      double[] adouble = living.getMovementOffsets(6, partialTick);
      float f6 = this.updateRotations(living.getMovementOffsets(5, partialTick)[0] - living.getMovementOffsets(10, partialTick)[0]);
      float f7 = this.updateRotations(living.getMovementOffsets(5, partialTick)[0] + (double)(f6 / 2.0F));
      float f8 = f * 6.2831855F;
      float f3 = -12.0F;

      for(int i = 0; i < 5; ++i) {
         double[] adouble1 = living.getMovementOffsets(5 - i, partialTick);
         float f9 = (float)Math.cos((double)((float)i * 0.45F + f8)) * 0.15F;
         double rotateAngleY = (double)(this.updateRotations(adouble1[0] - adouble[0]) * 0.017453292F * 1.5F);
         double rotateAngleX = (double)(f9 + living.getHeadPartYOffset(i, adouble, adouble1) * 0.017453292F * 1.5F * 5.0F);
         f2 = (float)((double)f2 + Math.sin(rotateAngleX) * 10.0D);
         f3 = (float)((double)f3 - Math.cos(rotateAngleY) * Math.cos(rotateAngleX) * 10.0D);
         f4 = (float)((double)f4 - Math.sin(rotateAngleY) * Math.cos(rotateAngleX) * 10.0D);
      }

      this.headJoint[1] = -(f2 / 16.0F);
      this.headJoint[2] = -(f3 / 16.0F);
      this.headJoint[0] = -(f4 / 16.0F);
      double[] adouble2 = living.getMovementOffsets(0, partialTick);
      this.yaw = (float)Math.toDegrees((double)(this.updateRotations(adouble2[0] - adouble[0]) * 0.017453292F));
      this.pitch = (float)Math.toDegrees((double)(this.updateRotations((double)living.getHeadPartYOffset(6, adouble, adouble2)) * 0.017453292F * 1.5F * 5.0F));
      this.roll = (float)Math.toDegrees((double)(-this.updateRotations(adouble2[0] - (double)f7) * 0.017453292F));
      return this.headJoint;
   }

   public boolean affectedByInvisibility(EntityDragon living, int eye) {
      return false;
   }

   public boolean doesEyeGlow(EntityDragon living, int eye) {
      return true;
   }

   private float updateRotations(double p_78214_1_) {
      while(p_78214_1_ >= 180.0D) {
         p_78214_1_ -= 360.0D;
      }

      while(p_78214_1_ < -180.0D) {
         p_78214_1_ += 360.0D;
      }

      return (float)p_78214_1_;
   }
}
