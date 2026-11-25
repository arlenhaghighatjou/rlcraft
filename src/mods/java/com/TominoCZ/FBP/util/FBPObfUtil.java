package com.TominoCZ.FBP.util;

import java.util.HashMap;

public class FBPObfUtil {
   private static HashMap<String, String> map = new HashMap();

   public FBPObfUtil() {
   }

   public static String translateObfMaterialName(String fieldName) {
      return !map.containsKey(fieldName) ? fieldName : (String)map.get(fieldName);
   }

   static {
      String obfNames = "AIR,GRASS,GROUND,WOOD,ROCK,IRON,ANVIL,WATER,LAVA,LEAVES,PLANTS,VINE,SPONGE,CLOTH,FIRE,SAND,CIRCUITS,CARPET,GLASS,REDSTONE_LIGHT,TNT,CORAL,ICE,PACKED_ICE,SNOW,CRAFTED_SNOW,CACTUS,CLAY,GOURD,DRAGON_EGG,PORTAL,CAKE,WEB,PISTON,BARRIER,STRUCTURE_VOID";
      String deobfNames = "AIR,GRASS,GROUND,WOOD,ROCK,IRON,ANVIL,WATER,LAVA,LEAVES,PLANTS,VINE,SPONGE,CLOTH,FIRE,SAND,CIRCUITS,CARPET,GLASS,REDSTONE_LIGHT,TNT,CORAL,ICE,PACKED_ICE,SNOW,CRAFTED_SNOW,CACTUS,CLAY,GOURD,DRAGON_EGG,PORTAL,CAKE,WEB,PISTON,BARRIER,STRUCTURE_VOID";
      String[] obf = obfNames.split(",");
      String[] deobf = deobfNames.split(",");

      for(int i = 0; i < obf.length; ++i) {
         map.put(obf[i], deobf[i].toLowerCase());
      }

   }
}
