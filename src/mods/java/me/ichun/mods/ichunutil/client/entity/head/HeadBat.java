package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.passive.EntityBat;

public class HeadBat extends HeadBase<EntityBat> {
   public HeadBat() {
      this.eyeOffset = new float[]{0.0F, 0.03125F, 0.1875F};
   }

   public float getHeadYaw(EntityBat living, float partialTick, int eye) {
      return living.getIsBatHanging() ? -super.getHeadYaw(living, partialTick, eye) : super.getHeadYaw(living, partialTick, eye);
   }
}
