package reborncore.common.multiblock;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class MultiblockServerTickHandler {
   public MultiblockServerTickHandler() {
   }

   @SubscribeEvent
   public void onWorldTick(WorldTickEvent event) {
      if (event.phase == Phase.START) {
         MultiblockRegistry.tickStart(event.world);
      }

   }
}
