package ivorius.reccomplex.world.gen.feature.selector;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CachedStructureSelectors<S extends StructureSelector> {
   private Map<Pair<Integer, ResourceLocation>, S> structureSelectors = new HashMap();
   private BiFunction<Biome, WorldProvider, S> selectorSupplier;

   public CachedStructureSelectors(BiFunction<Biome, WorldProvider, S> selectorSupplier) {
      this.selectorSupplier = selectorSupplier;
   }

   public S get(Biome biome, WorldProvider provider) {
      Pair<Integer, ResourceLocation> pair = new ImmutablePair(provider.getDimension(), Biome.REGISTRY.getNameForObject(biome));
      S structureSelector = this.structureSelectors.get(pair);
      if (structureSelector == null || !structureSelector.isValid(biome, provider)) {
         structureSelector = this.selectorSupplier.apply(biome, provider);
         this.structureSelectors.put(pair, structureSelector);
      }

      return structureSelector;
   }

   public void clear() {
      this.structureSelectors.clear();
   }
}
