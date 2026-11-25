package org.orecruncher.dsurround.expression;

import java.util.function.Supplier;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.expression.DynamicVariantList;
import org.orecruncher.lib.expression.Dynamic.DynamicNumber;
import org.orecruncher.lib.expression.Dynamic.DynamicString;

public class BiomeVariables extends DynamicVariantList {
   public BiomeVariables() {
      this.add(new DynamicString("biome.name", () -> {
         return EnvironStateHandler.EnvironState.getBiomeName();
      }));
      this.add(new DynamicString("biome.id", () -> {
         return EnvironStateHandler.EnvironState.getPlayerBiome().getKey().toString();
      }));
      this.add(new DynamicString("biome.modid", () -> {
         return EnvironStateHandler.EnvironState.getPlayerBiome().getKey().getResourceDomain();
      }));
      this.add(new DynamicString("biome.temperature", () -> {
         return EnvironStateHandler.EnvironState.getBiomeTemperature().getValue();
      }));
      this.add(new DynamicNumber("biome.rainfall", () -> {
         return EnvironStateHandler.EnvironState.getPlayerBiome().getRainfall();
      }));
      this.add(new DynamicNumber("biome.temperatureValue", () -> {
         return EnvironStateHandler.EnvironState.getPlayerBiome().getTemperature();
      }));
      this.add(new DynamicString("biome.traits", () -> {
         return EnvironStateHandler.EnvironState.getPlayerBiome().getBiomeTraits();
      }));
   }
}
