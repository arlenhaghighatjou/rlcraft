package net.silentchaos512.lib.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.util.GameUtil;

public final class ClientTicks {
   /** @deprecated */
   @Deprecated
   public static final ClientTicks INSTANCE = new ClientTicks();
   private static final int QUEUE_OVERFLOW_LIMIT = 200;
   private static volatile Queue<Runnable> scheduledActions = new ArrayDeque();
   public static int ticksInGame = 0;
   public static float partialTicks = 0.0F;
   public static float deltaTicks = 0.0F;
   public static float totalTicks = 0.0F;

   private ClientTicks() {
   }

   public static void scheduleAction(Runnable action) {
      if (GameUtil.isClient()) {
         scheduledActions.add(action);
      } else {
         SilentLib.LOGGER.error("Tried to add client tick action on server side? {}", action);
      }

      if (scheduledActions.size() >= 200) {
         SilentLib.LOGGER.warn("Too many client tick actions queued! Currently at {} items. Would have added '{}'.", Integer.valueOf(scheduledActions.size()), action);
         SilentLib.LOGGER.error(new IllegalStateException("ClientTicks queue overflow"));
         scheduledActions.clear();
      }

   }

   public static void register(EventBus bus) {
      bus.register(ClientTickEvent.class, ClientTicks::clientTickEnd);
      bus.register(RenderTickEvent.class, ClientTicks::renderTick);
   }

   public static void clientTickEnd(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         runScheduledActions();
         updateTickCounters();
      }
   }

   public static void renderTick(RenderTickEvent event) {
      if (event.phase == Phase.START) {
         partialTicks = event.renderTickTime;
      }

   }

   private static void runScheduledActions() {
      for(Runnable action = (Runnable)scheduledActions.poll(); action != null; action = (Runnable)scheduledActions.poll()) {
         action.run();
      }

   }

   private static void updateTickCounters() {
      GuiScreen gui = Minecraft.getMinecraft().currentScreen;
      if (gui == null || !gui.doesGuiPauseGame()) {
         ++ticksInGame;
         partialTicks = 0.0F;
      }

      float oldTotal = totalTicks;
      totalTicks = (float)ticksInGame + partialTicks;
      deltaTicks = totalTicks - oldTotal;
   }
}
