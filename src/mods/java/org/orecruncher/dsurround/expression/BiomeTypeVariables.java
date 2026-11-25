package org.orecruncher.dsurround.expression;

import java.util.function.Supplier;
import net.minecraftforge.common.BiomeDictionary.Type;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.registry.biome.BiomeUtil;
import org.orecruncher.lib.expression.DynamicVariantList;
import org.orecruncher.lib.expression.Dynamic.DynamicBoolean;

public class BiomeTypeVariables extends DynamicVariantList {
   public BiomeTypeVariables() {
      for(Type t : BiomeUtil.getBiomeTypes()) {
         this.add(new DynamicBoolean("biome.is" + t.getName(), () -> {
            return EnvironStateHandler.EnvironState.getTruePlayerBiome().isBiomeType(t);
         }));
      }

   }
}
