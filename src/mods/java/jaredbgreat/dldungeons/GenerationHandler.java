package jaredbgreat.dldungeons;

import jaredbgreat.dldungeons.builder.Builder;
import jaredbgreat.dldungeons.themes.Sizes;
import jaredbgreat.dldungeons.util.cache.Coords;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GenerationHandler implements IWorldGenerator {
   private static int frequency;
   private static int factor = 6;
   private static int minXZ;
   private static Random mrand;
   private static HashSet<Integer> dimensions;

   public GenerationHandler() {
      GameRegistry.registerWorldGenerator(this, 100);
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (!world.isRemote) {
         if ((!ConfigHandler.obeyRule || world.getWorldInfo().isMapFeaturesEnabled()) && ConfigHandler.naturalSpawn) {
            this.findDungeonsToBuild(world, chunkX, chunkZ);
         }
      }
   }

   private void buildDungeonHere(World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider, int chunkX, int chunkZ) {
      if (this.isDungeonCenter(world, world.getSeed(), chunkX, chunkZ, world.provider.getDimension())) {
         try {
            Builder.placeDungeon(chunkX, chunkZ, world);
         } catch (Throwable var7) {
            System.err.println("[DLDUNGEONS] Danger!  Failed to finalize a dungeon after building!");
            var7.printStackTrace();
         }
      }

   }

   private void findDungeonsToBuild(World world, int chunkX, int chunkZ) {
      long seed = world.getSeed();
      int dim = world.provider.getDimension();
      ArrayList<Coords> dungeonLocs = new ArrayList();
      int sx = chunkX - Sizes.HUGE.chunkRadius;
      int ex = chunkX + Sizes.HUGE.chunkRadius;
      int sz = chunkZ - Sizes.HUGE.chunkRadius;
      int ez = chunkZ + Sizes.HUGE.chunkRadius;

      for(int i = sx; i <= ex; ++i) {
         for(int j = sz; j <= ez; ++j) {
            if (this.isDungeonCenter(world, seed, i, j, dim)) {
               dungeonLocs.add(new Coords(i, j, dim));
            }
         }
      }

      dungeonLocs.sort(Coords.HashCompare.C);

      try {
         Builder.buildDungeonsChunk(chunkX, chunkZ, dungeonLocs, world);
      } catch (Throwable var14) {
         var14.printStackTrace();
      }

   }

   private boolean isDungeonCenter(World world, long seed, int chunkX, int chunkZ, int dim) {
      boolean blockedBiome = false;

      for(Type type : BiomeDictionary.getTypes(world.getBiome(new BlockPos(chunkX * 16, 63, chunkZ * 16)))) {
         blockedBiome = ConfigHandler.biomeExclusions.contains(type) || blockedBiome;
      }

      if (blockedBiome) {
         return false;
      } else if (dimensions.contains(Integer.valueOf(world.provider.getDimension())) != ConfigHandler.positiveDims) {
         return false;
      } else if (Math.abs(chunkX - world.getSpawnPoint().getX() / 16) >= minXZ && Math.abs(chunkZ - world.getSpawnPoint().getZ() / 16) >= minXZ) {
         mrand = new Random(seed + (long)dim + 2027L * (long)(chunkX / factor) + 1987L * (long)(chunkZ / factor));
         int xrand = mrand.nextInt();
         int zrand = mrand.nextInt();
         int xuse = (chunkX + xrand) % factor;
         int zuse = (chunkZ + zrand) % factor;
         return xuse == 0 && zuse == 0;
      } else {
         return false;
      }
   }

   public static void setFrequency(int freqScale) {
      if (freqScale % 2 == 0) {
         factor = 2;
      } else {
         factor = 3;
      }

      factor <<= freqScale / 2;
   }

   public static void setMinXZ(int value) {
      minXZ = value;
   }

   public static void setDimensions(int[] value) {
      dimensions = new HashSet();

      for(int i = 0; i < value.length; ++i) {
         dimensions.add(Integer.valueOf(value[i]));
      }

   }

   public static void addDimension(int value) {
      dimensions.add(Integer.valueOf(value));
   }

   public static void subDimension(int value) {
      dimensions.remove(Integer.valueOf(value));
   }
}
