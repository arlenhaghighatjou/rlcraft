package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.world.RTGWorld;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBOPGravelBeach extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.gravel_beach.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPGravelBeach() {
      super(biome);
   }

   public Biome preferredBeach() {
      return biome;
   }

   public void initConfig() {
      this.getConfig().SURFACE_BLEED_IN.set(true);
      this.getConfig().SURFACE_BLEED_OUT.set(true);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_FILLER_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPGravelBeach.TerrainBOPGravelBeach();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPGravelBeach.SurfaceBOPGravelBeach(this.getConfig(), biome.topBlock, biome.fillerBlock);
   }

   public static class SurfaceBOPGravelBeach extends SurfaceBase {
      private IBlockState mixBlockFill;

      public SurfaceBOPGravelBeach(BiomeConfig config, IBlockState top, IBlockState filler) {
         super(config, top, filler);
         this.mixBlockFill = this.getConfigBlock(config.SURFACE_MIX_FILLER_BLOCK.get(), Blocks.GRAVEL.getDefaultState());
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (depth == 0 && k > 61) {
                  primer.setBlockState(x, k, z, this.topBlock);
               } else if (k > 63 && depth > 3 && depth < 6) {
                  primer.setBlockState(x, k, z, this.mixBlockFill);
               } else if (depth < 4) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               }
            }
         }

      }
   }

   public static class TerrainBOPGravelBeach extends TerrainBase {
      public TerrainBOPGravelBeach() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainBeach(x, y, rtgWorld, river, 63.0F);
      }
   }
}
