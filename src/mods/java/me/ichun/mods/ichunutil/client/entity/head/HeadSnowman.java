package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntitySnowman;

public class HeadSnowman extends HeadBase<EntitySnowman> {
   public float[] eyeOffsetNoPumpkinLeft = new float[]{0.0F, 0.34375F, 0.21875F};
   public float[] eyeOffsetNoPumpkinRight = new float[]{0.0F, 0.375F, 0.21875F};

   public HeadSnowman() {
      this.eyeOffset = new float[]{0.0F, 0.46875F, 0.3125F};
      this.halfInterpupillaryDistance = 0.09375F;
      this.eyeScale = 1.0F;
   }

   public float[] getEyeOffsetFromJoint(EntitySnowman living, float partialTick, int eye) {
      if (living.isPumpkinEquipped()) {
         return this.eyeOffset;
      } else {
         return eye == 0 ? this.eyeOffsetNoPumpkinRight : this.eyeOffsetNoPumpkinLeft;
      }
   }

   public float getEyeScale(EntitySnowman living, float partialTick, int eye) {
      if (living.isPumpkinEquipped()) {
         return this.eyeScale;
      } else {
         return eye == 0 ? 0.65F : 0.75F;
      }
   }
}
