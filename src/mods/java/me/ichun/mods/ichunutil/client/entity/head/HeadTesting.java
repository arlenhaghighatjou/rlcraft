package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.EntityLivingBase;

public class HeadTesting extends HeadBase<EntityLivingBase> {
   public HeadTesting() {
   }

   public float getEyeSideOffset(EntityLivingBase living, float partialTick, int eye) {
      this.halfInterpupillaryDistance = 0.125F;
      return super.getEyeSideOffset(living, partialTick, eye);
   }

   public float getEyeScale(EntityLivingBase living, float partialTick, int eye) {
      return 0.4F;
   }

   public float[] getHeadJointOffset(EntityLivingBase living, float partialTick, int eye) {
      return new float[]{0.0F, -0.625F, 1.0F};
   }

   public float[] getEyeOffsetFromJoint(EntityLivingBase living, float partialTick, int eye) {
      return new float[]{0.0F, -0.03125F, 0.1875F};
   }
}
