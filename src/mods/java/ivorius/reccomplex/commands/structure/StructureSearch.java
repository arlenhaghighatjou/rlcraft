package ivorius.reccomplex.commands.structure;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.world.gen.feature.selector.StructureSelector;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.Metadata;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.ListGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.MazeGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.NaturalGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaDecorationGeneration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;

public class StructureSearch {
   public StructureSearch() {
   }

   @Nonnull
   public static Collection<String> keywords(String id, Structure<?> structure) {
      List<String> keywords = new ArrayList();
      keywords.add(id);
      structure.generationTypes(GenerationType.class).forEach((info) -> {
         keywords(keywords, info);
      });
      if (structure instanceof GenericStructure) {
         keywords(keywords, ((GenericStructure)structure).metadata);
      }

      return keywords;
   }

   protected static void keywords(Collection<String> keywords, GenerationType info) {
      keywords.add(info.id());
      keywords.add(info.displayString());
      keywords.add(StructureRegistry.GENERATION_TYPES.iDForType(info.getClass()));
   }

   @Nonnull
   public static void keywords(Collection<String> collection, Metadata metadata) {
      collection.add(metadata.authors);
      collection.add(metadata.comment);
      collection.add(metadata.weblink);
   }

   public static double searchRank(List<String> query, Collection<String> keywords) {
      Stream var10000 = keywords.stream();
      Predicate var10001 = Predicates.contains(Pattern.compile(String.join("|", Lists.transform(query, Pattern::quote)), 2));
      var10001.getClass();
      return (double)var10000.filter(var10001::apply).count();
   }

   public static double biome(Structure<?> structure, Biome biome) {
      double result = 0.0D;
      result = result + structure.generationTypes(NaturalGeneration.class).stream().mapToDouble((g) -> {
         return StructureSelector.generationWeightInBiome(g.biomeWeights, biome);
      }).sum();
      result = result + structure.generationTypes(VanillaDecorationGeneration.class).stream().mapToDouble((g) -> {
         return StructureSelector.generationWeightInBiome(g.biomeWeights, biome);
      }).sum();
      return result;
   }

   public static double dimension(Structure<?> structure, WorldServer world) {
      double result = 0.0D;
      result = result + structure.generationTypes(NaturalGeneration.class).stream().mapToDouble((g) -> {
         return StructureSelector.generationWeightInDimension(g.dimensionWeights, world.provider);
      }).sum();
      result = result + structure.generationTypes(VanillaDecorationGeneration.class).stream().mapToDouble((g) -> {
         return StructureSelector.generationWeightInDimension(g.dimensionWeights, world.provider);
      }).sum();
      return result;
   }

   public static double list(Structure<?> structure, String listID) {
      return structure.generationTypes(ListGeneration.class).stream().mapToDouble((g) -> {
         return g.listID.equals(listID) ? 1.0D : 0.0D;
      }).sum();
   }

   public static double maze(Structure<?> structure, String mazeID) {
      return structure.generationTypes(MazeGeneration.class).stream().mapToDouble((g) -> {
         return g.mazeID.equals(mazeID) ? 1.0D : 0.0D;
      }).sum();
   }

   public static double author(Structure<?> structure, String author) {
      return structure instanceof GenericStructure ? searchRank(Collections.singletonList(author), Collections.singleton(((GenericStructure)structure).metadata.authors)) : 0.0D;
   }

   public static long containedBlocks(Structure structure, BlockExpression matcher) {
      if (structure == null) {
         return 0L;
      } else {
         IvBlockCollection collection = structure.blockCollection();
         if (collection == null) {
            return 0L;
         } else {
            return collection.area().stream().anyMatch((p) -> {
               return ((Boolean)matcher.evaluate(collection.getBlockState(p))).booleanValue();
            }) ? 1L : 0L;
         }
      }
   }
}
