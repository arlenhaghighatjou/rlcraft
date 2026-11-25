package ivorius.reccomplex.world.gen.feature;

import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.math.IvVecMathHelper;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.world.gen.feature.selector.MixingStructureSelector;
import ivorius.reccomplex.world.gen.feature.selector.NaturalStructureSelector;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.NaturalGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.StaticGeneration;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;

public class WorldGenStructures {
   public static final long POS_SEED = 1298319823120938102L;
   public static final int STRUCTURE_TRIES = 10;

   public WorldGenStructures() {
   }

   public static void planStaticStructuresInChunk(EaglercraftRandom random, ChunkPos chunkPos, WorldServer world, BlockPos spawnPos, @Nullable Predicate<Structure> structurePredicate) {
      StaticGeneration.structuresAt(StructureRegistry.INSTANCE, chunkPos, world, spawnPos).forEach((triple) -> {
         StaticGeneration staticGenInfo = (StaticGeneration)triple.getMiddle();
         Structure<?> structure = (Structure)triple.getLeft();
         BlockSurfacePos pos = (BlockSurfacePos)triple.getRight();
         if (structurePredicate == null || structurePredicate.test(structure)) {
            (new StructureGenerator(structure)).world(world).generationInfo(staticGenInfo).seed(Long.valueOf(random.nextLong())).randomPosition(pos, staticGenInfo.placer).fromCenter(true).partially(true, chunkPos).generate();
         }
      });
   }

   protected static float distance(ChunkPos left, ChunkPos right) {
      return MathHelper.sqrt((float)((left.x - right.x) * (left.x - right.x) + (left.z - right.z) * (left.z - right.z)));
   }

   public static void planStructuresInChunk(EaglercraftRandom random, ChunkPos chunkPos, WorldServer world, Biome biomeGen, @Nullable Predicate<Structure> structurePredicate) {
      MixingStructureSelector<NaturalGeneration, NaturalStructureSelector.Category> structureSelector = (MixingStructureSelector)NaturalGeneration.selectors(StructureRegistry.INSTANCE).get(biomeGen, world.provider);
      float distanceToSpawn = distance(new ChunkPos(world.getSpawnPoint()), chunkPos);
      List<Pair<Structure<?>, NaturalGeneration>> generated = structureSelector.generatedStructures(random, world.getBiome(chunkPos.getBlock(0, 0, 0)), world.provider, Float.valueOf(distanceToSpawn));
      generated.stream().filter((pair) -> {
         return (void)(structurePredicate == null || structurePredicate.test(pair.getLeft()));
      }).forEach((pair) -> {
         planStructureInChunk(chunkPos, world, (Structure)pair.getLeft(), (NaturalGeneration)pair.getRight(), random.nextLong());
      });
   }

   public static boolean generateOneStructureInChunk(EaglercraftRandom random, ChunkPos chunkPos, WorldServer world, Biome biomeGen) {
      MixingStructureSelector<NaturalGeneration, NaturalStructureSelector.Category> structureSelector = (MixingStructureSelector)NaturalGeneration.selectors(StructureRegistry.INSTANCE).get(biomeGen, world.provider);
      float distanceToSpawn = distance(new ChunkPos(world.getSpawnPoint()), chunkPos);

      for(int i = 0; i < 10; ++i) {
         Pair<Structure<?>, NaturalGeneration> pair = structureSelector.selectOne(random, world.provider, world.getBiome(chunkPos.getBlock(0, 0, 0)), (MixingStructureSelector.Category)null, Float.valueOf(distanceToSpawn));
         if (pair != null && planStructureInChunk(chunkPos, world, (Structure)pair.getLeft(), (NaturalGeneration)pair.getRight(), random.nextLong())) {
            return true;
         }
      }

      return false;
   }

   protected static boolean planStructureInChunk(ChunkPos chunkPos, WorldServer world, Structure<?> structure, NaturalGeneration naturalGenInfo, long seed) {
      String structureName = StructureRegistry.INSTANCE.id(structure);
      BlockSurfacePos genPos = randomSurfacePos(chunkPos, seed);
      if (naturalGenInfo.hasLimitations() && !naturalGenInfo.getLimitations().areResolved(world, structureName)) {
         return false;
      } else {
         StructureGenerator<?> generator = (new StructureGenerator(structure)).world(world).generationInfo(naturalGenInfo).seed(Long.valueOf(seed)).maturity(StructureSpawnContext.GenerateMaturity.SUGGEST).randomPosition(genPos, naturalGenInfo.placer).fromCenter(true).partially(true, chunkPos);
         if (naturalGenInfo.getGenerationWeight(world.provider, generator.environment().biome) <= 0.0D) {
            RecurrentComplex.logger.trace(String.format("%s failed to spawn at %s (incompatible biome edge)", structure, genPos));
            return false;
         } else {
            return generator.generate().succeeded();
         }
      }
   }

   public static void complementStructuresInChunk(ChunkPos chunkPos, WorldServer world, List<WorldStructureGenerationData.StructureEntry> complement) {
      WorldStructureGenerationData data = WorldStructureGenerationData.get(world);
      complement.stream().filter((e) -> {
         return (void)(!e.preventComplementation());
      }).forEach((entry) -> {
         Structure<?> structure = (Structure)StructureRegistry.INSTANCE.get(entry.getStructureID());
         if (structure == null) {
            RecurrentComplex.logger.warn(String.format("Can't find structure %s (%s) to complement in %s (%d)", entry.getStructureID(), entry.getUuid(), chunkPos, world.provider.getDimension()));
         } else if (entry.instanceData == null && !entry.firstTime) {
            RecurrentComplex.logger.warn(String.format("Can't find instance data of %s (%s) to complement in %s (%d)", entry.getStructureID(), entry.getUuid(), chunkPos, world.provider.getDimension()));
         } else {
            (new StructureGenerator(structure)).world(world).generationInfo(entry.generationInfoID).seed(Long.valueOf(chunkSeed(entry.seed, chunkPos))).boundingBox(entry.boundingBox).transform(entry.transform).generationBB(Structures.chunkBoundingBox(chunkPos, true)).structureID(entry.getStructureID()).instanceData(entry.instanceData).maturity(StructureSpawnContext.GenerateMaturity.COMPLEMENT).generate();
            if (entry.firstTime) {
               entry.firstTime = false;
               data.markDirty();
            }

         }
      });
   }

   public static long chunkSeed(long seed, ChunkPos chunkPos) {
      return (long)chunkPos.x * 341873128712L + (long)chunkPos.z * 132897987541L + seed;
   }

   public static BlockSurfacePos randomSurfacePos(ChunkPos chunkPos, long seed) {
      EaglercraftRandom posRandom = new EaglercraftRandom(seed ^ 1298319823120938102L);
      return BlockSurfacePos.from(chunkPos.getBlock(posRandom.nextInt(16) + 8, 0, posRandom.nextInt(16) + 8));
   }

   public static boolean decorate(WorldServer world, EaglercraftRandom random, ChunkPos chunkPos, @Nullable Predicate<Structure> structurePredicate) {
      boolean generated = false;
      boolean worldWantsStructures = world.getWorldInfo().isMapFeaturesEnabled();
      WorldStructureGenerationData data = WorldStructureGenerationData.get(world);
      synchronized(data) {
         List<WorldStructureGenerationData.StructureEntry> complement = (List)data.structureEntriesIn(chunkPos).collect(Collectors.toList());
         if (structurePredicate == null) {
            data.checkChunk(chunkPos);
         }

         if (structurePredicate == null) {
            complementStructuresInChunk(chunkPos, world, complement);
         }

         if ((!RCConfig.honorStructureGenerationOption || worldWantsStructures) && (structurePredicate == null || data.checkChunkFinal(chunkPos))) {
            Biome biomeGen = world.getBiome(chunkPos.getBlock(8, 0, 8));
            BlockPos spawnPos = world.getSpawnPoint();
            planStaticStructuresInChunk(random, chunkPos, world, spawnPos, structurePredicate);
            boolean mayGenerate = RCConfig.isGenerationEnabled(biomeGen) && RCConfig.isGenerationEnabled(world.provider);
            if (world.provider.getDimension() == 0) {
               double distToSpawn = IvVecMathHelper.distanceSQ(new double[]{(double)(chunkPos.x * 16 + 8), (double)(chunkPos.z * 16 + 8)}, new double[]{(double)spawnPos.getX(), (double)spawnPos.getZ()});
               mayGenerate &= distToSpawn >= (double)(RCConfig.minDistToSpawnForGeneration * RCConfig.minDistToSpawnForGeneration);
            }

            if (mayGenerate) {
               planStructuresInChunk(random, chunkPos, world, biomeGen, structurePredicate);
            }

            generated = true;
         }

         return generated;
      }
   }
}
