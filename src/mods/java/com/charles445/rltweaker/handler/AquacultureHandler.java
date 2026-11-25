package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.AquacultureReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.world.biome.Biome;

public class AquacultureHandler {
   private AquacultureReflect reflector;

   public AquacultureHandler() {
      try {
         this.reflector = new AquacultureReflect();
         if (ModConfig.server.aquaculture.fixNeptunesBounty) {
            this.fixNeptunesBounty();
         }

         if (ModConfig.server.aquaculture.fixFreshwaterBug) {
            this.fixFreshwaterBug();
         }
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup AquacultureHandler!", var2);
         ErrorUtil.logSilent("Aquaculture Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   public void fixFreshwaterBug() {
      try {
         Map<Integer, ArrayList<Object>> biomeMap = this.reflector.getBiomeMap();
         if (biomeMap == null) {
            RLTweaker.logger.error("Aquaculture had null biomeMap, failed to fix freshwater bug");
            ErrorUtil.logSilent("Aquaculture Fix Freshwater Bug Null BiomeMap");
            return;
         }

         Object freshwaterBiomeType = this.reflector.getFreshwaterBiomeType();
         if (freshwaterBiomeType == null) {
            RLTweaker.logger.error("Aquaculture had null freshwater BiomeType, failed to fix freshwater bug");
            ErrorUtil.logSilent("Aquaculture Fix Freshwater Bug Null Freshwater BiomeType");
            return;
         }

         for(Biome biome : Biome.REGISTRY) {
            int biomeID = Biome.getIdForBiome(biome);
            if (!biomeMap.containsKey(Integer.valueOf(biomeID))) {
               biomeMap.put(Integer.valueOf(biomeID), new ArrayList());
               ((ArrayList)biomeMap.get(Integer.valueOf(biomeID))).add(freshwaterBiomeType);
            }
         }
      } catch (IllegalAccessException | IllegalArgumentException var6) {
         var6.printStackTrace();
         ErrorUtil.logSilent("Aquaculture Fix Freshwater Bug Failure");
      }

   }

   public void fixNeptunesBounty() {
      try {
         if (!this.reflector.getNeptuniumLootEnabled()) {
            return;
         }

         this.reflector.addLoot(this.reflector.getNeptunesBountyLoot(), this.reflector.getNeptuniumBarStack(), 2, 1, 4);
      } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException var2) {
         var2.printStackTrace();
         ErrorUtil.logSilent("Aquaculture Neptunes Bounty Loot Failure");
      }

   }
}
