package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.passive.EntityWolf;

public class HeadWolf extends HeadBase<EntityWolf> {
   public float[] eyeOffsetTame = new float[]{-0.0625F, 0.0625F, 0.125F};
   public float[] irisColourAngry = new float[]{0.7137255F, 0.05882353F, 0.05882353F};
   public float[] pupilColourAngry = new float[]{0.89411765F, 0.18039216F, 0.18039216F};

   public HeadWolf() {
      this.eyeOffset = new float[]{-0.0625F, 0.03125F, 0.125F};
      this.eyeScale = 0.65F;
   }

   public float getEyeScale(EntityWolf living, float partialTick, int eye) {
      return living.isTamed() ? 0.75F : this.eyeScale;
   }

   public float[] getEyeOffsetFromJoint(EntityWolf living, float partialTick, int eye) {
      return living.isTamed() ? this.eyeOffsetTame : this.eyeOffset;
   }

   public float[] getIrisColours(EntityWolf living, float partialTick, int eye) {
      return living.isAngry() ? this.irisColourAngry : this.irisColour;
   }

   public float[] getPupilColours(EntityWolf living, float partialTick, int eye) {
      return living.isAngry() ? this.pupilColourAngry : this.pupilColour;
   }

   public void preChildEntHeadRenderCalls(EntityWolf living, RenderLivingBase render) {
      if (living.isChild()) {
         float modelScale = 0.0625F;
         GlStateManager.translate(0.0F, 5.0F * modelScale, 2.0F * modelScale);
      }

   }
}
