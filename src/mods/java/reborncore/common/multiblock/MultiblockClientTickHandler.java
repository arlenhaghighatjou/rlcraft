package reborncore.common.multiblock;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class MultiblockClientTickHandler {
   public MultiblockClientTickHandler() {
   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.START) {
         MultiblockRegistry.tickStart(Minecraft.getMinecraft().world);
      }

   }
}
