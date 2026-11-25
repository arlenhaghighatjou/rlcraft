package org.orecruncher.dsurround.client.handlers;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.aurora.AuroraFactory;
import org.orecruncher.dsurround.client.aurora.AuroraUtils;
import org.orecruncher.dsurround.client.aurora.IAurora;
import org.orecruncher.dsurround.event.DiagnosticEvent;
import org.orecruncher.dsurround.lib.OutOfBandTimerEMA;
import org.orecruncher.lib.DiurnalUtils.DayCycle;
import org.orecruncher.lib.math.TimerEMA;

@SideOnly(Side.CLIENT)
public final class AuroraEffectHandler extends EffectHandlerBase {
   private IAurora current;
   private int dimensionId;
   private final TimerEMA timer = new OutOfBandTimerEMA("Aurora Render");
   private long nanos;

   public AuroraEffectHandler() {
      super("Aurora Effect");
   }

   public void onConnect() {
      this.current = null;
      ((DiagnosticHandler)EffectManager.instance().lookupService(DiagnosticHandler.class)).addTimer(this.timer);
   }

   public void onDisconnect() {
      this.current = null;
   }

   private boolean isAuroraTimeOfDay() {
      DayCycle dc = EnvironStateHandler.EnvironState.getDayCycle();
      return dc == DayCycle.SUNSET || dc == DayCycle.NIGHTTIME;
   }

   private boolean canSpawnAurora() {
      return this.current == null && this.canAuroraStay();
   }

   private boolean canAuroraStay() {
      if (!ModOptions.aurora.auroraEnable) {
         return false;
      } else {
         return this.isAuroraTimeOfDay() && AuroraUtils.getChunkRenderDistance() >= 6 && AuroraUtils.dimensionHasAuroras() && EnvironStateHandler.EnvironState.getTruePlayerBiome().getHasAurora();
      }
   }

   public void process(@Nonnull EntityPlayer player) {
      if (this.current != null) {
         if (!this.current.isComplete() && this.dimensionId == EnvironStateHandler.EnvironState.getDimensionId() && ModOptions.aurora.auroraEnable) {
            this.current.update();
            boolean isDying = this.current.isDying();
            boolean canStay = this.canAuroraStay();
            if (isDying && canStay) {
               ModBase.log().debug("Unfading aurora...", new Object[0]);
               this.current.setFading(false);
            } else if (!isDying && !canStay) {
               ModBase.log().debug("Aurora fade...", new Object[0]);
               this.current.setFading(true);
            }
         } else {
            this.current = null;
         }
      }

      if (this.canSpawnAurora()) {
         this.current = AuroraFactory.produce(AuroraUtils.getSeed());
         ModBase.log().debug("New aurora [%s]", new Object[]{this.current.toString()});
      }

      this.dimensionId = EnvironStateHandler.EnvironState.getDimensionId();
      this.timer.update((double)this.nanos);
      this.nanos = 0L;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void doRender(@Nonnull RenderWorldLastEvent event) {
      if (this.current != null) {
         long start = System.nanoTime();
         this.current.render(event.getPartialTicks());
         this.nanos += System.nanoTime() - start;
      }

   }

   @SubscribeEvent
   public void diagnostic(@Nonnull DiagnosticEvent.Gather event) {
      event.output.add("Aurora: " + (this.current == null ? "NONE" : this.current.toString()));
   }
}
