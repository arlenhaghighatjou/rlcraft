package com.endertech.minecraft.mods.adhooks.motion;

import com.endertech.minecraft.forge.messages.ForgeNetMsg;
import com.endertech.minecraft.forge.messages.ForgeNetMsgHandler;
import com.endertech.minecraft.mods.adhooks.Main;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class BoostUpMsg extends ForgeNetMsg {
   public float strength;

   public BoostUpMsg() {
   }

   public BoostUpMsg(float strength) {
      this.strength = strength;
   }

   public void sendTo(EntityPlayerMP player) {
      Main.instance.getConnection().sendToPlayer(this, player);
   }

   public static class Handler extends ForgeNetMsgHandler<BoostUpMsg> {
      public Handler() {
      }

      public IMessage onMessage(BoostUpMsg message, MessageContext ctx) {
         EntityPlayer player = this.getPlayer(ctx);
         if (player == null) {
            return null;
         } else {
            this.addScheduledTask(ctx, () -> {
               double motionY = player.motionY;
               double strength = (double)message.strength;
               double maxMotionY = 1.2D * strength;
               double newMotionY = motionY + strength;
               player.motionY = newMotionY > maxMotionY ? Math.max(motionY, maxMotionY) : newMotionY;
            });
            return null;
         }
      }
   }
}
