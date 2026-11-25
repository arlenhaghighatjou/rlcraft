package com.ferreusveritas.dynamictrees.systems;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.block.Block;

public class DirtHelper {
   public static final String DIRTLIKE = "dirtlike";
   public static final String SANDLIKE = "sandlike";
   public static final String GRAVELLIKE = "gravellike";
   public static final String WATERLIKE = "waterlike";
   public static final String NETHERLIKE = "netherlike";
   public static final String MUDLIKE = "mudlike";
   public static final String HARDCLAYLIKE = "hardclaylike";
   public static final String SLIMELIKE = "slimelike";
   public static final String FUNGUSLIKE = "funguslike";
   private static final Map<String, Integer> adjectiveMap = new HashMap();
   private static final Map<Block, Integer> dirtMap = new HashMap();

   public DirtHelper() {
   }

   public static void createNewAdjective(String adjName) {
      adjectiveMap.put(adjName, Integer.valueOf(1 << adjectiveMap.size()));
   }

   private static int getFlags(String adjName) {
      return ((Integer)adjectiveMap.getOrDefault(adjName, Integer.valueOf(0))).intValue();
   }

   public static void registerSoil(Block block, String adjName) {
      if (adjectiveMap.containsKey(adjName)) {
         int flag = ((Integer)adjectiveMap.get(adjName)).intValue();
         dirtMap.compute(block, (k, v) -> {
            return v == null ? flag : v.intValue() | flag;
         });
      } else {
         System.err.println("Adjective \"" + adjName + "\" not found while registering soil block: " + block);
      }

   }

   public static boolean isSoilAcceptable(Block block, int soilFlags) {
      return (((Integer)dirtMap.getOrDefault(block, Integer.valueOf(0))).intValue() & soilFlags) != 0;
   }

   public static int getSoilFlags(String... types) {
      int flags = 0;

      for(String t : types) {
         flags |= getFlags(t);
      }

      return flags;
   }

   static {
      createNewAdjective("dirtlike");
      createNewAdjective("sandlike");
      createNewAdjective("gravellike");
      createNewAdjective("waterlike");
      createNewAdjective("netherlike");
      createNewAdjective("mudlike");
      createNewAdjective("hardclaylike");
      createNewAdjective("slimelike");
      createNewAdjective("funguslike");
   }
}
