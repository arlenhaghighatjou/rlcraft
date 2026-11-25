package org.orecruncher.dsurround.expression;

import java.util.function.Supplier;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.DiurnalUtils;
import org.orecruncher.lib.DiurnalUtils.DayCycle;
import org.orecruncher.lib.expression.DynamicVariantList;
import org.orecruncher.lib.expression.Dynamic.DynamicBoolean;
import org.orecruncher.lib.expression.Dynamic.DynamicNumber;

public class DiurnalVariables extends DynamicVariantList {
   public DiurnalVariables() {
      this.add(new DynamicBoolean("diurnal.isDay", () -> {
         return EnvironStateHandler.EnvironState.getDayCycle() == DayCycle.DAYTIME;
      }));
      this.add(new DynamicBoolean("diurnal.isNight", () -> {
         return EnvironStateHandler.EnvironState.getDayCycle() == DayCycle.NIGHTTIME;
      }));
      this.add(new DynamicBoolean("diurnal.isSunrise", () -> {
         return EnvironStateHandler.EnvironState.getDayCycle() == DayCycle.SUNRISE;
      }));
      this.add(new DynamicBoolean("diurnal.isSunset", () -> {
         return EnvironStateHandler.EnvironState.getDayCycle() == DayCycle.SUNSET;
      }));
      this.add(new DynamicBoolean("diurnal.isAuroraVisible", () -> {
         return EnvironStateHandler.EnvironState.getDayCycle() == DayCycle.SUNRISE || EnvironStateHandler.EnvironState.getDayCycle() == DayCycle.DAYTIME;
      }));
      this.add(new DynamicNumber("diurnal.moonPhaseFactor", () -> {
         return DiurnalUtils.getMoonPhaseFactor(EnvironStateHandler.EnvironState.getWorld());
      }));
      this.add(new DynamicNumber("diurnal.celestialAngle", () -> {
         return EnvironStateHandler.EnvironState.getWorld().getCelestialAngle(0.0F);
      }));
   }
}
