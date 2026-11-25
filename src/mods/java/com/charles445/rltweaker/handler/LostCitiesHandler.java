package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.LostCitiesReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LostCitiesHandler {
   private LostCitiesReflect reflector;
   private LostCitiesHandler.LCTerrainHandler terrainHandler;

   public LostCitiesHandler() {
      try {
         this.reflector = new LostCitiesReflect();
         this.terrainHandler = new LostCitiesHandler.LCTerrainHandler();
         MinecraftForge.TERRAIN_GEN_BUS.register(this.terrainHandler);
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup LostCitiesHandler!", var2);
         ErrorUtil.logSilent("LostCities Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   public class LCTerrainHandler {
      public LCTerrainHandler() {
      }

      @SubscribeEvent
      public void onDecorate(Decorate event) {
         World world = event.getWorld();
         if (!world.isRemote) {
            if (ModConfig.server.lostcities.generationCacheCleanupEnabled) {
               try {
                  if (!LostCitiesHandler.this.reflector.isLostCities(world)) {
                     return;
                  }
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var9) {
                  ErrorUtil.logSilent("LCTerrainHandler isLostCities Invocation");
                  return;
               }

               Object chunkGenerator = null;

               try {
                  chunkGenerator = LostCitiesHandler.this.reflector.getLostCityChunkGenerator(world);
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var8) {
                  ErrorUtil.logSilent("LCTerrainHandler getLostCityChunkGenerator Invocation");
                  return;
               }

               if (chunkGenerator != null) {
                  int generationCacheMaxCount = ModConfig.server.lostcities.generationCacheMaxCount;

                  try {
                     Map cachedPrimers = LostCitiesHandler.this.reflector.getCachedPrimers(chunkGenerator);
                     if (cachedPrimers.size() >= generationCacheMaxCount) {
                        RLTweaker.logger.debug("Clearing Lost Cities cachedPrimers: " + cachedPrimers.size());
                        cachedPrimers.clear();
                     }
                  } catch (IllegalAccessException | IllegalArgumentException var7) {
                     ErrorUtil.logSilent("LCTerrainHandler getCachedPrimers Invocation");
                  }

                  try {
                     Map cachedHeightmaps = LostCitiesHandler.this.reflector.getCachedHeightmaps(chunkGenerator);
                     if (cachedHeightmaps.size() >= generationCacheMaxCount) {
                        RLTweaker.logger.debug("Clearing Lost Cities cachedHeightmaps: " + cachedHeightmaps.size());
                        cachedHeightmaps.clear();
                     }
                  } catch (IllegalAccessException | IllegalArgumentException var6) {
                     ErrorUtil.logSilent("LCTerrainHandler getCachedHeightmaps Invocation");
                  }

               }
            }
         }
      }
   }
}
