package rtg.world.biome.realistic.vanilla;

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
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.collection.DecoCollectionDesertRiver;
import rtg.api.world.deco.collection.DecoCollectionSavanna;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.GroundEffect;

public class RealisticBiomeVanillaSavanna extends RealisticBiomeBase {
   public static Biome biome = Biomes.SAVANNA;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaSavanna() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_VILLAGES.set(true);
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().ALLOW_CACTUS).set(true);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaSavanna.TerrainVanillaSavanna();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaSavanna.SurfaceVanillaSavanna(this.getConfig(), Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRASS.getDefaultState(), 13.0F, 0.27F);
   }

   public void initDecos() {
      this.addDecoCollection(new DecoCollectionDesertRiver(this.getConfig()));
      this.addDecoCollection(new DecoCollectionSavanna(this.getConfig()));
   }

   public void overrideDecorations() {
      this.baseBiome().decorator.cactiPerChunk = -999;
   }

   public static class SurfaceVanillaSavanna extends SurfaceBase {
      private IBlockState mixBlock;
      private float width;
      private float height;

      public SurfaceVanillaSavanna(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mix, float mixWidth, float mixHeight) {
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

   public static class TerrainVanillaSavanna extends TerrainBase {
      private GroundEffect groundEffect = new GroundEffect(4.0F);

      public TerrainVanillaSavanna() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(65.0F + this.groundEffect.added(rtgWorld, (float)x, (float)y), river);
      }
   }
}
