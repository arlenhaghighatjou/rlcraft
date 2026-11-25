package ivorius.reccomplex.world.gen.feature.decoration;

import com.google.gson.annotations.SerializedName;
import ivorius.ivtoolkit.tools.IvGsonHelper;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.WorldGenStructures;
import ivorius.reccomplex.world.gen.feature.selector.StructureSelector;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaDecorationGeneration;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import org.apache.commons.lang3.tuple.Pair;

public class RCBiomeDecorator {
   public static RCBiomeDecorator.Adapter vanillaAdapter = new VanillaDecorationAdapter();
   public static final List<RCBiomeDecorator.Adapter> adapters = new ArrayList();
   public static final int STRUCTURE_TRIES = 5;

   public RCBiomeDecorator() {
   }

   @ParametersAreNonnullByDefault
   public static Result decorate(WorldServer worldIn, EaglercraftRandom random, BlockPos chunkPos, RCBiomeDecorator.DecorationType type) {
      try {
         return doDecorate(worldIn, random, chunkPos, type);
      } catch (Exception var5) {
         RecurrentComplex.logger.error("Exception when decorating", var5);
         return null;
      }
   }

   protected static Result doDecorate(WorldServer worldIn, EaglercraftRandom random, BlockPos chunkPos, RCBiomeDecorator.DecorationType type) {
      Biome biomeIn = worldIn.getBiome(chunkPos.add(16, 0, 16));
      BiomeDecorator decorator = biomeIn.decorator;
      RCBiomeDecorator.Adapter adapter = adapter(worldIn, chunkPos, type, biomeIn, decorator);
      if (biomeIn != Biomes.ROOFED_FOREST && biomeIn != Biomes.MUTATED_ROOFED_FOREST) {
         int origAmount = adapter.amount(worldIn, random, biomeIn, decorator, chunkPos, type);
         if (origAmount < 0) {
            return null;
         } else {
            int vanillaAmount = doDecorate(worldIn, random, chunkPos, type, origAmount, adapter.mayGiveUp(worldIn, random, biomeIn, decorator, chunkPos, type));
            if (vanillaAmount == origAmount) {
               return null;
            } else {
               for(int i = 0; i < vanillaAmount; ++i) {
                  adapter.generate(worldIn, random, biomeIn, decorator, chunkPos, type);
               }

               return vanillaAmount >= 0 ? Result.DENY : null;
            }
         }
      } else {
         return Result.ALLOW;
      }
   }

   @ParametersAreNonnullByDefault
   public static int decorate(WorldServer worldIn, EaglercraftRandom random, BlockPos chunkPos, RCBiomeDecorator.DecorationType type, int amount) {
      try {
         return doDecorate(worldIn, random, chunkPos, type, amount, false);
      } catch (Exception var6) {
         RecurrentComplex.logger.error("Exception when decorating", var6);
         return amount;
      }
   }

   protected static int doDecorate(WorldServer worldIn, EaglercraftRandom random, BlockPos blockPos, RCBiomeDecorator.DecorationType type, int amount, boolean lowChance) {
      ChunkPos chunkPos = new ChunkPos(blockPos);
      double baseWeight = RCConfig.baseDecorationWeights.get(type) * (double)(RCConfig.isGenerationEnabled(worldIn.provider) ? 1 : 0);
      if (baseWeight <= 0.0D) {
         return amount;
      } else {
         Biome biomeIn = worldIn.getBiome(chunkPos.getBlock(16, 0, 16));
         StructureSelector<VanillaDecorationGeneration, RCBiomeDecorator.DecorationType> selector = VanillaDecorationGeneration.selectors(StructureRegistry.INSTANCE).get(biomeIn, worldIn.provider);
         double totalWeight = selector.totalWeight(type);
         return totalWeight <= 0.0D ? amount : tryGenerate(worldIn, random, chunkPos, selector, type, totalWeight, baseWeight, amount, lowChance);
      }
   }

   public static Pair<Structure<?>, VanillaDecorationGeneration> selectDecoration(WorldServer worldIn, EaglercraftRandom random, BlockPos blockPos, RCBiomeDecorator.DecorationType type) {
      ChunkPos chunkPos = new ChunkPos(blockPos);
      double baseWeight = RCConfig.baseDecorationWeights.get(type) * (double)(RCConfig.isGenerationEnabled(worldIn.provider) ? 1 : 0);
      if (baseWeight <= 0.0D) {
         return null;
      } else {
         Biome biomeIn = worldIn.getBiome(chunkPos.getBlock(16, 0, 16));
         StructureSelector<VanillaDecorationGeneration, RCBiomeDecorator.DecorationType> selector = VanillaDecorationGeneration.selectors(StructureRegistry.INSTANCE).get(biomeIn, worldIn.provider);
         double totalWeight = selector.totalWeight(type);
         if (totalWeight <= 0.0D) {
            return null;
         } else {
            return (double)random.nextFloat() * (totalWeight * baseWeight + 1.0D) < 1.0D ? null : selector.selectOne(random, type, totalWeight);
         }
      }
   }

   public static RCBiomeDecorator.Adapter adapter(WorldServer worldIn, BlockPos chunkPos, RCBiomeDecorator.DecorationType type, Biome biomeIn, BiomeDecorator decorator) {
      return (RCBiomeDecorator.Adapter)adapters.stream().filter((a) -> {
         return a.matches(worldIn, biomeIn, decorator, chunkPos, type);
      }).findFirst().orElse(vanillaAdapter);
   }

   public static int tryGenerate(WorldServer worldIn, EaglercraftRandom random, ChunkPos chunkPos, StructureSelector<VanillaDecorationGeneration, RCBiomeDecorator.DecorationType> selector, RCBiomeDecorator.DecorationType type, double totalWeight, double baseWeight, int vanillaAmount, boolean mayGiveUp) {
      int rcAmount = amount(random, totalWeight * baseWeight, vanillaAmount);
      if (rcAmount <= 0 && mayGiveUp) {
         return -1;
      } else {
         for(int i = 0; i < rcAmount; ++i) {
            for(int t = 0; t < 5; ++t) {
               Pair<Structure<?>, VanillaDecorationGeneration> structurePair = selector.selectOne(random, type, totalWeight);
               if (generate(structurePair, worldIn, chunkPos, random)) {
                  break;
               }
            }
         }

         return vanillaAmount - rcAmount;
      }
   }

   public static boolean generate(Pair<Structure<?>, VanillaDecorationGeneration> generation, WorldServer worldIn, ChunkPos chunkPos, EaglercraftRandom random) {
      long seed = random.nextLong();
      return (new StructureGenerator((Structure)generation.getLeft())).generationInfo((GenerationType)generation.getRight()).world(worldIn).seed(Long.valueOf(seed)).maturity(StructureSpawnContext.GenerateMaturity.SUGGEST).partially(true, chunkPos).allowOverlaps(true).randomPosition(WorldGenStructures.randomSurfacePos(chunkPos, seed), ((VanillaDecorationGeneration)generation.getRight()).placer()).fromCenter(true).generate().succeeded();
   }

   protected static Placer shift(Placer placer, int y) {
      return (context, blockCollection) -> {
         return placer.place(context, blockCollection) + y;
      };
   }

   protected static int amount(EaglercraftRandom random, double totalWeight, int picks) {
      int amount = 0;

      for(int i = 0; i < picks; ++i) {
         if (random.nextDouble() * (totalWeight + 1.0D) > 1.0D) {
            ++amount;
         }
      }

      return amount;
   }

   interface Adapter {
      boolean matches(WorldServer var1, Biome var2, BiomeDecorator var3, BlockPos var4, RCBiomeDecorator.DecorationType var5);

      int amount(WorldServer var1, EaglercraftRandom var2, Biome var3, BiomeDecorator var4, BlockPos var5, RCBiomeDecorator.DecorationType var6);

      boolean mayGiveUp(WorldServer var1, EaglercraftRandom var2, Biome var3, BiomeDecorator var4, BlockPos var5, RCBiomeDecorator.DecorationType var6);

      void generate(WorldServer var1, EaglercraftRandom var2, Biome var3, BiomeDecorator var4, BlockPos var5, RCBiomeDecorator.DecorationType var6);
   }

   public static enum DecorationType {
      @SerializedName("big_mushroom")
      BIG_SHROOM,
      @SerializedName("cactus")
      CACTUS,
      @SerializedName("tree")
      TREE,
      @SerializedName("desert_well")
      DESERT_WELL,
      @SerializedName("fossil")
      FOSSIL,
      @SerializedName("ocean_monument")
      OCEAN_MONUMENT,
      @SerializedName("scattered_feature")
      SCATTERED_FEATURE,
      @SerializedName("village")
      VILLAGE,
      @SerializedName("nether_bridge")
      NETHER_BRIDGE,
      @SerializedName("stronghold")
      STRONGHOLD,
      @SerializedName("mineshaft")
      MINESHAFT;

      private DecorationType() {
      }

      public static RCBiomeDecorator.DecorationType byID(String id) {
         return (RCBiomeDecorator.DecorationType)IvGsonHelper.enumForName(id, values());
      }

      @Nullable
      public static RCBiomeDecorator.DecorationType getDecorationType(Decorate event) {
         switch(event.getType()) {
         case BIG_SHROOM:
            return BIG_SHROOM;
         case TREE:
            return TREE;
         case CACTUS:
            return CACTUS;
         case FOSSIL:
            return FOSSIL;
         case DESERT_WELL:
            return DESERT_WELL;
         default:
            return null;
         }
      }

      public String id() {
         return IvGsonHelper.serializedName(this);
      }
   }
}
