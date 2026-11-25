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
import rtg.api.world.deco.DecoBoulder;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaColdBeach extends RealisticBiomeBase {
   public static Biome biome = Biomes.COLD_BEACH;
   public static Biome river = Biomes.FROZEN_RIVER;

   public RealisticBiomeVanillaColdBeach() {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_FILLER_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaColdBeach.TerrainVanillaColdBeach();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaColdBeach.SurfaceVanillaBeach(this.getConfig(), biome.topBlock, biome.fillerBlock);
   }

   public void initDecos() {
      DecoBoulder decoBoulder = new DecoBoulder();
      decoBoulder.setBoulderBlock(Blocks.COBBLESTONE.getDefaultState());
      decoBoulder.setChance(16);
      decoBoulder.setMaxY(95);
      decoBoulder.setStrengthFactor(3.0F);
      this.addDeco(decoBoulder);
   }

   public static class SurfaceVanillaBeach extends SurfaceBase {
      private IBlockState mixBlockFill;

      public SurfaceVanillaBeach(BiomeConfig config, IBlockState top, IBlockState filler) {
         super(config, top, filler);
         this.mixBlockFill = this.getConfigBlock(config.SURFACE_MIX_FILLER_BLOCK.get(), Blocks.SANDSTONE.getDefaultState());
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();

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

   public static class TerrainVanillaColdBeach extends TerrainBase {
      public TerrainVanillaColdBeach() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainBeach(x, y, rtgWorld, river, 63.0F);
      }
   }
}
