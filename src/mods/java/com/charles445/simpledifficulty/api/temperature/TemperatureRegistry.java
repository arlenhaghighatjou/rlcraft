package com.charles445.simpledifficulty.api.temperature;

import java.util.LinkedHashMap;

public class TemperatureRegistry {
   public static LinkedHashMap<String, ITemperatureModifier> modifiers = new LinkedHashMap();
   public static LinkedHashMap<String, ITemperatureDynamicModifier> dynamicModifiers = new LinkedHashMap();

   public TemperatureRegistry() {
   }

   public static void registerModifier(ITemperatureModifier modifier) {
      modifiers.put(modifier.getName(), modifier);
   }

   public static void registerDynamicModifier(ITemperatureDynamicModifier modifier) {
      dynamicModifiers.put(modifier.getName(), modifier);
   }
}
