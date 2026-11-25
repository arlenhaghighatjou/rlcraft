package gigaherz.toolbelt.common;

import gigaherz.toolbelt.BeltFinder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class BeltEvents {
   public BeltEvents() {
   }

   @SubscribeEvent
   public static void startTracking(StartTracking event) {
      Entity entity = event.getTarget();
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         BeltFinder.sendSync(player);
      }
   }
}
