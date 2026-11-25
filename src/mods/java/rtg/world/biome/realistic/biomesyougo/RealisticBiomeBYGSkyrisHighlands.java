package rtg.world.biome.realistic.biomesyougo;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.BumpyHillsEffect;
import rtg.api.world.terrain.heighteffect.JitterEffect;

public class RealisticBiomeBYGSkyrisHighlands extends RealisticBiomeBYGBase {
   public RealisticBiomeBYGSkyrisHighlands(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public void initDecos() {
      this.fallenTrees(new IBlockState[]{BlockUtil.getBlockStateFromCfgString("byg:skyrislog", BlockUtil.getStateLog(EnumType.ACACIA)), BlockUtil.getBlockStateFromCfgString("byg:skyrislog", BlockUtil.getStateLog(EnumType.ACACIA))}, new int[]{4, 4});
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGSkyrisHighlands.TerrainBOPHighland();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBYGSkyrisHighlands.SurfaceBOPHighland(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock);
   }

   public static class SurfaceBOPHighland extends SurfaceBase {
      public SurfaceBOPHighland(BiomeConfig config, IBlockState top, IBlockState filler) {
         super(config, top, filler);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();
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
                  primer.setBlockState(x, k, z, this.topBlock);
               } else if (depth < 4) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               }
            }
         }

      }
   }

   public static class TerrainBOPHighland extends TerrainBase {
      private float baseHeight = 90.0F;
      private BumpyHillsEffect onTop = new BumpyHillsEffect();
      private JitterEffect withJitter;

      public TerrainBOPHighland() {
         this.onTop.hillHeight = 30.0F;
         this.onTop.hillWavelength = 60.0F;
         this.onTop.spikeHeight = 20.0F;
         this.onTop.spikeWavelength = 10.0F;
         this.withJitter = new JitterEffect();
         this.withJitter.amplitude = 2.0F;
         this.withJitter.wavelength = 5.0F;
         this.withJitter.jittered = this.onTop;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(this.baseHeight + this.withJitter.added(rtgWorld, (float)x, (float)y) + groundNoise(x, y, 6.0F, rtgWorld), river);
      }
   }
}
