package snownee.snow.compat;

import java.util.List;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.snow.SnowRealMagic;

public final class FBPHack {
   public FBPHack() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void hackFBP(Load event) {
      try {
         Class.forName("io.redstudioragnarok.FBP.FBP");
      } catch (ClassNotFoundException var8) {
         try {
            Class<?> FBPClass = Class.forName("com.TominoCZ.FBP.FBP");
            Object mod = FBPClass.getDeclaredField("INSTANCE").get((Object)null);
            List<String> list = (List)FBPClass.getDeclaredField("blockAnimBlacklist").get(mod);
            if (list.contains("minecraft:snow_layer")) {
               return;
            }

            list.add("minecraft:snow_layer");
            Class<?> FBPConfigHandlerClass = Class.forName("com.TominoCZ.FBP.handler.FBPConfigHandler");
            FBPConfigHandlerClass.getDeclaredMethod("writeAnimExceptions").invoke((Object)null);
         } catch (Throwable var7) {
            SnowRealMagic.logger.catching(var7);
         }
      }

   }
}
