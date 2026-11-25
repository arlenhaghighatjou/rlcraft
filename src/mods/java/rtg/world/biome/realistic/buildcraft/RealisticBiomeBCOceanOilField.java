package rtg.world.biome.realistic.buildcraft;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.deco.collection.DecoCollectionOcean;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBCOceanOilField extends RealisticBiomeBCBase {
   public RealisticBiomeBCOceanOilField(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.0F);
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_SPONGE).set(false);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBCOceanOilField.TerrainVanillaOcean();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBCOceanOilField.SurfaceVanillaOcean(this.getConfig(), Blocks.SAND.getDefaultState(), Blocks.SAND.getDefaultState(), Blocks.GRAVEL.getDefaultState(), 20.0F, 0.2F);
   }

   public void initDecos() {
      this.addDecoCollection(new DecoCollectionOcean(this.getConfig()));
   }

   public static class SurfaceVanillaOcean extends SurfaceBase {
      private IBlockState mixBlock;
      private float width;
      private float height;
      private float mixCheck;

      public SurfaceVanillaOcean(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mix, float mixWidth, float mixHeight) {
         super(config, top, filler);
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mix);
         this.width = mixWidth;
         this.height = mixHeight;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (depth == 0 && k > 0 && k < 63) {
                  this.mixCheck = simplex.noise2f((float)i / this.width, (float)j / this.width);
                  if (this.mixCheck > this.height) {
                     primer.setBlockState(x, k, z, this.mixBlock);
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
                  }
               } else if (depth < 4 && k < 63) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               } else if (depth == 0 && k < 69) {
                  primer.setBlockState(x, k, z, Blocks.SAND.getDefaultState());
               }
            }
         }

      }
   }

   public static class TerrainVanillaOcean extends TerrainBase {
      public TerrainVanillaOcean() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainOcean(x, y, rtgWorld, river, 50.0F);
      }
   }
}
