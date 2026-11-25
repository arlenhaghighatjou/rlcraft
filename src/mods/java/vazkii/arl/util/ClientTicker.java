package vazkii.arl.util;

import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(
   value = {Side.CLIENT},
   modid = "autoreglib"
)
public final class ClientTicker {
   public static int ticksInGame = 0;
   public static float partialTicks = 0.0F;
   public static float delta = 0.0F;
   public static float total = 0.0F;
   private static Queue<Runnable> pendingActions = new ArrayDeque();

   public ClientTicker() {
   }

   public static void addAction(Runnable action) {
      if (FMLCommonHandler.instance().getSide().isClient()) {
         pendingActions.add(action);
      }

   }

   @SideOnly(Side.CLIENT)
   private static void calcDelta() {
      float oldTotal = total;
      total = (float)ticksInGame + partialTicks;
      delta = total - oldTotal;
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void renderTick(RenderTickEvent event) {
      if (event.phase == Phase.START) {
         partialTicks = event.renderTickTime;
      } else {
         calcDelta();
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void clientTickEnd(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         GuiScreen gui = Minecraft.getMinecraft().currentScreen;
         if (gui == null || !gui.doesGuiPauseGame()) {
            ++ticksInGame;
            partialTicks = 0.0F;
         }

         while(!pendingActions.isEmpty()) {
            Runnable action = (Runnable)pendingActions.poll();
            if (action != null) {
               action.run();
            }
         }

         calcDelta();
      }

   }
}
