package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.passive.AbstractHorse;

public class HeadHorse extends HeadBase<AbstractHorse> {
   public float[] eyeOffsetNormal = new float[]{0.0F, 0.53125F, -0.09375F};
   public float halfInterpupillaryDistanceNormal = 0.15625F;
   public float eyeScaleNormal = 0.6F;

   public HeadHorse() {
      this.eyeOffset = new float[]{0.0F, 0.375F, 0.3125F};
      this.halfInterpupillaryDistance = 0.1875F;
      this.eyeScale = 0.9F;
   }

   public float[] getEyeOffsetFromJoint(AbstractHorse living, float partialTick, int eye) {
      return iChunUtil.config.horseEasterEgg == 1 ? this.eyeOffset : this.eyeOffsetNormal;
   }

   public float getEyeSideOffset(AbstractHorse living, float partialTick, int eye) {
      return iChunUtil.config.horseEasterEgg == 1 ? (eye == 0 ? this.halfInterpupillaryDistance : -this.halfInterpupillaryDistance) : (eye == 0 ? this.halfInterpupillaryDistanceNormal : -this.halfInterpupillaryDistanceNormal);
   }

   public float getEyeScale(AbstractHorse living, float partialTick, int eye) {
      return iChunUtil.config.horseEasterEgg == 1 ? this.eyeScale : this.eyeScaleNormal;
   }

   public float getEyeRotation(AbstractHorse living, float partialTick, int eye) {
      return iChunUtil.config.horseEasterEgg == 1 ? 0.0F : (eye == 0 ? 90.0F : -90.0F);
   }

   public float getHeadYaw(AbstractHorse living, float partialTick, int eye) {
      return iChunUtil.config.horseEasterEgg == 1 ? 180.0F : super.getHeadYaw(living, partialTick, eye);
   }

   public float getHeadPitch(AbstractHorse living, float partialTick, int eye) {
      return iChunUtil.config.horseEasterEgg == 1 ? (float)Math.toDegrees((double)(living.getRearingAmount(partialTick) * 0.7853982F)) : super.getHeadPitch(living, partialTick, eye);
   }

   public void preChildEntHeadRenderCalls(AbstractHorse living, RenderLivingBase render) {
      if (living.isChild()) {
         float f1 = living.getHorseSize();
         if (iChunUtil.config.horseEasterEgg == 1) {
            GlStateManager.scale(f1, f1, f1);
            GlStateManager.translate(0.0F, 1.35F * (1.0F - f1), 0.0F);
         } else {
            float f = living.getGrassEatingAmount(0.0F);
            float f2 = 0.5F + f1 * f1 * 0.5F;
            GlStateManager.scale(f2, f2, f2);
            if (f <= 0.0F) {
               GlStateManager.translate(0.0F, 1.35F * (1.0F - f1), 0.0F);
            } else {
               GlStateManager.translate(0.0F, 0.9F * (1.0F - f1) * f + 1.35F * (1.0F - f1) * (1.0F - f), 0.15F * (1.0F - f1) * f);
            }
         }
      }

   }
}
