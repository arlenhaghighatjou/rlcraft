package org.orecruncher.dsurround.expression;

import java.util.function.Supplier;
import net.minecraft.world.World;
import org.orecruncher.dsurround.capabilities.CapabilitySeasonInfo;
import org.orecruncher.dsurround.capabilities.season.ISeasonInfo;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.lib.expression.DynamicVariantList;
import org.orecruncher.lib.expression.Dynamic.DynamicBoolean;
import org.orecruncher.lib.expression.Dynamic.DynamicNumber;
import org.orecruncher.lib.expression.Dynamic.DynamicString;

public class WeatherVariables extends DynamicVariantList {
   public WeatherVariables() {
      this.add(new DynamicBoolean("weather.isRaining", () -> {
         return Weather.isRaining();
      }));
      this.add(new DynamicBoolean("weather.isThundering", () -> {
         return Weather.isThundering();
      }));
      this.add(new DynamicString("weather.temperature", () -> {
         return EnvironStateHandler.EnvironState.getBiomeTemperature().getValue();
      }));
      this.add(new DynamicBoolean("weather.isNotRaining", () -> {
         return !Weather.isRaining();
      }));
      this.add(new DynamicBoolean("weather.isNotThundering", () -> {
         return !Weather.isThundering();
      }));
      this.add(new DynamicNumber("weather.rainfall", () -> {
         return Weather.getIntensityLevel();
      }));
      this.add(new DynamicNumber("weather.temperatureValue", () -> {
         World world = EnvironStateHandler.EnvironState.getWorld();
         ISeasonInfo season = CapabilitySeasonInfo.getCapability(world);
         return season.getTemperature(EnvironStateHandler.EnvironState.getPlayerPosition());
      }));
      this.add(new DynamicBoolean("weather.canWaterFreeze", () -> {
         World world = EnvironStateHandler.EnvironState.getWorld();
         ISeasonInfo season = CapabilitySeasonInfo.getCapability(world);
         return season.canWaterFreeze(EnvironStateHandler.EnvironState.getPlayerPosition());
      }));
   }
}
