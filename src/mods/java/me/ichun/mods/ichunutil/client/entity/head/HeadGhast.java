package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntityGhast;

public class HeadGhast extends HeadBase<EntityGhast> {
   public HeadGhast() {
      this.eyeOffset = new float[]{0.0F, -0.40625F, 0.5F};
      this.halfInterpupillaryDistance = 0.21875F;
      this.eyeScale = 1.5F;
   }

   public float getEyeScale(EntityGhast living, float partialTick, int eye) {
      return living.isAttacking() ? this.eyeScale : 0.0F;
   }
}
