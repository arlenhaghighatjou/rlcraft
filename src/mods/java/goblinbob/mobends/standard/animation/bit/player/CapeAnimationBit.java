package goblinbob.mobends.standard.animation.bit.player;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.math.MathHelper;

public class CapeAnimationBit extends AnimationBit<PlayerData> {
   public CapeAnimationBit() {
   }

   public String[] getActions(PlayerData entityData) {
      return null;
   }

   public void perform(PlayerData data) {
      AbstractClientPlayer player = (AbstractClientPlayer)data.getEntity();
      data.cape.rotation.orientX(0.0F);
      double partialTicks = (double)DataUpdateHandler.partialTicks;
      double d0 = player.prevChasingPosX + (player.chasingPosX - player.prevChasingPosX) * partialTicks - (player.prevPosX + (player.posX - player.prevPosX) * partialTicks);
      double d1 = player.prevChasingPosY + (player.chasingPosY - player.prevChasingPosY) * partialTicks - (player.prevPosY + (player.posY - player.prevPosY) * partialTicks);
      double d2 = player.prevChasingPosZ + (player.chasingPosZ - player.prevChasingPosZ) * partialTicks - (player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks);
      double f = (double)player.prevRenderYawOffset + (double)(player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
      double d3 = Math.sin(f * 0.017453292D);
      double d4 = -Math.cos(f * 0.017453292D);
      double f1 = d1 * 10.0D;
      f1 = MathHelper.clamp(f1, -6.0D, 32.0D);
      float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
      float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      double f4 = (double)player.prevCameraYaw + (double)(player.cameraYaw - player.prevCameraYaw) * partialTicks;
      f1 = f1 + Math.sin(((double)player.prevDistanceWalkedModified + (double)(player.distanceWalkedModified - player.prevDistanceWalkedModified) * partialTicks) * 6.0D) * 32.0D * f4;
      if (player.isSneaking()) {
         f1 += 25.0D;
      }

      if (data.isFlying() && player.isSprinting()) {
         data.cape.rotation.setSmoothness(0.5F).orientX(0.0F);
         data.setCapeWaveSpeed(4.0F);
      } else {
         data.cape.rotation.setSmoothness(0.5F).orientX((float)((double)(6.0F + f2 / 2.0F) + f1));
         data.cape.rotation.rotateZ(f3 / 2.0F);
         data.cape.rotation.rotateY(-f3 / 2.0F);
         data.setCapeWaveSpeed(1.0F);
      }

   }
}
