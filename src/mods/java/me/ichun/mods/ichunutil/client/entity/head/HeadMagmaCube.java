package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntityMagmaCube;

public class HeadMagmaCube extends HeadBase<EntityMagmaCube> {
   public HeadMagmaCube() {
      this.eyeOffset = new float[]{0.0F, -1.1875F, 0.25F};
   }

   public float getEyeScale(EntityMagmaCube living, float partialTick, int eye) {
      float squishFactor = living.prevSquishFactor + (living.squishFactor - living.prevSquishFactor) * partialTick;
      return squishFactor <= 0.0F ? this.eyeScale : this.eyeScale + squishFactor * 1.5F;
   }

   public float[] getHeadJointOffset(EntityMagmaCube living, float partialTick, int eye) {
      float squishFactor = living.prevSquishFactor + (living.squishFactor - living.prevSquishFactor) * partialTick;
      return squishFactor <= 0.0F ? super.getHeadJointOffset(living, partialTick, eye) : new float[]{0.0F, -(0.0F - squishFactor * 2.5F) / 16.0F, 0.0F};
   }
}
