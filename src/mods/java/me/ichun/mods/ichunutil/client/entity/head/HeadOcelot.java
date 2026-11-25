package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.passive.EntityOcelot;

public class HeadOcelot extends HeadBase<EntityOcelot> {
   public float[][] pupilColourAssortment;

   public HeadOcelot() {
      this.eyeOffset = new float[]{0.0F, 0.0625F, 0.1875F};
      this.halfInterpupillaryDistance = 0.09375F;
      this.eyeScale = 0.7F;
      this.pupilColourAssortment = new float[][]{{0.050980393F, 0.49803922F, 0.78431374F}, {0.3019608F, 0.5372549F, 0.050980393F}, {0.12156863F, 0.43529412F, 0.19607843F}};
   }

   public float[] getPupilColours(EntityOcelot living, float partialTick, int eye) {
      this.livingRand.setSeed((long)(Math.abs(living.hashCode()) * 1231));
      return this.pupilColourAssortment[this.livingRand.nextInt(3)];
   }

   public void preChildEntHeadRenderCalls(EntityOcelot living, RenderLivingBase render) {
      if (living.isChild()) {
         float modelScale = 0.0625F;
         GlStateManager.scale(0.75F, 0.75F, 0.75F);
         GlStateManager.translate(0.0F, 10.0F * modelScale, 4.0F * modelScale);
      }

   }
}
