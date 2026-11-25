package com.ferreusveritas.dynamictrees.worldgen;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.WorldGenRegistry;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors;
import com.ferreusveritas.dynamictrees.api.worldgen.IGroundFinder;
import com.ferreusveritas.dynamictrees.systems.poissondisc.PoissonDisc;
import com.ferreusveritas.dynamictrees.systems.poissondisc.PoissonDiscProviderUniversal;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.RandomXOR;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class TreeGenerator {
   protected static TreeGenerator INSTANCE;
   protected final BiomeDataBase defaultBiomeDataBase;
   public static final BiomeDataBase DIMENSIONBLACKLISTED = new BiomeDataBase();
   protected final PoissonDiscProviderUniversal circleProvider;
   protected final RandomXOR random = new RandomXOR();
   protected final Map<Integer, BiomeDataBase> dimensionMap = new HashMap();

   public static void preInit() {
      if (WorldGenRegistry.isWorldGenEnabled()) {
         new TreeGenerator();
      }

   }

   public TreeGenerator() {
      INSTANCE = this;
      this.defaultBiomeDataBase = new BiomeDataBase();
      this.circleProvider = new PoissonDiscProviderUniversal();
   }

   public static TreeGenerator getTreeGenerator() {
      return INSTANCE;
   }

   public BiomeDataBase getBiomeDataBase(int dimensionId) {
      return (BiomeDataBase)this.dimensionMap.getOrDefault(Integer.valueOf(dimensionId), this.getDefaultBiomeDataBase());
   }

   public BiomeDataBase getBiomeDataBase(World world) {
      return this.getBiomeDataBase(world.provider.getDimension());
   }

   public BiomeDataBase getDefaultBiomeDataBase() {
      return this.defaultBiomeDataBase;
   }

   public void linkDimensionToDataBase(int dimensionId, BiomeDataBase dBase) {
      this.dimensionMap.put(Integer.valueOf(dimensionId), dBase);
   }

   public void BlackListDimension(int dimensionId) {
      this.dimensionMap.put(Integer.valueOf(dimensionId), DIMENSIONBLACKLISTED);
   }

   public void clearAllBiomeDataBases() {
      this.dimensionMap.clear();
      this.defaultBiomeDataBase.clear();
   }

   public boolean validateBiomeDataBases() {
      return this.defaultBiomeDataBase.isValid() && this.dimensionMap.values().stream().allMatch((db) -> {
         return db.isValid();
      });
   }

   public PoissonDiscProviderUniversal getCircleProvider() {
      return this.circleProvider;
   }

   public void makeWoolCircle(World world, PoissonDisc circle, int h, TreeGenerator.EnumGeneratorResult resultType, SafeChunkBounds safeBounds) {
      this.makeWoolCircle(world, circle, h, resultType, safeBounds, 0);
   }

   public void makeWoolCircle(World world, PoissonDisc circle, int h, TreeGenerator.EnumGeneratorResult resultType, SafeChunkBounds safeBounds, int flags) {
      for(int ix = -circle.radius; ix <= circle.radius; ++ix) {
         for(int iz = -circle.radius; iz <= circle.radius; ++iz) {
            if (circle.isEdge(circle.x + ix, circle.z + iz)) {
               safeBounds.setBlockState(world, new BlockPos(circle.x + ix, h, circle.z + iz), Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata((circle.x ^ circle.z) & 15)), flags, true);
            }
         }
      }

      if (resultType != TreeGenerator.EnumGeneratorResult.GENERATED) {
         BlockPos pos = new BlockPos(circle.x, h, circle.z);
         EnumDyeColor color = resultType.getColor();
         safeBounds.setBlockState(world, pos, Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, color), true);
         safeBounds.setBlockState(world, pos.up(), Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, color), true);
      }

   }

   public TreeGenerator.EnumGeneratorResult makeTree(World world, BiomeDataBase biomeDataBase, PoissonDisc circle, IGroundFinder groundFinder, SafeChunkBounds safeBounds) {
      circle.add(8, 8);
      BlockPos pos = new BlockPos(circle.x, 0, circle.z);
      Biome biome = world.getBiome(pos);
      BiomeDataBase.BiomeEntry biomeEntry = biomeDataBase.getEntry(biome);
      pos = groundFinder.findGround(biomeEntry, world, pos);
      if (pos == BlockPos.ORIGIN) {
         return TreeGenerator.EnumGeneratorResult.NOGROUND;
      } else {
         this.random.setXOR(pos);
         IBlockState dirtState = world.getBlockState(pos);
         TreeGenerator.EnumGeneratorResult result = TreeGenerator.EnumGeneratorResult.GENERATED;
         BiomePropertySelectors.SpeciesSelection speciesSelection = biomeEntry.getSpeciesSelector().getSpecies(pos, dirtState, this.random);
         if (speciesSelection.isHandled()) {
            Species species = speciesSelection.getSpecies();
            if (species.isValid()) {
               if (species.isAcceptableSoilForWorldgen(world, pos, dirtState)) {
                  if (biomeEntry.getChanceSelector().getChance(this.random, species, circle.radius) == BiomePropertySelectors.EnumChance.OK) {
                     if (species.generate(world, pos, biome, this.random, circle.radius, safeBounds)) {
                        result = TreeGenerator.EnumGeneratorResult.GENERATED;
                     } else {
                        result = TreeGenerator.EnumGeneratorResult.FAILGENERATION;
                     }
                  } else {
                     result = TreeGenerator.EnumGeneratorResult.FAILCHANCE;
                  }
               } else {
                  result = TreeGenerator.EnumGeneratorResult.FAILSOIL;
               }
            } else {
               result = TreeGenerator.EnumGeneratorResult.NOTREE;
            }
         } else {
            result = TreeGenerator.EnumGeneratorResult.UNHANDLEDBIOME;
         }

         if (ModConfigs.worldGenDebug) {
            this.makeWoolCircle(world, circle, pos.getY(), result, safeBounds);
         }

         circle.add(-8, -8);
         return result;
      }
   }

   public static enum EnumGeneratorResult {
      GENERATED(EnumDyeColor.WHITE),
      NOTREE(EnumDyeColor.BLACK),
      UNHANDLEDBIOME(EnumDyeColor.YELLOW),
      FAILSOIL(EnumDyeColor.BROWN),
      FAILCHANCE(EnumDyeColor.BLUE),
      FAILGENERATION(EnumDyeColor.RED),
      NOGROUND(EnumDyeColor.PURPLE);

      private final EnumDyeColor color;

      private EnumGeneratorResult(EnumDyeColor color) {
         this.color = color;
      }

      public EnumDyeColor getColor() {
         return this.color;
      }
   }
}
