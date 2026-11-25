package goblinbob.mobends.core.client.event;

import goblinbob.mobends.core.flux.ComputedDependencyHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class FluxHandler {
   public FluxHandler() {
   }

   @SubscribeEvent
   public void checkDirty(RenderTickEvent event) {
      if (event.phase != Phase.END) {
         ComputedDependencyHelper.reevaluateDirty();
      }
   }
}
