package rtg.world.biome.realistic.vanilla;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.PlateauUtil;
import rtg.api.util.noise.ISimplexData2D;
import rtg.api.util.noise.SimplexData2D;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.collection.DecoCollectionDesertRiver;
import rtg.api.world.deco.collection.DecoCollectionMesa;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.VoronoiPlateauEffect;

public class RealisticBiomeVanillaMesaPlateau extends RealisticBiomeBase {
   public static Biome biome = Biomes.MESA_CLEAR_ROCK;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaMesaPlateau() {
      super(biome);
   }

   public Biome preferredBeach() {
      return biome;
   }

   public void initConfig() {
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.1F);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_CACTUS).set(true);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_2_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().ALLOW_PLATEAU_MODIFICATIONS).set(false);
      this.getConfig().addProperty(this.getConfig().PLATEAU_GRADIENT_BLOCK_LIST).set(PlateauUtil.getMesaPlateauBlocks());
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaMesaPlateau.TerrainRTGMesaPlateau(67.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaMesaPlateau.SurfaceVanillaMesaPlateau(this.getConfig(), biome.topBlock, BlockUtil.getStateClay(EnumDyeColor.ORANGE), 0);
   }

   public void rReplace(ChunkPrimer primer, int i, int j, int x, int y, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
      this.rReplaceWithRiver(primer, i, j, x, y, depth, rtgWorld, noise, river, base);
   }

   public void initDecos() {
      this.addDecoCollection(new DecoCollectionDesertRiver(this.getConfig()));
      this.addDecoCollection(new DecoCollectionMesa(this.getConfig()));
   }

   public void overrideDecorations() {
      this.baseBiome().decorator.cactiPerChunk = -999;
   }

   public class SurfaceVanillaMesaPlateau extends SurfaceBase {
      private int grassRaise = 0;
      private IBlockState mixBlock;
      private IBlockState mix2Block;

      public SurfaceVanillaMesaPlateau(BiomeConfig config, IBlockState top, IBlockState fill, int grassHeight) {
         super(config, top, fill);
         this.grassRaise = grassHeight;
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), BlockUtil.getStateClay(EnumDyeColor.ORANGE));
         this.mix2Block = this.getConfigBlock(config.SURFACE_MIX_2_BLOCK.get(), Blocks.RED_SANDSTONE.getDefaultState());
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.3F;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (cliff) {
                  primer.setBlockState(x, k, z, PlateauUtil.getPlateauBand(rtgWorld, RealisticBiomeVanillaMesaPlateau.this, i, k, j));
               } else if (k > 74 + this.grassRaise) {
                  if (depth == 0) {
                     if (rand.nextInt(5) == 0) {
                        primer.setBlockState(x, k, z, this.mix2Block);
                     } else {
                        primer.setBlockState(x, k, z, this.topBlock);
                     }
                  } else if (depth < 4) {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               } else if (depth == 0 && k > 61) {
                  int r = (int)((float)(k - (62 + this.grassRaise)) / 2.0F);
                  if (rand.nextInt(r + 2) == 0) {
                     primer.setBlockState(x, k, z, this.mixBlock);
                  } else if (rand.nextInt((int)((float)r / 2.0F) + 2) == 0) {
                     primer.setBlockState(x, k, z, this.mix2Block);
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

   public static class TerrainRTGMesaPlateau extends TerrainBase {
      private static final float stepStart = 0.25F;
      private static final float stepFinish = 0.4F;
      private static final float stepHeight = 32.0F;
      final VoronoiPlateauEffect plateau = new VoronoiPlateauEffect();
      final int groundNoise;
      private float jitterWavelength = 30.0F;
      private float jitterAmplitude = 10.0F;
      private float bumpinessMultiplier = 0.05F;
      private float bumpinessWavelength = 10.0F;

      public TerrainRTGMesaPlateau(float base) {
         this.plateau.pointWavelength = 200.0F;
         this.base = base;
         this.groundNoise = 4;
      }

      public float generateNoise(RTGWorld rtgWorld, int passedX, int passedY, float border, float river) {
         ISimplexData2D jitterData = SimplexData2D.newDisk();
         rtgWorld.simplexInstance(1).multiEval2D((double)((float)passedX / this.jitterWavelength), (double)((float)passedY / this.jitterWavelength), jitterData);
         float x = (float)((double)passedX + jitterData.getDeltaX() * (double)this.jitterAmplitude);
         float y = (float)((double)passedY + jitterData.getDeltaY() * (double)this.jitterAmplitude);
         float bordercap;
         bordercap = (bordercap = border * 3.5F - 2.5F) > 1.0F ? 1.0F : bordercap;
         float rivercap;
         rivercap = (rivercap = 3.0F * river) > 1.0F ? 1.0F : rivercap;
         float bumpiness = rtgWorld.simplexInstance(2).noise2f(x / this.bumpinessWavelength, y / this.bumpinessWavelength) * this.bumpinessMultiplier;
         float simplex = this.plateau.added(rtgWorld, x, y) * bordercap * rivercap + bumpiness;
         float added = PlateauUtil.stepIncrease(simplex, 0.25F, 0.4F, 32.0F) / border;
         return riverized(this.base + TerrainBase.groundNoise(x, y, (float)this.groundNoise, rtgWorld), river) + added;
      }
   }
}
