package collision.handlers;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import collision.packets.PacketCollisionS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler {
   public EventHandler() {
   }

   @SideOnly(Side.CLIENT)
   public void register(EventBus bus) {
      bus.register(PlayerTickEvent.class, this::playerTick);
   }

   public void playerTick(PlayerTickEvent event) {
      if (event.phase != Phase.START && event.player != null && event.player.world.isRemote) {
         EntityPlayer player = event.player;
         double motionX = player.motionX;
         double motionZ = player.motionZ;
         double curMotionCombined = (double)((int)(Math.sqrt(motionX * motionX + motionZ * motionZ) * 20.0D * 100.0D)) / 100.0D;
         double prevMotionCombined = player.getEntityData().getDouble("prevMotionCombined");
         player.getEntityData().setDouble("prevMotionCombined", curMotionCombined);
         if (!player.isElytraFlying()) {
            double accel = prevMotionCombined - curMotionCombined;
            if (accel > 5.0D && player.collidedHorizontally) {
               PacketHandler.INSTANCE.sendToServer(new PacketCollisionS(accel));
            }

         }
      }
   }
}
