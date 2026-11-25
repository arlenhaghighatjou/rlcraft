package svenhjol.meson.helper;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.ChunkGeneratorSettings.Factory;
import svenhjol.meson.Meson;
import svenhjol.meson.iface.IMesonEnum;

public class WorldHelper {
   public static ChunkGeneratorSettings chunkGeneratorSettings = null;
   public static long lastSeed;
   public static List<WorldHelper.StructureType> temples = new ArrayList();

   public WorldHelper() {
   }

   public static boolean canGenerateStructures(World world) {
      return world.getWorldInfo().isMapFeaturesEnabled();
   }

   public static boolean canGenerateStructure(World world, WorldHelper.StructureType structure) {
      if (chunkGeneratorSettings == null) {
         return false;
      } else {
         switch(structure) {
         case STRONGHOLD:
            return chunkGeneratorSettings.useStrongholds;
         case MINESHAFT:
            return chunkGeneratorSettings.useMineShafts;
         case MANSION:
            return chunkGeneratorSettings.useMansions;
         case VILLAGE:
            return chunkGeneratorSettings.useVillages;
         case JUNGLE_PYRAMID:
         case DESERT_PYRAMID:
         case SWAMP_HUT:
         case IGLOO:
            return chunkGeneratorSettings.useTemples;
         default:
            return true;
         }
      }
   }

   public static BlockPos getNearestStronghold(World world, BlockPos chunk) {
      return getNearestStructure(world, chunk, "Stronghold");
   }

   public static BlockPos getNearestVillage(World world, BlockPos chunk) {
      return getNearestStructure(world, chunk, "Village");
   }

   public static BlockPos getNearestStructure(World world, BlockPos chunk, String structure) {
      return ((WorldServer)world).getChunkProvider().getNearestStructurePos(world, structure, chunk, false);
   }

   public static BlockPos getNearestStructure(World world, BlockPos chunk, WorldHelper.StructureType type) {
      int offset = 1000;
      if (world.isRemote) {
         return null;
      } else if (!temples.contains(type)) {
         return getNearestStructure(world, chunk, type.getCapitalizedName());
      } else {
         for(int dist = 0; dist < 50; ++dist) {
            for(int x = -1; x <= 1; ++x) {
               for(int z = -1; z <= 1; ++z) {
                  int xx = x * offset + x * dist * offset;
                  int zz = z * offset + z * dist * offset;
                  BlockPos tryPos = chunk.add(xx, 0, zz);
                  BlockPos templePos = getNearestStructure(world, tryPos, "Temple");
                  if (templePos != null) {
                     Biome biome = world.getBiome(templePos);
                     boolean found;
                     switch(type) {
                     case JUNGLE_PYRAMID:
                        found = biome == Biomes.JUNGLE || biome == Biomes.JUNGLE_HILLS;
                        break;
                     case DESERT_PYRAMID:
                        found = biome == Biomes.DESERT || biome == Biomes.DESERT_HILLS;
                        break;
                     case SWAMP_HUT:
                        found = biome == Biomes.SWAMPLAND;
                        break;
                     case IGLOO:
                        found = biome == Biomes.ICE_PLAINS || biome == Biomes.COLD_TAIGA;
                        break;
                     default:
                        found = true;
                     }

                     if (found) {
                        Meson.debug("WorldHelper: Found structure " + type + " at " + tryPos);
                        return templePos;
                     }
                  }
               }
            }
         }

         Meson.debug("WorldHelper: Failed to find structure " + type);
         return null;
      }
   }

   public static long getNearestVillageSeed(World world, BlockPos chunk) {
      BlockPos nearest = getNearestVillage(world, chunk);
      long seed = 0L;
      if (nearest != null) {
         seed = (long)nearest.toString().hashCode();
      }

      return seed;
   }

   public static double getDistanceSq(BlockPos pos1, BlockPos pos2) {
      double d0 = (double)pos1.getX();
      double d1 = (double)pos1.getZ();
      double d2 = d0 - (double)pos2.getX();
      double d3 = d1 - (double)pos2.getZ();
      return d2 * d2 + d3 * d3;
   }

   public static ChunkPos getChunkPos(BlockPos pos) {
      return new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
   }

   public static void populateWorldSettings(World world) {
      if (!world.isRemote) {
         long seed = world.getWorldInfo().getSeed();
         if (lastSeed == seed) {
            return;
         }

         lastSeed = seed;
         String generatorOptions = world.getWorldInfo().getGeneratorOptions();
         chunkGeneratorSettings = Factory.jsonToFactory(generatorOptions).build();
         Meson.log(chunkGeneratorSettings.useStrongholds);
      }

   }

   static {
      temples.add(WorldHelper.StructureType.SWAMP_HUT);
      temples.add(WorldHelper.StructureType.DESERT_PYRAMID);
      temples.add(WorldHelper.StructureType.JUNGLE_PYRAMID);
      temples.add(WorldHelper.StructureType.IGLOO);
   }

   public static enum StructureType implements IMesonEnum {
      VILLAGE,
      MINESHAFT,
      SWAMP_HUT,
      DESERT_PYRAMID,
      JUNGLE_PYRAMID,
      IGLOO,
      STRONGHOLD,
      MANSION;

      private StructureType() {
      }
   }
}
