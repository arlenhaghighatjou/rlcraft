package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.passive.EntityRabbit;

public class HeadRabbit extends HeadBase<EntityRabbit> {
   public HeadRabbit() {
      this.eyeOffset = new float[]{0.0F, 0.1875F, 0.3125F};
      this.halfInterpupillaryDistance = 0.0625F;
      this.eyeScale = 0.6F;
   }

   public float[] getHeadJointOffset(EntityRabbit living, float partialTick, int eye) {
      float scale = 0.0625F;
      if (living.isChild()) {
         GlStateManager.scale(0.56666666F, 0.56666666F, 0.56666666F);
         GlStateManager.translate(0.0F, 22.0F * scale, 2.0F * scale);
      } else {
         GlStateManager.scale(0.6F, 0.6F, 0.6F);
         GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
      }

      return super.getHeadJointOffset(living, partialTick, eye);
   }
}
