package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.boss.EntityWither;

public class HeadWither extends HeadBase<EntityWither> {
   public float[] headJointSideHeadLeft = new float[]{-0.625F, -0.25F, 0.0F};
   public float[] headJointSideHeadRight = new float[]{0.5F, -0.25F, 0.0F};
   public float[] eyeOffsetSideHead = new float[]{0.0625F, 0.125F, 0.25F};
   public float halfInterpupillaryDistanceSideHead = 0.15625F;
   public float eyeScaleSideHead = 0.75F;

   public HeadWither() {
      this.headJoint = new float[]{0.0F, 0.0F, 0.0F};
      this.eyeOffset = new float[]{0.0F, 0.0F, 0.25F};
      this.halfInterpupillaryDistance = 0.125F;
      this.eyeScale = 1.0F;
   }

   public int getEyeCount(EntityWither living) {
      return 6;
   }

   public float getEyeScale(EntityWither living, float partialTick, int eye) {
      return eye > 1 ? this.eyeScaleSideHead : this.eyeScale;
   }

   public float getHeadYaw(EntityWither living, float partialTick, int eye) {
      return eye <= 1 ? super.getHeadYaw(living, partialTick, eye) : living.getHeadYRotation(eye <= 3 ? 1 : 0) - (living.prevRenderYawOffset + (living.renderYawOffset - living.prevRenderYawOffset) * partialTick);
   }

   public float getHeadPitch(EntityWither living, float partialTick, int eye) {
      return eye <= 1 ? super.getHeadPitch(living, partialTick, eye) : living.getHeadXRotation(eye <= 3 ? 1 : 0);
   }

   public float getEyeSideOffset(EntityWither living, float partialTick, int eye) {
      if (eye <= 1) {
         return eye % 2 == 0 ? this.halfInterpupillaryDistance : -this.halfInterpupillaryDistance;
      } else {
         return eye % 2 == 0 ? this.halfInterpupillaryDistanceSideHead : -this.halfInterpupillaryDistanceSideHead;
      }
   }

   public float[] getEyeOffsetFromJoint(EntityWither living, float partialTick, int eye) {
      return eye <= 1 ? this.eyeOffset : this.eyeOffsetSideHead;
   }

   public float[] getHeadJointOffset(EntityWither living, float partialTick, int eye) {
      if (eye <= 1) {
         return super.getHeadJointOffset(living, partialTick, eye);
      } else {
         return eye <= 3 ? this.headJointSideHeadLeft : this.headJointSideHeadRight;
      }
   }
}
