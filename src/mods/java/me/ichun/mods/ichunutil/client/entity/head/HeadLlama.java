package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.passive.AbstractHorse;

public class HeadLlama extends HeadHorse {
   public HeadLlama() {
      this.eyeOffset = new float[]{0.0F, 0.125F, 0.5F};
      this.halfInterpupillaryDistance = 0.1875F;
      this.eyeScale = 0.9F;
   }

   public float getEyeRotation(AbstractHorse living, float partialTick, int eye) {
      return 0.0F;
   }

   public float getHeadPitch(AbstractHorse living, float partialTick, int eye) {
      this.eyeOffsetNormal = new float[]{0.0F, 0.8875F, 0.375F};
      this.halfInterpupillaryDistanceNormal = 0.14375F;
      this.eyeScaleNormal = 0.8F;
      return iChunUtil.config.horseEasterEgg == 1 ? 0.0F : super.getHeadPitch(living, partialTick, eye);
   }

   public void preChildEntHeadRenderCalls(AbstractHorse living, RenderLivingBase render) {
      if (living.isChild()) {
         float modelScale = 0.0625F;
         if (iChunUtil.config.horseEasterEgg == 1) {
            GlStateManager.scale(0.625F, 0.45454544F, 0.45454544F);
            GlStateManager.translate(0.0F, 33.0F * modelScale, 0.0F);
         } else {
            GlStateManager.scale(0.71428573F, 0.64935064F, 0.7936508F);
            GlStateManager.translate(0.0F, 21.0F * modelScale, 0.22F);
         }
      }

   }
}
