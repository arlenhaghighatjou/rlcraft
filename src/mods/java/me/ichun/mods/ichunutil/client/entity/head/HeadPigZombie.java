package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityPigZombie;

public class HeadPigZombie extends HeadBase<EntityPigZombie> {
   public float[] eyeOffsetSkin = new float[]{-0.021875F, 0.28125F, 0.28125F};
   public float eyeScaleSkin = 0.65F;

   public HeadPigZombie() {
      this.headJoint = new float[]{0.0F, 0.0F, 0.0F};
      this.eyeOffset = new float[]{0.0F, 0.28125F, 0.25F};
      this.halfInterpupillaryDistance = 0.1875F;
      this.eyeScale = 0.8F;
   }

   public float getEyeScale(EntityPigZombie living, float partialTick, int eye) {
      return eye == 1 && !living.isChild() ? this.eyeScaleSkin : this.eyeScale;
   }

   public float[] getEyeOffsetFromJoint(EntityPigZombie living, float partialTick, int eye) {
      return eye == 1 && !living.isChild() ? this.eyeOffsetSkin : this.eyeOffset;
   }

   public float[] getHeadJointOffset(EntityPigZombie living, float partialTick, int eye) {
      if (living.isSneaking()) {
         GlStateManager.translate(0.0F, 0.2F, 0.0F);
      }

      return super.getHeadJointOffset(living, partialTick, eye);
   }
}
