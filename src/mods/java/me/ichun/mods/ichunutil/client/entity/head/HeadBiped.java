package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class HeadBiped extends HeadBase<EntityLivingBase> {
   public HeadBiped() {
   }

   public float[] getHeadJointOffset(EntityLivingBase living, float partialTick, int eye) {
      if (living.isSneaking()) {
         GlStateManager.translate(0.0F, 0.2F, 0.0F);
      }

      return super.getHeadJointOffset(living, partialTick, eye);
   }
}
