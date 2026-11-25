package com.charles445.simpledifficulty.api.config;

public enum ServerOptions implements IConfigOption {
   DEBUG("debug"),
   THIRST_ENABLED("thirstEnabled"),
   THIRST_DRINK_BLOCKS("thirstDrinkBlocks"),
   THIRST_DRINK_RAIN("thirstDrinkRain"),
   PEACEFUL_DANGER("peacefulDanger"),
   TEMPERATURE_ENABLED("temperatureEnabled"),
   TEMPERATURE_TE_ENABLED("temperatureTEEnabled"),
   CANTEEN_DOSES("canteenDoses"),
   STRICT_HEATERS("strictHeaters"),
   IRON_CANTEEN_DOSES("ironCanteenDoses"),
   INFINITE_PURIFIED_WATER("infinitePurifiedWater");

   String name;

   private ServerOptions(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.getName();
   }
}
