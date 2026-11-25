package rtg.world.biome.realistic.vanilla;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.collection.DecoCollectionIceTrees;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaIcePlainsSpikes extends RealisticBiomeBase {
   public static Biome biome = Biomes.MUTATED_ICE_FLATS;
   public static Biome river = Biomes.FROZEN_RIVER;

   public RealisticBiomeVanillaIcePlainsSpikes() {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_ICE_TREES).set(true);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaIcePlainsSpikes.TerrainVanillaIcePlainsSpikes();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaIcePlainsSpikes.SurfaceVanillaIcePlainsSpikes(this.getConfig(), biome.topBlock, biome.fillerBlock, biome.topBlock, biome.topBlock);
   }

   public void initDecos() {
      if (this.getConfig().ALLOW_ICE_TREES.get()) {
         this.addDecoCollection(new DecoCollectionIceTrees(this.getConfig()));
      }

   }

   public static class SurfaceVanillaIcePlainsSpikes extends SurfaceBase {
      private IBlockState cliffBlock1;
      private IBlockState cliffBlock2;

      public SurfaceVanillaIcePlainsSpikes(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState cliff1, IBlockState cliff2) {
         super(config, top, filler);
         this.cliffBlock1 = cliff1;
         this.cliffBlock2 = cliff2;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.4F;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (cliff) {
                  if (depth > -1 && depth < 2) {
                     primer.setBlockState(x, k, z, rand.nextInt(3) == 0 ? this.cliffBlock2 : this.cliffBlock1);
                  } else if (depth < 10) {
                     primer.setBlockState(x, k, z, this.cliffBlock1);
                  }
               } else if (depth == 0 && k > 61) {
                  primer.setBlockState(x, k, z, this.topBlock);
               } else if (depth < 4) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               }
            }
         }

      }
   }

   public static class TerrainVanillaIcePlainsSpikes extends TerrainBase {
      public TerrainVanillaIcePlainsSpikes() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 160.0F, 10.0F, 60.0F, 200.0F, 65.0F);
      }
   }
}
