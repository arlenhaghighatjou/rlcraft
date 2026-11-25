package com.teamacronymcoders.contenttweaker.api;

import java.util.HashMap;
import java.util.Map;

public class Mappings {
   private static Map<String, String> soundEventMappings;
   private static Map<String, String> materialMappings;

   public Mappings() {
   }

   public static Map<String, String> getSoundEventMappings() {
      if (soundEventMappings == null) {
         soundEventMappings = new HashMap();
         soundEventMappings.put("ANVIL", "anvil");
         soundEventMappings.put("CLOTH", "cloth");
         soundEventMappings.put("GLASS", "glass");
         soundEventMappings.put("GROUND", "ground");
         soundEventMappings.put("LADDER", "ladder");
         soundEventMappings.put("METAL", "metal");
         soundEventMappings.put("PLANT", "plant");
         soundEventMappings.put("SAND", "sand");
         soundEventMappings.put("SNOW", "snow");
         soundEventMappings.put("STONE", "stone");
         soundEventMappings.put("WOOD", "wood");
      }

      return soundEventMappings;
   }

   public static Map<String, String> getMaterialMappings() {
      if (materialMappings == null) {
         materialMappings = new HashMap();
         materialMappings.put("AIR", "air");
         materialMappings.put("ANVIL", "anvil");
         materialMappings.put("BARRIER", "barrier");
         materialMappings.put("CACTUS", "cactus");
         materialMappings.put("CAKE", "cake");
         materialMappings.put("CARPET", "carpet");
         materialMappings.put("CIRCUITS", "circuits");
         materialMappings.put("CLAY", "clay");
         materialMappings.put("CLOTH", "cloth");
         materialMappings.put("CORAL", "coral");
         materialMappings.put("CRAFTED_SNOW", "crafted_snow");
         materialMappings.put("DRAGON_EGG", "dragon_egg");
         materialMappings.put("FIRE", "fire");
         materialMappings.put("GLASS", "glass");
         materialMappings.put("GOURD", "gourd");
         materialMappings.put("GRASS", "grass");
         materialMappings.put("GROUND", "ground");
         materialMappings.put("ICE", "ice");
         materialMappings.put("IRON", "iron");
         materialMappings.put("LAVA", "lava");
         materialMappings.put("LEAVES", "leaves");
         materialMappings.put("PACKED_ICE", "packed_ice");
         materialMappings.put("PISTON", "piston");
         materialMappings.put("PLANTS", "plants");
         materialMappings.put("PORTAL", "portal");
         materialMappings.put("REDSTONE_LIGHT", "redstone_light");
         materialMappings.put("ROCK", "rock");
         materialMappings.put("SAND", "sand");
         materialMappings.put("SNOW", "snow");
         materialMappings.put("SPONGE", "sponge");
         materialMappings.put("STRUCTURE_VOID", "structure_void");
         materialMappings.put("TNT", "tnt");
         materialMappings.put("VINE", "vine");
         materialMappings.put("WATER", "water");
         materialMappings.put("WEB", "web");
         materialMappings.put("WOOD", "wood");
      }

      return materialMappings;
   }
}
