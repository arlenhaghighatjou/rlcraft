package org.orecruncher.dsurround.server.services;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilityDimensionInfo;
import org.orecruncher.dsurround.capabilities.dimension.IDimensionInfoEx;
import org.orecruncher.dsurround.network.Network;
import org.orecruncher.dsurround.network.PacketThunder;
import org.orecruncher.dsurround.network.PacketWeatherUpdate;
import org.orecruncher.lib.PlayerUtils;
import org.orecruncher.lib.random.XorShiftRandom;

public class WeatherGenerator {
   protected final EaglercraftRandom RANDOM = XorShiftRandom.current();
   protected final World world;
   protected final IDimensionInfoEx data;

   public WeatherGenerator(@Nonnull World world) {
      this.world = world;
      this.data = (IDimensionInfoEx)CapabilityDimensionInfo.getCapability(world);
   }

   @Nonnull
   public String name() {
      return "STANDARD";
   }

   protected int nextThunderEvent(float rainIntensity) {
      float scale = 2.0F - rainIntensity;
      return this.RANDOM.nextInt((int)(450.0F * scale)) + 300;
   }

   protected boolean doFlash(float rainIntensity) {
      int randee = (int)(rainIntensity * 100.0F);
      return this.RANDOM.nextInt(150) <= randee;
   }

   protected WorldInfo worldInfo() {
      return this.world.getWorldInfo();
   }

   protected void preProcess() {
   }

   protected void doRain() {
      if (this.worldInfo().isRaining()) {
         if (this.data.getRainIntensity() == 0.0F) {
            this.data.randomizeRain();
            ModBase.log().debug("dim %d rain intensity set to %f, duration %d ticks", new Object[]{this.data.getId(), this.data.getRainIntensity(), this.worldInfo().getRainTime()});
         }

         this.data.setCurrentRainIntensity(this.world.getRainStrength(1.0F));
      } else if (this.world.getRainStrength(1.0F) > 0.0F) {
         this.data.setCurrentRainIntensity(this.world.getRainStrength(1.0F));
      } else if (this.data.getCurrentRainIntensity() > 0.0F) {
         this.data.setRainIntensity(0.0F);
         this.data.setCurrentRainIntensity(0.0F);
         ModBase.log().debug("dim %d rain has stopped, next rain %d ticks", new Object[]{this.data.getId(), this.worldInfo().getRainTime()});
      } else if (this.data.getRainIntensity() > 0.0F) {
         this.data.setRainIntensity(0.0F);
      }

   }

   protected void doAmbientThunder() {
      if (ModOptions.rain.allowBackgroundThunder) {
         float intensity = this.data.getCurrentRainIntensity();
         if (this.worldInfo().isThundering() && intensity >= ModOptions.rain.stormThunderThreshold) {
            int time = this.data.getThunderTimer() - 1;
            if (time <= 0) {
               if (time == 0) {
                  EntityPlayer player = PlayerUtils.getRandomPlayer(this.world);
                  float theY = (float)this.data.getSkyHeight();
                  if (player != null) {
                     PacketThunder packet = new PacketThunder(this.data.getId(), this.doFlash(intensity), new BlockPos(player.posX, (double)theY, player.posZ));
                     Network.sendToDimension(this.data.getId(), packet);
                  }
               }

               time = this.nextThunderEvent(intensity);
            }

            this.data.setThunderTimer(time);
         } else {
            this.data.setThunderTimer(0);
         }

      }
   }

   protected void postProcess() {
   }

   public final void update() {
      this.process();
      this.sendUpdate();
   }

   protected void process() {
      this.preProcess();
      this.doRain();
      this.doAmbientThunder();
      this.postProcess();
   }

   protected void sendUpdate() {
      if (this.world.playerEntities.size() > 0) {
         PacketWeatherUpdate packet = new PacketWeatherUpdate(this.data.getId(), this.data.getCurrentRainIntensity(), this.data.getRainIntensity(), this.worldInfo().getRainTime(), this.world.getThunderStrength(1.0F), this.worldInfo().getThunderTime(), this.data.getThunderTimer());
         Network.sendToDimension(this.data.getId(), packet);
      }

   }
}
