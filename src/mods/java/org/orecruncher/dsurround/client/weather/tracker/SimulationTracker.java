package org.orecruncher.dsurround.client.weather.tracker;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.dimension.IDimensionInfo;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.dsurround.event.ThunderEvent;
import org.orecruncher.lib.PlayerUtils;
import org.orecruncher.lib.TimeUtils;
import org.orecruncher.lib.math.MathStuff;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class SimulationTracker extends Tracker {
   protected float intensityLevel = 0.0F;
   protected float maxIntensityLevel = 0.0F;
   protected int nextThunderEvent = 0;
   protected Weather.Properties intensity = Weather.Properties.NONE;
   protected Random random;

   public SimulationTracker() {
   }

   protected String type() {
      return "SIMULATION";
   }

   @Nonnull
   public Weather.Properties getWeatherProperties() {
      return this.intensity;
   }

   public float getIntensityLevel() {
      return this.intensityLevel;
   }

   public float getMaxIntensityLevel() {
      return this.maxIntensityLevel;
   }

   public int getNextThunderEvent() {
      return this.nextThunderEvent;
   }

   public boolean doVanilla() {
      return false;
   }

   public boolean backgroundThunderPossible() {
      if (!ModOptions.rain.allowBackgroundThunder) {
         return false;
      } else {
         int dimId = EnvironStateHandler.EnvironState.getDimensionId();
         return dimId != -1 && dimId != 1 && this.isThundering() && this.getIntensityLevel() >= ModOptions.rain.stormThunderThreshold;
      }
   }

   public void update() {
      IDimensionInfo info = EnvironStateHandler.EnvironState.getDimensionInfo();
      if (info != null && info.hasWeather()) {
         this.updateRainState();
         this.doAmbientThunder();
      } else {
         this.intensity = Weather.Properties.NONE;
         this.intensityLevel = 0.0F;
         this.maxIntensityLevel = 0.0F;
         this.nextThunderEvent = 0;
      }

   }

   private void updateRainState() {
      float vanillaIntensity = super.getIntensityLevel();
      if (vanillaIntensity > 0.0F && this.intensityLevel == 0.0F) {
         this.random = new XorShiftRandom(generateSeed());
         float delta = ModOptions.rain.defaultMaxRainStrength - ModOptions.rain.defaultMinRainStrength;
         float result;
         if (delta <= 0.0F) {
            result = ModOptions.rain.defaultMinRainStrength;
         } else {
            float mid = delta / 2.0F;
            result = ModOptions.rain.defaultMinRainStrength + (this.random.nextFloat() + this.random.nextFloat()) * mid;
         }

         this.maxIntensityLevel = MathStuff.clamp(result, 0.01F, 1.0F);
      } else if (vanillaIntensity == 0.0F && this.intensityLevel > 0.0F) {
         this.maxIntensityLevel = 0.0F;
         this.nextThunderEvent = 0;
         this.random = null;
      }

      float newIntensity = MathStuff.clamp(vanillaIntensity, 0.0F, this.maxIntensityLevel);
      this.setCurrentIntensity(newIntensity);
   }

   protected void setCurrentIntensity(float level) {
      this.intensity = Weather.Properties.mapRainStrength(level);
      if (this.intensity == Weather.Properties.VANILLA) {
         this.intensityLevel = 0.0F;
      } else {
         this.intensityLevel = MathStuff.clamp(level, 0.0F, 1.0F);
      }

   }

   private static long generateSeed() {
      return TimeUtils.getGMTDaySeedBase() + (long)EnvironStateHandler.EnvironState.getClock().getDay();
   }

   private void doAmbientThunder() {
      if (this.backgroundThunderPossible()) {
         int time = this.nextThunderEvent - 1;
         if (time <= 0) {
            float intensity = this.getIntensityLevel();
            if (time == 0) {
               EntityPlayer player = PlayerUtils.getRandomPlayer(EnvironStateHandler.EnvironState.getWorld());
               if (player != null) {
                  float theY = (float)EnvironStateHandler.EnvironState.getDimensionInfo().getSkyHeight();
                  BlockPos pos = new BlockPos(player.posX, (double)theY, player.posZ);
                  MinecraftForge.EVENT_BUS.post(new ThunderEvent(player.getEntityWorld().provider.getDimension(), this.doFlash(intensity), pos));
               }
            }

            time = this.nextThunderEvent(intensity);
         }

         this.nextThunderEvent = time;
      } else {
         this.nextThunderEvent = 0;
      }

   }

   private int nextThunderEvent(float rainIntensity) {
      float scale = 2.0F - rainIntensity;
      return this.random.nextInt((int)(450.0F * scale)) + 300;
   }

   protected boolean doFlash(float rainIntensity) {
      int randee = (int)(rainIntensity * 100.0F);
      return this.random.nextInt(150) <= randee;
   }
}
