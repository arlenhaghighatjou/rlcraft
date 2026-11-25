package rtg.world.biome.realistic.vanilla;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenTrees;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.PlateauUtil;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.deco.collection.DecoCollectionDesertRiver;
import rtg.api.world.deco.collection.DecoCollectionMesa;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaMesaPlateauFM extends RealisticBiomeBase {
   public static Biome biome = Biomes.MUTATED_MESA_ROCK;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaMesaPlateauFM() {
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
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_3_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_4_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().ALLOW_PLATEAU_MODIFICATIONS).set(false);
      this.getConfig().addProperty(this.getConfig().PLATEAU_GRADIENT_BLOCK_LIST).set(PlateauUtil.getMesaPlateauBlocks());
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaMesaPlateau.TerrainRTGMesaPlateau(67.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaMesaPlateauFM.SurfaceVanillaMesaPlateauFM(this.getConfig(), biome.topBlock, BlockUtil.getStateClay(EnumDyeColor.ORANGE), 0, 0.2F, 0.6F);
   }

   public void rReplace(ChunkPrimer primer, int i, int j, int x, int y, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
      this.rReplaceWithRiver(primer, i, j, x, y, depth, rtgWorld, noise, river, base);
   }

   public void initDecos() {
      this.addDecoCollection(new DecoCollectionDesertRiver(this.getConfig()));
      this.addDecoCollection(new DecoCollectionMesa(this.getConfig()));
      DecoTree decoTree = new DecoTree(new WorldGenTrees(false));
      decoTree.setLoops(16);
      decoTree.setTreeType(DecoTree.TreeType.WORLDGEN);
      decoTree.setDistribution(new DecoTree.Distribution(24.0F, 1.0F, 0.0F));
      decoTree.setTreeConditionChance(1);
      decoTree.setTreeConditionFloat(4.0F);
      decoTree.setTreeConditionNoise(0.0F);
      decoTree.setMinY(74);
      this.addDeco(decoTree);
   }

   public void overrideDecorations() {
      this.baseBiome().decorator.cactiPerChunk = -999;
   }

   public class SurfaceVanillaMesaPlateauFM extends SurfaceBase {
      private int grassRaise = 0;
      private IBlockState mixBlock;
      private IBlockState mix2Block;
      private IBlockState mix3Block;
      private IBlockState mix4Block;
      private float mix3Height;
      private float mix4Height;

      public SurfaceVanillaMesaPlateauFM(BiomeConfig config, IBlockState top, IBlockState fill, int grassHeight, float mix3Height, float mix4Height) {
         super(config, top, fill);
         this.grassRaise = grassHeight;
         this.mix3Height = mix3Height;
         this.mix4Height = mix4Height;
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), BlockUtil.getStateClay(EnumDyeColor.ORANGE));
         this.mix2Block = this.getConfigBlock(config.SURFACE_MIX_2_BLOCK.get(), Blocks.RED_SANDSTONE.getDefaultState());
         this.mix3Block = this.getConfigBlock(config.SURFACE_MIX_3_BLOCK.get(), BlockUtil.getStateDirt(DirtType.COARSE_DIRT));
         this.mix4Block = this.getConfigBlock(config.SURFACE_MIX_4_BLOCK.get(), Blocks.GRASS.getDefaultState());
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.3F;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (cliff) {
                  primer.setBlockState(x, k, z, PlateauUtil.getPlateauBand(rtgWorld, RealisticBiomeVanillaMesaPlateauFM.this, i, k, j));
               } else {
                  float mixNoise = rtgWorld.simplexInstance(0).noise2f((float)i / 12.0F, (float)j / 12.0F);
                  if (k > 74 + this.grassRaise) {
                     if (depth == 0) {
                        if (mixNoise > this.mix4Height) {
                           primer.setBlockState(x, k, z, this.mix4Block);
                        } else if (mixNoise > this.mix3Height) {
                           primer.setBlockState(x, k, z, this.mix3Block);
                        } else if (rand.nextInt(5) == 0) {
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
   }
}
