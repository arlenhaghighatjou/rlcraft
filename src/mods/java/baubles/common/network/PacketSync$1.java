package baubles.common.network;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

class PacketSync$1 implements Runnable {
   PacketSync$1(PacketSync this$0, PacketSync var2) {
      this.this$0 = this$0;
      this.val$message = var2;
   }

   public void run() {
      World world = Baubles.proxy.getClientWorld();
      if (world != null) {
         Entity p = world.getEntityByID(this.val$message.playerId);
         if (p != null && p instanceof EntityPlayer) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)p);
            baubles.setStackInSlot(this.val$message.slot, this.val$message.bauble);
         }

      }
   }
}
