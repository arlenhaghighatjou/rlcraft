package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntityEndermite;

public class HeadEndermite extends HeadBase<EntityEndermite> {
   public HeadEndermite() {
      this.eyeOffset = new float[]{0.0F, 0.0F, 0.0625F};
      this.irisColour = new float[]{0.34117648F, 0.09019608F, 0.19607843F};
      this.halfInterpupillaryDistance = 0.0F;
      this.eyeScale = 0.6F;
   }

   public int getEyeCount(EntityEndermite living) {
      return 1;
   }
}
