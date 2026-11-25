package bettercombat.mod.util;

import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.network.PacketOffhandCooldown;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

public class ScheduledPacketTask implements Runnable {
   private EntityPlayer player;
   private PacketOffhandCooldown message;

   public ScheduledPacketTask(EntityPlayer player, PacketOffhandCooldown message) {
      this.player = player;
      this.message = message;
   }

   public void run() {
      if (this.player != null) {
         Helpers.execNullable(this.player.getCapability(EventHandlers.TUTO_CAP, (EnumFacing)null), (stg) -> {
            stg.setOffhandCooldown(this.message.cooldown);
         });
         Helpers.execNullable(this.player.getCapability(EventHandlers.TUTO_CAP, (EnumFacing)null), (stg) -> {
            stg.setOffhandBeginningCooldown(this.message.cooldownBeginning);
         });
      }
   }
}
