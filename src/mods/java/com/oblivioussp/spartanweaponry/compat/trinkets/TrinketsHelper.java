package com.oblivioussp.spartanweaponry.compat.trinkets;

import net.minecraft.entity.Entity;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.race.EntityProperties;

public class TrinketsHelper {
   public static final String RACE_TITAN = "Titan";

   public TrinketsHelper() {
   }

   public static String getEntityRace(Entity entity) {
      try {
         EntityProperties raceProp = Capabilities.getEntityRace(entity);
         if (raceProp != null) {
            return raceProp.getCurrentRace().getName();
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      return "";
   }
}
