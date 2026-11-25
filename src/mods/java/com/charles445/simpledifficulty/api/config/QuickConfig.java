package com.charles445.simpledifficulty.api.config;

public class QuickConfig {
   private static boolean temperatureEnabled;
   private static boolean thirstEnabled;

   public QuickConfig() {
   }

   public static boolean isTemperatureEnabled() {
      return temperatureEnabled;
   }

   public static boolean isThirstEnabled() {
      return thirstEnabled;
   }

   protected static void updateValues() {
      temperatureEnabled = ServerConfig.instance.getBoolean(ServerOptions.TEMPERATURE_ENABLED);
      thirstEnabled = ServerConfig.instance.getBoolean(ServerOptions.THIRST_ENABLED);
   }
}
