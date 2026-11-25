package ivorius.reccomplex.world.gen.feature.selector;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import ivorius.ivtoolkit.random.WeightedSelector;
import ivorius.ivtoolkit.random.WeightedSelector.SimpleItem;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.dimensions.DimensionDictionary;
import ivorius.reccomplex.utils.presets.PresettedList;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedBiomeMatcher;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedDimensionMatcher;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;

public class StructureSelector<T extends GenerationType & EnvironmentalSelection<C>, C> {
   protected final Set<String> cachedDimensionTypes = new HashSet();
   protected Multimap<C, SimpleItem<Pair<Structure<?>, T>>> weightedStructureInfos = ArrayListMultimap.create();
   protected TObjectDoubleMap<C> totalWeights = new TObjectDoubleHashMap();

   public StructureSelector(Map<String, Structure<?>> structures, WorldProvider provider, Biome biome, Class<T> typeClass) {
      this.cachedDimensionTypes.addAll(DimensionDictionary.getDimensionTypes(provider));

      for(Entry<String, Structure<?>> entry : structures.entrySet()) {
         float tweaked = RCConfig.tweakedSpawnRate((String)entry.getKey());

         for(T selection : ((Structure)entry.getValue()).generationTypes(typeClass)) {
            double generationWeight = ((EnvironmentalSelection)selection).getGenerationWeight(provider, biome) * (double)tweaked;
            if (generationWeight > 0.0D) {
               this.weightedStructureInfos.put(((EnvironmentalSelection)selection).generationCategory(), new SimpleItem(generationWeight, Pair.of(entry.getValue(), selection)));
               this.totalWeights.adjustOrPutValue(((EnvironmentalSelection)selection).generationCategory(), generationWeight, generationWeight);
            }
         }
      }

   }

   public static double generationWeight(WorldProvider provider, Biome biome, PresettedList<WeightedBiomeMatcher> biomeWeights, PresettedList<WeightedDimensionMatcher> dimensionWeights) {
      return generationWeightInBiome(biomeWeights, biome) * generationWeightInDimension(dimensionWeights, provider);
   }

   public static double generationWeightInDimension(PresettedList<WeightedDimensionMatcher> dimensionWeights, WorldProvider provider) {
      for(WeightedDimensionMatcher generationInfo : (ArrayList)dimensionWeights.getContents()) {
         if (generationInfo.matches(provider)) {
            return generationInfo.getActiveGenerationWeight();
         }
      }

      return 0.0D;
   }

   public static double generationWeightInBiome(PresettedList<WeightedBiomeMatcher> biomeWeights, Biome biome) {
      for(WeightedBiomeMatcher generationInfo : (ArrayList)biomeWeights.getContents()) {
         if (generationInfo.matches(biome)) {
            return generationInfo.getActiveGenerationWeight();
         }
      }

      return 0.0D;
   }

   public double totalWeight(@Nonnull C c) {
      return this.totalWeights.get(c);
   }

   @Nullable
   public Pair<Structure<?>, T> selectOne(EaglercraftRandom random, @Nonnull C c) {
      return this.selectOne(random, c, this.totalWeight(c));
   }

   @Nullable
   public Pair<Structure<?>, T> selectOne(EaglercraftRandom random, @Nonnull C c, double totalWeight) {
      return totalWeight > 0.0D ? (Pair)WeightedSelector.select(random, this.weightedStructureInfos.get(c)) : null;
   }

   public boolean isValid(Biome biome, WorldProvider provider) {
      return DimensionDictionary.getDimensionTypes(provider).equals(this.cachedDimensionTypes);
   }
}
