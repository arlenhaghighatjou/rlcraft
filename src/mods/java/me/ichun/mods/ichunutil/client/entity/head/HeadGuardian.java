package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntityGuardian;

public class HeadGuardian extends HeadBase<EntityGuardian> {
   public HeadGuardian() {
      this.eyeOffset = new float[]{0.0F, -1.0F, 0.5F};
      this.irisColour = new float[]{0.8392157F, 0.827451F, 0.79607844F};
      this.pupilColour = new float[]{0.39607844F, 0.13725491F, 0.12156863F};
      this.halfInterpupillaryDistance = 0.0F;
      this.eyeScale = 2.0F;
   }

   public int getEyeCount(EntityGuardian living) {
      return 1;
   }

   public float[] getIrisColours(EntityGuardian living, float partialTick, int eye) {
      return this.irisColour;
   }

   public float[] getPupilColours(EntityGuardian living, float partialTick, int eye) {
      return this.pupilColour;
   }
}
