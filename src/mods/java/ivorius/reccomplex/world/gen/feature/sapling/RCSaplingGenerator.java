package ivorius.reccomplex.world.gen.feature.sapling;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.random.WeightedSelector;
import ivorius.ivtoolkit.util.IvFunctions;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.SaplingGeneration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.apache.commons.lang3.tuple.Pair;

public class RCSaplingGenerator {
   public static final List<RCSaplingGenerator.Predictor> predictors = new ArrayList();

   public RCSaplingGenerator() {
   }

   public static boolean maybeGrowSapling(WorldServer world, BlockPos pos, Random random) {
      if (RCConfig.saplingTriggerChance > 0.0D && (RCConfig.saplingTriggerChance >= 1.0D || (double)random.nextFloat() >= RCConfig.saplingTriggerChance)) {
         Pair<Structure<?>, SaplingGeneration> pair = findRandomSapling(world, pos, random, true);
         if (pair == null) {
            return false;
         } else {
            growSapling(world, pos, random, (Structure)pair.getLeft(), (SaplingGeneration)pair.getRight());
            return true;
         }
      } else {
         return false;
      }
   }

   @Nullable
   public static Pair<Structure<?>, SaplingGeneration> findRandomSapling(WorldServer world, BlockPos pos, Random random, boolean considerVanilla) {
      Environment baseEnv = Environment.inNature(world, new StructureBoundingBox(pos, pos));
      List<Pair<Structure<?>, SaplingGeneration>> applicable = (List)StructureRegistry.INSTANCE.getGenerationTypes(SaplingGeneration.class).stream().filter((pair1) -> {
         return ((SaplingGeneration)pair1.getRight()).generatesIn(baseEnv.withGeneration((GenerationType)pair1.getRight()));
      }).collect(Collectors.toCollection(ArrayList::new));
      int vanillaComplexity = complexity(world, pos, random, predictors);
      ImmutableMultimap<Integer, Pair<Structure<?>, SaplingGeneration>> groups = IvFunctions.groupMap(applicable, (pairx) -> {
         return ((SaplingGeneration)pair.getRight()).pattern.pattern.compile(true).size();
      });
      List<Integer> complexities = Lists.newArrayList(groups.keySet());
      if (vanillaComplexity > 0) {
         complexities.add(Integer.valueOf(vanillaComplexity));
      }

      Collections.sort(complexities);
      Pair<Structure<?>, SaplingGeneration> pair = null;

      while(complexities.size() > 0 && pair == null) {
         Integer complexity = (Integer)complexities.remove(complexities.size() - 1);
         Set<Pair<Structure<?>, SaplingGeneration>> placeable = (Set)groups.get(complexity).stream().filter((p) -> {
            return ((SaplingGeneration)p.getRight()).pattern.canPlace(world, pos, ((Structure)p.getLeft()).size(), ((Structure)p.getLeft()).isRotatable(), ((Structure)p.getLeft()).isMirrorable());
         }).collect(Collectors.toSet());
         double totalWeight = placeable.stream().mapToDouble(RCSaplingGenerator::getSpawnWeight).sum();
         if (complexity.intValue() == vanillaComplexity && considerVanilla && random.nextDouble() * (totalWeight * RCConfig.baseSaplingSpawnWeight + 1.0D) < 1.0D) {
            break;
         }

         if (totalWeight > 0.0D) {
            pair = (Pair)WeightedSelector.select(random, placeable, RCSaplingGenerator::getSpawnWeight);
         }
      }

      return pair;
   }

   public static int complexity(WorldServer world, BlockPos pos, Random random, List<RCSaplingGenerator.Predictor> predictors) {
      return predictors.stream().mapToInt((a) -> {
         return a.complexity(world, pos, world.getBlockState(pos), random);
      }).filter((i) -> {
         return i >= 0;
      }).max().orElse(-1);
   }

   public static double getSpawnWeight(Pair<Structure<?>, SaplingGeneration> p) {
      return (double)RCConfig.tweakedSpawnRate(StructureRegistry.INSTANCE.id(p.getLeft())) * ((SaplingGeneration)p.getRight()).getActiveWeight();
   }

   public static void growSapling(WorldServer world, BlockPos pos, Random random, Structure<?> structure, SaplingGeneration saplingGenInfo) {
      int[] strucSize = structure.size();
      Multimap<AxisAlignedTransform2D, BlockPos> placeables = saplingGenInfo.pattern.testAll(world, pos, strucSize, structure.isRotatable(), structure.isMirrorable());
      AxisAlignedTransform2D transform = (AxisAlignedTransform2D)Lists.newArrayList(placeables.keys()).get(random.nextInt(placeables.keys().size()));
      Collection<BlockPos> transformedPositions = placeables.get(transform);
      BlockPos startPos = (BlockPos)Lists.newArrayList(transformedPositions).get(random.nextInt(transformedPositions.size()));
      Map<BlockPos, IBlockState> before = new HashMap();
      IBlockState air = Blocks.AIR.getDefaultState();
      saplingGenInfo.pattern.copy(transform, strucSize).forEach((i) -> {
         return (void)i.delete;
      }, (p, i) -> {
         BlockPos ePos = p.add(startPos);
         before.put(ePos, world.getBlockState(ePos));
         world.setBlockState(ePos, air, 4);
      });
      BlockPos spawnPos = transform.apply(saplingGenInfo.spawnShift, new int[]{1, 1, 1}).add(startPos);
      boolean success = (new StructureGenerator(structure)).world(world).generationInfo(saplingGenInfo).transform(transform).seed(Long.valueOf(random.nextLong())).maturity(StructureSpawnContext.GenerateMaturity.SUGGEST).memorize(RCConfig.memorizeSaplings).allowOverlaps(true).randomPosition(BlockSurfacePos.from(spawnPos), (context, blockCollection) -> {
         return (void)spawnPos.getY();
      }).generate().succeeded();
      if (!success) {
         before.forEach((pos1, state) -> {
            world.setBlockState(pos1, state, 4);
         });
      }

   }

   static {
      predictors.add(new VanillaSaplingPredictor());
   }

   interface Predictor {
      int complexity(World var1, BlockPos var2, IBlockState var3, Random var4);
   }
}
