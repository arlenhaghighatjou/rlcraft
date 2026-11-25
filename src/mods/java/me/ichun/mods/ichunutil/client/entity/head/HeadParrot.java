package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.passive.EntityParrot;

public class HeadParrot extends HeadBase<EntityParrot> {
   public HeadParrot() {
      this.eyeOffset = new float[]{0.0F, 0.059375F, 0.03125F};
      this.halfInterpupillaryDistance = 0.0625F;
      this.eyeScale = 0.375F;
   }

   public float getEyeRotation(EntityParrot living, float partialTick, int eye) {
      return eye == 0 ? 90.0F : -90.0F;
   }

   public float getPupilScale(EntityParrot living, float partialTick, int eye) {
      return super.getPupilScale(living, partialTick, eye) * (living.isPartying() ? 1.6F : 1.0F);
   }
}
