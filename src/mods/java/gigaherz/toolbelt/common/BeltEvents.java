package gigaherz.toolbelt.common;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import gigaherz.toolbelt.BeltFinder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;

public class BeltEvents {
   public BeltEvents() {
   }

   public static void register(EventBus bus) {
      bus.register(StartTracking.class, BeltEvents::startTracking);
   }

   public static void startTracking(StartTracking event) {
      Entity entity = event.getTarget();
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         BeltFinder.sendSync(player);
      }
   }
}
