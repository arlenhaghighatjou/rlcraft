package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import biomesoplenty.api.block.BOPBlocks;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.HeightEffect;
import rtg.api.world.terrain.heighteffect.JitterEffect;
import rtg.api.world.terrain.heighteffect.MountainsWithPassesEffect;
import rtg.world.biome.realistic.vanilla.RealisticBiomeVanillaExtremeHills;

public class RealisticBiomeBOPTropicalIsland extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.tropical_island.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPTropicalIsland() {
      super(biome);
   }

   public Biome preferredBeach() {
      return (Biome)BOPBiomes.white_beach.orNull();
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaExtremeHills.RidgedExtremeHills(84.0F, 65.0F, 200.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPTropicalIsland.SurfaceVanillaExtremeHillsPlus(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, 0.4F, 1.6F, 60.0F, 65.0F, 1.5F, this.baseBiome().topBlock, 0.08F);
   }

   public void initDecos() {
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.getDistribution().setNoiseDivisor(80.0F);
      decoFallenTree.getDistribution().setNoiseFactor(60.0F);
      decoFallenTree.getDistribution().setNoiseAddend(-15.0F);
      decoFallenTree.setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE);
      decoFallenTree.setLogConditionChance(12);
      decoFallenTree.setLogBlock(BOPBlocks.log_1.getStateFromMeta(7));
      decoFallenTree.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      decoFallenTree.setMinSize(3);
      decoFallenTree.setMaxSize(4);
      this.addDeco(decoFallenTree, this.getConfig().ALLOW_LOGS.get());
   }

   public static class SurfaceVanillaExtremeHillsPlus extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;
      private float mixHeight;

      public SurfaceVanillaExtremeHillsPlus(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixSize) {
         super(config, top, fill);
         this.min = minCliff;
         this.sCliff = stoneCliff;
         this.sHeight = stoneHeight;
         this.sStrength = stoneStrength;
         this.cCliff = clayCliff;
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mix);
         this.mixHeight = mixSize;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         float c = TerrainBase.calcCliff(x, z, noise);
         int cliff = 0;
         boolean m = false;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (depth == 0) {
                  float p = simplex.noise3f((float)i / 8.0F, (float)j / 8.0F, (float)k / 8.0F) * 0.5F;
                  if (c > this.min && c > this.sCliff - ((float)k - this.sHeight) / this.sStrength + p) {
                     cliff = 1;
                  }

                  if (c > this.cCliff) {
                     cliff = 2;
                  }

                  if (cliff == 1) {
                     if (rand.nextInt(3) == 0) {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     } else {
                        primer.setBlockState(x, k, z, this.hcStone());
                     }
                  } else if (cliff == 2) {
                     primer.setBlockState(x, k, z, this.getShadowStoneBlock());
                  } else if (k < 63) {
                     if (k < 62) {
                        primer.setBlockState(x, k, z, this.fillerBlock);
                     } else {
                        primer.setBlockState(x, k, z, this.topBlock);
                     }
                  } else if (simplex.noise2f((float)i / 12.0F, (float)j / 12.0F) > this.mixHeight) {
                     primer.setBlockState(x, k, z, this.mixBlock);
                     m = true;
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
                  }
               } else if (depth < 6) {
                  if (cliff == 1) {
                     primer.setBlockState(x, k, z, this.hcStone());
                  } else if (cliff == 2) {
                     primer.setBlockState(x, k, z, this.getShadowStoneBlock());
                  } else {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               }
            }
         }

      }
   }

   public static class TerrainVanillaExtremeHillsPlus extends TerrainBase {
      private float width;
      private float strength;
      private float spikeWidth = 40.0F;
      private float spikeHeight = 70.0F;
      private HeightEffect heightEffect;

      public TerrainVanillaExtremeHillsPlus(float mountainWidth, float mountainStrength, float height) {
         this.width = mountainWidth;
         this.strength = mountainStrength;
         this.base = height;
         MountainsWithPassesEffect mountainEffect = new MountainsWithPassesEffect();
         mountainEffect.mountainHeight = this.strength;
         mountainEffect.mountainWavelength = this.width;
         mountainEffect.spikeHeight = this.spikeHeight;
         mountainEffect.spikeWavelength = this.spikeWidth;
         this.heightEffect = new JitterEffect(7.0F, 10.0F, mountainEffect);
         this.heightEffect = new JitterEffect(3.0F, 6.0F, this.heightEffect);
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(this.heightEffect.added(rtgWorld, (float)x, (float)y) + this.base, river);
      }
   }
}
