package org.orecruncher.dsurround.client.handlers;

import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.handlers.fog.BedrockFogRangeCalculator;
import org.orecruncher.dsurround.client.handlers.fog.BiomeFogColorCalculator;
import org.orecruncher.dsurround.client.handlers.fog.BiomeFogRangeCalculator;
import org.orecruncher.dsurround.client.handlers.fog.FixedFogRangeCalculator;
import org.orecruncher.dsurround.client.handlers.fog.FogResult;
import org.orecruncher.dsurround.client.handlers.fog.HazeFogRangeCalculator;
import org.orecruncher.dsurround.client.handlers.fog.HolisticFogColorCalculator;
import org.orecruncher.dsurround.client.handlers.fog.HolisticFogRangeCalculator;
import org.orecruncher.dsurround.client.handlers.fog.MorningFogRangeCalculator;
import org.orecruncher.dsurround.client.handlers.fog.SeasonFogRangeCalculator;
import org.orecruncher.dsurround.client.handlers.fog.WeatherFogRangeCalculator;
import org.orecruncher.dsurround.event.DiagnosticEvent;
import org.orecruncher.dsurround.lib.OutOfBandTimerEMA;
import org.orecruncher.dsurround.lib.compat.ModEnvironment;
import org.orecruncher.dsurround.registry.RegistryDataEvent;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.effect.EffectRegistry;
import org.orecruncher.dsurround.registry.effect.theme.ThemeInfo;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.math.TimerEMA;

@SideOnly(Side.CLIENT)
public class FogHandler extends EffectHandlerBase {
   private final TimerEMA timer = new OutOfBandTimerEMA("Fog Render");
   private long nanos;
   private ThemeInfo theme;
   protected HolisticFogColorCalculator fogColor = new HolisticFogColorCalculator();
   protected HolisticFogRangeCalculator fogRange = new HolisticFogRangeCalculator();

   public FogHandler() {
      super("Fog Handler");
   }

   private boolean doFog() {
      return ModOptions.fog.enableFogProcessing && EnvironStateHandler.EnvironState.getDimensionInfo().hasFog();
   }

   public void process(@Nonnull EntityPlayer player) {
      if (this.doFog()) {
         this.fogRange.tick();
         this.fogColor.tick();
      }

      this.timer.update((double)this.nanos);
      this.nanos = 0L;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void fogColorEvent(FogColors event) {
      if (this.doFog()) {
         long start = System.nanoTime();
         Material material = event.getState().getMaterial();
         if (material != Material.LAVA && material != Material.WATER) {
            Color color = this.fogColor.calculate(event);
            event.setRed(color.red);
            event.setGreen(color.green);
            event.setBlue(color.blue);
         }

         this.nanos += System.nanoTime() - start;
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void fogRenderEvent(RenderFogEvent event) {
      if (this.doFog()) {
         long start = System.nanoTime();
         Material material = event.getState().getMaterial();
         if (material != Material.LAVA && material != Material.WATER) {
            FogResult result = this.fogRange.calculate(event);
            GlStateManager.setFogStart(result.getStart());
            GlStateManager.setFogEnd(result.getEnd());
         }

         this.nanos += System.nanoTime() - start;
      }

   }

   @SubscribeEvent
   public void diagnostics(DiagnosticEvent.Gather event) {
      event.output.add("Theme: " + this.theme.name());
      if (this.doFog()) {
         event.output.add("Fog Range: " + this.fogRange.toString());
         event.output.add("Fog Color: " + this.fogColor.toString());
      } else {
         event.output.add("FOG: IGNORED");
      }

   }

   public void onConnect() {
      ((DiagnosticHandler)EffectManager.instance().lookupService(DiagnosticHandler.class)).addTimer(this.timer);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onWorldLoad(@Nonnull Load event) {
      if (event.getWorld().isRemote) {
         this.setupTheme(RegistryManager.EFFECTS);
      }
   }

   @SubscribeEvent
   public void onConfigurationChanged(@Nonnull RegistryDataEvent.Reload event) {
      if (event.reg instanceof EffectRegistry) {
         this.setupTheme((EffectRegistry)event.reg);
      }

   }

   protected void setupTheme(@Nonnull EffectRegistry reg) {
      this.theme = reg.setTheme(EffectRegistry.DEFAULT_THEME);
      this.fogColor = new HolisticFogColorCalculator();
      this.fogRange = new HolisticFogRangeCalculator();
      if (this.theme.doBiomeFog()) {
         this.fogColor.add(new BiomeFogColorCalculator());
         this.fogRange.add(new BiomeFogRangeCalculator());
      }

      if (this.theme.doElevationHaze()) {
         this.fogRange.add(new HazeFogRangeCalculator());
      }

      if (this.theme.doMorningFog()) {
         if (ModEnvironment.SereneSeasons.isLoaded()) {
            this.fogRange.add(new SeasonFogRangeCalculator());
         } else {
            this.fogRange.add(new MorningFogRangeCalculator());
         }
      }

      if (this.theme.doBedrockFog()) {
         this.fogRange.add(new BedrockFogRangeCalculator());
      }

      if (this.theme.doWeatherFog()) {
         this.fogRange.add(new WeatherFogRangeCalculator());
      }

      if (this.theme.doFixedFog()) {
         this.fogRange.add(new FixedFogRangeCalculator(this.theme.getMinFogDistance(), this.theme.getMaxFogDistance()));
      }

   }
}
