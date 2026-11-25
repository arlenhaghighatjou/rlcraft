package rlmixins.handlers.chunkanimator;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ChunkAnimatorCooldownHandler {
   private static int cooldown = 0;

   public ChunkAnimatorCooldownHandler() {
   }

   public static void startCooldown() {
      cooldown = 10;
   }

   public static int getCooldown() {
      return cooldown;
   }

   @SubscribeEvent
   public static void clientTick(ClientTickEvent event) {
      if (event.phase != Phase.START && cooldown > 0) {
         --cooldown;
      }
   }
}
