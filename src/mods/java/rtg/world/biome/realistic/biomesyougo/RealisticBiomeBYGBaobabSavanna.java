package rtg.world.biome.realistic.biomesyougo;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.GroundEffect;

public class RealisticBiomeBYGBaobabSavanna extends RealisticBiomeBYGBase {
   public RealisticBiomeBYGBaobabSavanna(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public void initDecos() {
      this.fallenTrees(new IBlockState[]{BlockUtil.getStateLog(EnumType.ACACIA), BlockUtil.getBlockStateFromCfgString("byg:baobablog", BlockUtil.getStateLog(EnumType.ACACIA))}, new int[]{4, 6});
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGBaobabSavanna.TerrainBYGBiome();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBYGBaobabSavanna.SurfaceBYGBiome(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, this.baseBiome().topBlock, 13.0F, 0.27F);
   }

   public static class SurfaceBYGBiome extends SurfaceBase {
      private IBlockState mixBlock;
      private float width;
      private float height;

      public SurfaceBYGBiome(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mix, float mixWidth, float mixHeight) {
         super(config, top, filler);
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mix);
         this.width = mixWidth;
         this.height = mixHeight;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
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
                     if (rand.nextInt(3) == 0) {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     } else {
                        primer.setBlockState(x, k, z, this.hcStone());
                     }
                  } else if (depth < 10) {
                     primer.setBlockState(x, k, z, this.hcStone());
                  }
               } else if (depth == 0 && k > 61) {
                  if (simplex.noise2f((float)i / this.width, (float)j / this.width) > this.height) {
                     primer.setBlockState(x, k, z, this.mixBlock);
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
                  }
               } else if (depth < 4) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               }
            }
         }

      }
   }

   public static class TerrainBYGBiome extends TerrainBase {
      private GroundEffect groundEffect = new GroundEffect(4.0F);

      public TerrainBYGBiome() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(65.0F + this.groundEffect.added(rtgWorld, (float)x, (float)y), river);
      }
   }
}
