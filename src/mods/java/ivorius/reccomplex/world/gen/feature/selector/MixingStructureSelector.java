package ivorius.reccomplex.world.gen.feature.selector;

import ivorius.ivtoolkit.random.WeightedSelector;
import ivorius.ivtoolkit.random.WeightedSelector.SimpleItem;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;

public class MixingStructureSelector<T extends GenerationType & EnvironmentalSelection<C>, C extends MixingStructureSelector.Category> extends StructureSelector<T, C> {
   public MixingStructureSelector(Map<String, Structure<?>> structures, WorldProvider provider, Biome biome, Class<T> typeClass) {
      super(structures, provider, biome, typeClass);
   }

   public int structuresInBiome(C category, WorldProvider worldProvider, Biome biome, Float distanceToSpawn, EaglercraftRandom random) {
      return category != null ? category.structuresInBiome(biome, worldProvider, this.totalWeight(category), distanceToSpawn, random) : 0;
   }

   public List<Pair<Structure<?>, T>> generatedStructures(EaglercraftRandom random, Biome biome, WorldProvider provider, Float distanceToSpawn) {
      return (List)this.weightedStructureInfos.keySet().stream().flatMap((category) -> {
         return IntStream.range(0, this.structuresInBiome(category, provider, biome, distanceToSpawn, random)).mapToObj((i) -> {
            return category;
         });
      }).map((category) -> {
         return (Pair)WeightedSelector.select(random, this.weightedStructureInfos.get(category));
      }).collect(Collectors.toList());
   }

   @Nullable
   public Pair<Structure<?>, T> selectOne(EaglercraftRandom random, WorldProvider provider, Biome biome, @Nullable C c, Float distanceToSpawn) {
      if (c != null) {
         return super.selectOne(random, c);
      } else {
         List<SimpleItem<C>> list = (List)this.weightedStructureInfos.keySet().stream().map((category) -> {
            return new SimpleItem((double)this.structuresInBiome(category, provider, biome, distanceToSpawn, random), category);
         }).collect(Collectors.toList());
         return this.selectOne(random, WeightedSelector.select(random, list));
      }
   }

   interface Category {
      int structuresInBiome(Biome var1, WorldProvider var2, double var3, Float var5, EaglercraftRandom var6);
   }
}
