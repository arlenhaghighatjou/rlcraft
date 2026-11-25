package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class HeadPlayer extends HeadBase<EntityPlayer> {
   public HeadPlayer() {
   }

   public float[] getHeadJointOffset(EntityPlayer living, float partialTick, int eye) {
      if (living.isSneaking()) {
         GlStateManager.translate(0.0F, 0.2F, 0.0F);
      }

      return super.getHeadJointOffset(living, partialTick, eye);
   }

   public float getHeadPitch(EntityPlayer living, float partialTick, int eye) {
      return living.isElytraFlying() ? -45.0F : super.getHeadPitch(living, partialTick, eye);
   }
}
