package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntityEnderman;

public class HeadEnderman extends HeadBase<EntityEnderman> {
   public HeadEnderman() {
      this.eyeOffset = new float[]{0.0F, 0.21875F, 0.25F};
      this.irisColour = new float[]{0.88F, 0.47F, 0.98F};
      this.pupilColour = new float[]{0.8F, 0.0F, 0.98F};
      this.halfInterpupillaryDistance = 0.15625F;
      this.eyeScale = 0.85F;
   }

   public float getPupilScale(EntityEnderman living, float partialTick, int eye) {
      return living.isScreaming() ? 0.4F : super.getPupilScale(living, partialTick, eye);
   }

   public boolean affectedByInvisibility(EntityEnderman living, int eye) {
      return false;
   }

   public boolean doesEyeGlow(EntityEnderman living, int eye) {
      return true;
   }
}
