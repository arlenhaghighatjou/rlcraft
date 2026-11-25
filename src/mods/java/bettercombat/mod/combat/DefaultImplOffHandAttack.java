package bettercombat.mod.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;

public class DefaultImplOffHandAttack implements IOffHandAttack {
   private int offhandCooldown;

   public DefaultImplOffHandAttack() {
   }

   public int getOffhandCooldown() {
      return this.offhandCooldown;
   }

   public void setOffhandCooldown(int amount) {
      this.offhandCooldown = amount;
   }

   public void tick() {
      if (this.offhandCooldown > 0) {
         --this.offhandCooldown;
      }

   }

   public void swingOffHand(EntityPlayer player) {
      player.isSwingInProgress = true;
      player.swingingHand = EnumHand.OFF_HAND;
      if (player.world instanceof WorldServer) {
         ((WorldServer)player.world).getEntityTracker().sendToTracking(player, new SPacketAnimation(player, 3));
      }

   }
}
