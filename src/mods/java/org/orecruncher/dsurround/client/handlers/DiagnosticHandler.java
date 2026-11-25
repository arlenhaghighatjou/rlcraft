package org.orecruncher.dsurround.client.handlers;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.sound.SoundEngine;
import org.orecruncher.dsurround.event.DiagnosticEvent;
import org.orecruncher.dsurround.lib.OutOfBandTimerEMA;
import org.orecruncher.lib.math.MathStuff;
import org.orecruncher.lib.math.TimerEMA;

@SideOnly(Side.CLIENT)
public class DiagnosticHandler extends EffectHandlerBase {
   private List<String> diagnostics;
   private List<String> timings;
   private List<String> serverDataReport = ImmutableList.of();
   private final List<TimerEMA> timers = new ArrayList();
   private final TimerEMA clientTick = new TimerEMA("Client Tick");
   private final TimerEMA lastTick = new TimerEMA("Last Tick");
   private long timeMark;
   private long lastTickMark = -1L;
   private float tps = 0.0F;
   private TimerEMA processingTimer;
   private TimerEMA diagnosticTimer;

   public DiagnosticHandler() {
      super("Diagnostics");
   }

   public void addTimer(@Nonnull TimerEMA timer) {
      this.timers.add(timer);
      if ("Processing".equals(timer.name())) {
         this.processingTimer = timer;
      } else if ("Diagnostics".equals(timer.name())) {
         this.diagnosticTimer = timer;
      }

   }

   public void process(@Nonnull EntityPlayer player) {
      if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
         this.diagnostics = new ArrayList();
         this.timings = new ArrayList();
         if (ModBase.isDeveloperMode()) {
            this.diagnostics.add(TextFormatting.RED + "DEVELOPER MODE ENABLED");
         }

         if (ModOptions.logging.enableDebugLogging) {
            DiagnosticEvent.Gather gather = new DiagnosticEvent.Gather(player.getEntityWorld(), player);
            MinecraftForge.EVENT_BUS.post(gather);
            this.diagnostics.add("");
            this.diagnostics.addAll(gather.output);
            double adjusted = this.processingTimer.getMSecs() - this.diagnosticTimer.getMSecs();
            this.timings.add("");
            this.timings.add(TextFormatting.LIGHT_PURPLE + this.clientTick.toString());
            this.timings.add(TextFormatting.LIGHT_PURPLE + this.lastTick.toString());
            this.timings.add(TextFormatting.LIGHT_PURPLE + String.format("TPS:%7.3fms", this.tps));

            for(TimerEMA timer : this.timers) {
               boolean oob = timer instanceof OutOfBandTimerEMA;
               this.timings.add((oob ? TextFormatting.GRAY : TextFormatting.AQUA) + timer.toString());
               if (oob) {
                  adjusted += timer.getMSecs();
               }
            }

            String txt = String.format(TextFormatting.YELLOW + "%s:%7.3fms", "Impact", adjusted);
            this.timings.add(txt);
         }

         if (this.serverDataReport != null && !this.serverDataReport.isEmpty()) {
            this.timings.add("");
            this.timings.addAll(this.serverDataReport);
         }
      } else {
         this.diagnostics = null;
         this.timings = null;
      }

   }

   public void onConnect() {
      this.diagnostics = null;
      this.serverDataReport = null;
   }

   public void onDisconnect() {
      this.diagnostics = null;
      this.serverDataReport = null;
      this.timers.clear();
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void tickStart(@Nonnull ClientTickEvent event) {
      if (event.phase == Phase.START) {
         this.timeMark = System.nanoTime();
         if (this.lastTickMark != -1L) {
            this.lastTick.update((double)(this.timeMark - this.lastTickMark));
            this.tps = MathStuff.clamp((float)(50.0D / this.lastTick.getMSecs() * 20.0D), 0.0F, 20.0F);
         }

         this.lastTickMark = this.timeMark;
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void worldLoad(@Nonnull Load event) {
      if (ModOptions.logging.enableDebugLogging && event.getWorld() instanceof WorldClient) {
         IRenderHandler handler = event.getWorld().provider.getWeatherRenderer();
         ModBase.log().debug("World class     : %s", new Object[]{event.getWorld().getClass().getName()});
         ModBase.log().debug("World Provider  : %s", new Object[]{event.getWorld().provider.getClass().getName()});
         ModBase.log().debug("Weather Renderer: %s", new Object[]{handler != null ? handler.getClass().getName() : "VANILLA"});
         ModBase.log().debug("Entity Renderer : %s", new Object[]{Minecraft.getMinecraft().entityRenderer.getClass().getName()});
         ModBase.log().debug("Particle Manager: %s", new Object[]{Minecraft.getMinecraft().effectRenderer.getClass().getName()});
         ModBase.log().debug("Music Ticker    : %s", new Object[]{Minecraft.getMinecraft().getMusicTicker().getClass().getName()});
         ModBase.log().debug("Sound Manager   : %s", new Object[]{SoundEngine.instance().getSoundManager().getClass().getName()});
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void tickEnd(@Nonnull ClientTickEvent event) {
      if (event.phase == Phase.END) {
         this.clientTick.update((double)(System.nanoTime() - this.timeMark));
      }

   }

   @SubscribeEvent
   public void onGatherText(@Nonnull Text event) {
      if (this.diagnostics != null && !this.diagnostics.isEmpty()) {
         event.getLeft().addAll(this.diagnostics);
      }

      if (this.timings != null && !this.timings.isEmpty()) {
         event.getRight().addAll(this.timings);
      }

   }

   public void setServerTPSReport(@Nonnull List<String> report) {
      this.serverDataReport = report;
   }
}
