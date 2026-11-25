package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntitySpider;

public class HeadSpider extends HeadBase<EntitySpider> {
   public float halfInterpupillaryDistance2 = 0.125F;
   public float halfInterpupillaryDistance3 = 0.25F;
   public float[] eyeOffset2 = new float[]{0.0F, 0.1875F, 0.5F};
   public float[] eyeOffset3 = new float[]{0.0F, 0.0625F, 0.46875F};

   public HeadSpider() {
      this.eyeOffset = new float[]{0.0F, 0.0F, 0.5F};
      this.irisColour = new float[]{0.8F, 0.0F, 0.0F};
      this.halfInterpupillaryDistance = 0.0625F;
      this.eyeScale = 0.8F;
   }

   public int getEyeCount(EntitySpider living) {
      return 6;
   }

   public float getEyeSideOffset(EntitySpider living, float partialTick, int eye) {
      if (eye <= 1) {
         return eye % 2 == 0 ? this.halfInterpupillaryDistance : -this.halfInterpupillaryDistance;
      } else if (eye <= 3) {
         return eye % 2 == 0 ? this.halfInterpupillaryDistance2 : -this.halfInterpupillaryDistance2;
      } else {
         return eye % 2 == 0 ? this.halfInterpupillaryDistance3 : -this.halfInterpupillaryDistance3;
      }
   }

   public float getEyeRotation(EntitySpider living, float partialTick, int eye) {
      if (eye >= 4) {
         return eye % 2 == 0 ? 45.0F : -45.0F;
      } else {
         return 0.0F;
      }
   }

   public float[] getEyeOffsetFromJoint(EntitySpider living, float partialTick, int eye) {
      if (eye <= 1) {
         return this.eyeOffset;
      } else {
         return eye <= 3 ? this.eyeOffset2 : this.eyeOffset3;
      }
   }

   public boolean affectedByInvisibility(EntitySpider living, int eye) {
      return false;
   }

   public boolean doesEyeGlow(EntitySpider living, int eye) {
      return true;
   }
}
