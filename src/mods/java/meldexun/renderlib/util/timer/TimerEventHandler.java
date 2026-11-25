package meldexun.renderlib.util.timer;

import java.util.ArrayList;
import java.util.List;
import meldexun.renderlib.config.RenderLibConfig;
import meldexun.renderlib.util.GLUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(
   modid = "renderlib",
   value = {Side.CLIENT}
)
public class TimerEventHandler {
   private static final ITimer gpuRenderTimer = tryCreateGLTimer("GPU", 100);
   private static final ITimer cpuRenderTimer = new CPUTimer("CPU (Render)", 100);
   private static final ITimer cpuTickTimer = new CPUTimer("CPU (Tick)", 100);
   public static final List<ITimer> timers = new ArrayList();

   public TimerEventHandler() {
   }

   public static ITimer tryCreateGLTimer(String name, int maxResultCount) {
      return (ITimer)(!GLUtil.CAPS.OpenGL33 && !GLUtil.CAPS.GL_ARB_timer_query ? new DummyTimer(name, "?") : new GLTimer(name, maxResultCount));
   }

   @SubscribeEvent
   public static void onRenderGameOverlayEvent(Post event) {
      if (event.getType() == ElementType.ALL) {
         if (RenderLibConfig.showFrameTimes) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution scaled = new ScaledResolution(mc);
            int x = scaled.getScaledWidth() - 2;
            int y = 2;
            drawLeftAligned(mc.fontRenderer, "avg", x - 40, y);
            drawLeftAligned(mc.fontRenderer, "max", x, y);

            for(int i = 0; i < timers.size(); ++i) {
               drawDebug(mc.fontRenderer, (ITimer)timers.get(i), x, y + (i + 1) * 10);
            }

         }
      }
   }

   private static void drawDebug(FontRenderer font, ITimer timer, int x, int y) {
      if (timer != null) {
         int nameWidth = font.getStringWidth(timer.getName());
         int avgWidth = font.getStringWidth(timer.avgString());
         int maxWidth = font.getStringWidth(timer.maxString());
         int avgOffset = Math.max(maxWidth + 4, 40);
         int nameOffset = Math.max(avgOffset + avgWidth + 4, 80);
         draw(font, timer.getName(), x - nameWidth - nameOffset, y);
         draw(font, timer.avgString(), x - avgWidth - avgOffset, y);
         draw(font, timer.maxString(), x - maxWidth, y);
      }
   }

   private static void drawLeftAligned(FontRenderer font, String string, int x, int y) {
      draw(font, string, x - font.getStringWidth(string), y);
   }

   private static void draw(FontRenderer font, String string, int x, int y) {
      font.drawStringWithShadow(string, (float)x, (float)y, -1);
   }

   @SubscribeEvent
   public static void onRenderTickEvent(RenderTickEvent event) {
      if (event.phase == Phase.START) {
         cpuRenderTimer.update();
         cpuRenderTimer.start();
         gpuRenderTimer.update();
         gpuRenderTimer.start();
      } else {
         gpuRenderTimer.stop();
         cpuRenderTimer.stop();
      }

   }

   @SubscribeEvent
   public static void onRenderTickEvent(ClientTickEvent event) {
      if (event.phase == Phase.START) {
         cpuTickTimer.update();
         cpuTickTimer.start();
      } else {
         cpuTickTimer.stop();
      }

   }

   static {
      timers.add(gpuRenderTimer);
      timers.add(cpuRenderTimer);
      timers.add(cpuTickTimer);
   }
}
