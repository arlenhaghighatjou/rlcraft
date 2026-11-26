package codersafterdark.reskillable.client.base;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ClientTickHandler {
   public static volatile Queue<Runnable> scheduledActions = new ArrayDeque();
   public static int ticksInGame;
   public static float partialTicks;
   public static float delta;
   public static float total;

   public ClientTickHandler() {
   }

   private static void calcDelta() {
      float oldTotal = total;
      total = (float)ticksInGame + partialTicks;
      delta = total - oldTotal;
   }

   public static void register(EventBus bus) {
      bus.register(RenderTickEvent.class, ClientTickHandler::renderTick);
      bus.register(ClientTickEvent.class, ClientTickHandler::clientTickEnd);
   }

   public static void renderTick(RenderTickEvent event) {
      if (event.phase == Phase.START) {
         partialTicks = event.renderTickTime;
      }

   }

   public static void clientTickEnd(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         Minecraft mc = Minecraft.getMinecraft();
         if (mc.world == null) {
            PlayerDataHandler.cleanup();
         } else if (mc.player != null) {
            while(!scheduledActions.isEmpty()) {
               ((Runnable)scheduledActions.poll()).run();
            }
         }

         GuiScreen gui = mc.currentScreen;
         if (gui == null || !gui.doesGuiPauseGame()) {
            ++ticksInGame;
            partialTicks = 0.0F;
            HUDHandler.tick();
         }

         calcDelta();
      }

   }
}
