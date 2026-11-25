package net.silentchaos512.lib.event;

import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.silentchaos512.lib.SilentLib;

@EventBusSubscriber(
   modid = "silentlib"
)
public final class ServerTicks {
   private static final int QUEUE_OVERFLOW_LIMIT = 30;
   private static volatile Queue<Runnable> scheduledActions = new ArrayDeque();

   private ServerTicks() {
   }

   public static void scheduleAction(Runnable action) {
      scheduledActions.add(action);
      if (scheduledActions.size() > 30) {
         SilentLib.LOGGER.warn("Too many server tick actions queued! Currently at {} items.", Integer.valueOf(scheduledActions.size()));
      }

   }

   @SubscribeEvent
   public static void serverTicks(ServerTickEvent event) {
      if (event.phase == Phase.START) {
         runScheduledActions();
      }

   }

   private static void runScheduledActions() {
      for(Runnable action = (Runnable)scheduledActions.poll(); action != null; action = (Runnable)scheduledActions.poll()) {
         action.run();
      }

   }
}
