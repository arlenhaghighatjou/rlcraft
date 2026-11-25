package rtg.world.biome.realistic.biomesyougo;

import java.util.Arrays;
import java.util.Collections;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.DecoWorldGen;
import rtg.api.world.gen.feature.WorldGenPond;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBYGDeadSea extends RealisticBiomeBYGBase {
   private static IBlockState bygMixBlock = BlockUtil.getBlockStateFromCfgString("byg:sodalite", BlockUtil.getStateStone(EnumType.GRANITE));

   public RealisticBiomeBYGDeadSea(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public Biome preferredBeach() {
      return this.baseBiome();
   }

   public double waterLakeMult() {
      return 4.0D;
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_2_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().PLATEAU_GRADIENT_BLOCK_LIST).set((Comparable[])Collections.unmodifiableCollection(Arrays.asList("minecraft:stone", "minecraft:cobblestone", "minecraft:stone", "minecraft:stone", "minecraft:cobblestone", "byg:sodalite")).toArray(new String[0]));
   }

   public void initDecos() {
      WorldGenPond pond = new WorldGenPond(Blocks.WATER.getDefaultState());
      DecoWorldGen decoPond = new DecoWorldGen(pond);
      decoPond.setChance(1);
      decoPond.setMinY(62);
      decoPond.setMaxY(66);
      decoPond.setEventType(EventType.LAKE_WATER);
      this.addDeco(decoPond);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGDeadSea.TerrainRTGMesaBryce();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBYGDeadSea.SurfaceVanillaMesaBryce(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().topBlock, 0);
   }

   public class SurfaceVanillaMesaBryce extends SurfaceBase {
      private int grassRaise = 0;
      private IBlockState mixBlock;
      private IBlockState mix2Block;

      public SurfaceVanillaMesaBryce(BiomeConfig config, IBlockState top, IBlockState fill, int grassHeight) {
         super(config, top, fill);
         this.grassRaise = grassHeight;
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), top);
         this.mix2Block = this.getConfigBlock(config.SURFACE_MIX_2_BLOCK.get(), top);
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
                  if (depth > -1 && depth < 2) {
                     if (rand.nextInt(3) == 0) {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     } else {
                        primer.setBlockState(x, k, z, this.hcStone());
                     }
                  } else if (depth < 10) {
                     primer.setBlockState(x, k, z, this.hcStone());
                  }
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

   public class TerrainRTGMesaBryce extends TerrainBase {
      private static final float height = 20.0F;

      TerrainRTGMesaBryce() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         float sn = simplex.noise2f((float)x / 2.0F, (float)y / 2.0F) * 0.5F + 0.5F;
         sn = (float)((double)sn + (double)simplex.noise2f((float)x, (float)y) * 0.2D + 0.2D);
         sn = sn + simplex.noise2f((float)x / 4.0F, (float)y / 4.0F) * 4.0F + 4.0F;
         sn = sn + simplex.noise2f((float)x / 8.0F, (float)y / 8.0F) * 2.0F + 2.0F;
         float n = 20.0F / sn * 2.0F;
         n = n + simplex.noise2f((float)x / 64.0F, (float)y / 64.0F) * 4.0F;
         n = sn < 6.0F ? n : 0.0F;
         return riverized(getTerrainBase() + n, river);
      }
   }
}
