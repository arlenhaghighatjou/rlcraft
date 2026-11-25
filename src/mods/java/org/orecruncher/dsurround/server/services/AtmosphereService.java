package org.orecruncher.dsurround.server.services;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import javax.annotation.Nonnull;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilityDimensionInfo;
import org.orecruncher.dsurround.capabilities.dimension.IDimensionInfo;
import org.orecruncher.dsurround.lib.compat.ModEnvironment;

public final class AtmosphereService extends Service {
   private final Int2ObjectOpenHashMap<WeatherGenerator> generators = new Int2ObjectOpenHashMap();

   AtmosphereService() {
      super("AtmosphereService");
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void tickEvent(@Nonnull WorldTickEvent event) {
      if (event.side == Side.SERVER && event.phase == Phase.END) {
         this.getGenerator(event.world).update();
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onWorldLoad(Load e) {
      World world = e.getWorld();
      if (!world.isRemote) {
         this.getGenerator(world);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onWorldUnload(Unload e) {
      World world = e.getWorld();
      if (!world.isRemote) {
         int dimId = world.provider.getDimension();
         this.generators.remove(dimId);
      }
   }

   private WeatherGenerator getGenerator(@Nonnull World world) {
      int dimId = world.provider.getDimension();
      WeatherGenerator result = (WeatherGenerator)this.generators.get(dimId);
      if (result == null) {
         this.generators.put(dimId, result = this.createGenerator(world));
      }

      return result;
   }

   private boolean doVanillaRain() {
      return ModOptions.rain.doVanillaRain || ModEnvironment.Weather2.isLoaded();
   }

   private WeatherGenerator createGenerator(@Nonnull World world) {
      WeatherGenerator result = null;
      IDimensionInfo info = CapabilityDimensionInfo.getCapability(world);
      if (info != null && info.hasWeather()) {
         int dimId = world.provider.getDimension();
         if (this.doVanillaRain()) {
            if (dimId != -1) {
               result = new WeatherGeneratorVanilla(world);
            }
         } else if (dimId == -1) {
            result = new WeatherGeneratorNether(world);
         } else {
            result = new WeatherGenerator(world);
         }
      }

      if (result == null) {
         result = new WeatherGeneratorNone(world);
      }

      ModBase.log().info("Creating %s weather generator for dimension [%s]", new Object[]{result.name(), world.provider.getDimensionType().getName()});
      return result;
   }
}
