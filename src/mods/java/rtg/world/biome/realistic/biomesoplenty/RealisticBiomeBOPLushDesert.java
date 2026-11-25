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
import rtg.api.world.deco.DecoBoulder;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoJungleCacti;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.GroundEffect;
import rtg.api.world.terrain.heighteffect.HeightEffect;
import rtg.api.world.terrain.heighteffect.HeightVariation;
import rtg.api.world.terrain.heighteffect.RaiseEffect;
import rtg.api.world.terrain.heighteffect.VariableRuggednessEffect;

public class RealisticBiomeBOPLushDesert extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.lush_desert.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPLushDesert() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().ALLOW_CACTUS).set(true);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPLushDesert.TerrainBOPLushDesert(65.0F, 40.0F, 10.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPLushDesert.SurfaceBOPLushDesert(this.getConfig(), biome.topBlock, biome.fillerBlock, Blocks.GRASS.getDefaultState(), biome.fillerBlock, 40.0F, 0.5F, 10.0F, 0.5F);
   }

   public void initDecos() {
      DecoBoulder decoBoulder = new DecoBoulder();
      decoBoulder.setBoulderBlock(Blocks.COBBLESTONE.getDefaultState());
      decoBoulder.setMaxY(80);
      decoBoulder.setChance(16);
      decoBoulder.setStrengthFactor(1.0F);
      this.addDeco(decoBoulder);
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.getDistribution().setNoiseDivisor(80.0F);
      decoFallenTree.getDistribution().setNoiseFactor(60.0F);
      decoFallenTree.getDistribution().setNoiseAddend(-15.0F);
      decoFallenTree.setLogConditionChance(12);
      decoFallenTree.setRandomLogBlocks(new IBlockState[]{Blocks.LOG2.getStateFromMeta(1), BOPBlocks.log_3.getStateFromMeta(2), Blocks.LOG.getDefaultState()});
      decoFallenTree.setMinSize(3);
      decoFallenTree.setMaxSize(5);
      this.addDeco(decoFallenTree, this.getConfig().ALLOW_LOGS.get());
      DecoJungleCacti decoJungleCacti = new DecoJungleCacti();
      decoJungleCacti.setStrengthFactor(8.0F);
      decoJungleCacti.setMaxY(110);
      this.addDeco(decoJungleCacti, this.getConfig().ALLOW_CACTUS.get());
   }

   public static class SurfaceBOPLushDesert extends SurfaceBase {
      private IBlockState blockMixTop;
      private IBlockState blockMixFiller;
      private float floMixWidth;
      private float floMixHeight;
      private float floSmallWidth;
      private float floSmallStrength;

      public SurfaceBOPLushDesert(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFiller, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
         super(config, top, filler);
         this.blockMixTop = mixTop;
         this.blockMixFiller = mixFiller;
         this.floMixWidth = mixWidth;
         this.floMixHeight = mixHeight;
         this.floSmallWidth = smallWidth;
         this.floSmallStrength = smallStrength;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 3.4F;
         boolean mix = false;

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
                  if (simplex.noise2f((float)i / this.floMixWidth, (float)j / this.floMixWidth) + simplex.noise2f((float)i / this.floSmallWidth, (float)j / this.floSmallWidth) * this.floSmallStrength > this.floMixHeight) {
                     primer.setBlockState(x, k, z, this.blockMixTop);
                     mix = true;
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
                  }
               } else if (depth < 4) {
                  if (mix) {
                     primer.setBlockState(x, k, z, this.blockMixFiller);
                  } else {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               }
            }
         }

      }
   }

   public static class TerrainBOPLushDesert extends TerrainBase {
      private float minHeight;
      private float mesaWavelength;
      private float hillStrength;
      private float topBumpinessHeight = 2.0F;
      private float topBumpinessWavelength = 15.0F;
      private HeightEffect height;
      private HeightEffect groundEffect;

      public TerrainBOPLushDesert(float minHeight, float maxHeight, float hillStrength) {
         this.minHeight = minHeight;
         this.mesaWavelength = maxHeight;
         this.hillStrength = hillStrength;
         this.groundEffect = new GroundEffect(3.0F);
         HeightVariation topVariation = new HeightVariation();
         topVariation.height = hillStrength;
         topVariation.octave = 1;
         topVariation.wavelength = VariableRuggednessEffect.STANDARD_RUGGEDNESS_WAVELENGTH;
         HeightVariation topBumpiness = new HeightVariation();
         topBumpiness.height = this.topBumpinessHeight;
         topBumpiness.wavelength = this.topBumpinessWavelength;
         topBumpiness.octave = 3;
         this.height = new VariableRuggednessEffect(new RaiseEffect(0.0F), topVariation.plus(topBumpiness).plus(new RaiseEffect(hillStrength)), 0.3F, 0.15F, this.mesaWavelength);
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(this.minHeight + this.groundEffect.added(rtgWorld, (float)x, (float)y), river) + this.height.added(rtgWorld, (float)x, (float)y);
      }
   }
}
