package com.charles445.simpledifficulty.api.config;

public enum ClientOptions implements IConfigOption {
   DEBUG("debug"),
   DRAW_THIRST_SATURATION("drawThirstSaturation"),
   ENABLE_THERMOMETER("enableThermometer"),
   ALTERNATE_TEMP("alternateTemp"),
   HUD_THERMOMETER("hudThermometer"),
   HUD_THERMOMETERX("hudThermometerX"),
   HUD_THERMOMETERY("hudThermometerY"),
   TEMPERATURE_READOUT("temperatureReadout"),
   CLASSICHUD_TEMPERATURE("classicHUDTemperature"),
   CLASSICHUD_THIRST("classicHUDThirst"),
   HEATER_PARTICLES("heaterParticles");

   String name;

   private ClientOptions(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.getName();
   }
}
