package mcjty.lostcities.dimensions.world.lost;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.varia.ChunkCoord;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class Highway {
   private static NoiseGeneratorPerlin perlinX = null;
   private static NoiseGeneratorPerlin perlinZ = null;
   private static Map<ChunkCoord, Integer> xHighwayLevelCache = new HashMap();
   private static Map<ChunkCoord, Integer> zHighwayLevelCache = new HashMap();

   public Highway() {
   }

   private static void makePerlin(long seed) {
      if (perlinX == null) {
         Random random = new Random(seed);
         perlinX = new NoiseGeneratorPerlin(random, 4);
      }

      if (perlinZ == null) {
         Random random = new Random(seed ^ 879190747L);
         perlinZ = new NoiseGeneratorPerlin(random, 4);
      }

   }

   public static void cleanCache() {
      perlinX = null;
      perlinZ = null;
      xHighwayLevelCache.clear();
      zHighwayLevelCache.clear();
   }

   public static int getXHighwayLevel(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      return getHighwayLevel(provider, profile, xHighwayLevelCache, (cp) -> {
         return hasXHighway(cp, profile);
      }, Orientation.X, new ChunkCoord(provider.dimensionId, chunkX, chunkZ));
   }

   public static int getZHighwayLevel(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      return getHighwayLevel(provider, profile, zHighwayLevelCache, (cp) -> {
         return hasZHighway(cp, profile);
      }, Orientation.Z, new ChunkCoord(provider.dimensionId, chunkX, chunkZ));
   }

   private static int getHighwayLevel(LostCityChunkGenerator provider, LostCityProfile profile, Map<ChunkCoord, Integer> cache, Function<ChunkCoord, Boolean> hasHighway, Orientation orientation, ChunkCoord cp) {
      if (cache.containsKey(cp)) {
         return ((Integer)cache.get(cp)).intValue();
      } else {
         int mask = profile.HIGHWAY_DISTANCE_MASK;
         if (mask <= 0) {
            cache.put(cp, Integer.valueOf(-1));
            return -1;
         } else if ((cp.getCoord(orientation.getOpposite()) & mask) != 0) {
            cache.put(cp, Integer.valueOf(-1));
            return -1;
         } else if (provider.getProfile().isSpace() && CitySphere.intersectsWithCitySphere(cp.getChunkX(), cp.getChunkZ(), provider)) {
            cache.put(cp, Integer.valueOf(-1));
            return -1;
         } else {
            makePerlin(provider.seed);
            if (!((Boolean)hasHighway.apply(cp)).booleanValue()) {
               cache.put(cp, Integer.valueOf(-1));
               return -1;
            } else {
               ChunkCoord lower;
               for(lower = cp.lower(orientation); ((Boolean)hasHighway.apply(lower)).booleanValue(); lower = lower.lower(orientation)) {
                  ;
               }

               lower = lower.higher(orientation);

               ChunkCoord higher;
               for(higher = cp.higher(orientation); ((Boolean)hasHighway.apply(higher)).booleanValue(); higher = higher.higher(orientation)) {
                  ;
               }

               higher = higher.lower(orientation);
               int level = -1;
               if (higher.getCoord(orientation) - lower.getCoord(orientation) >= 5) {
                  boolean valid;
                  if (profile.HIGHWAY_REQUIRES_TWO_CITIES) {
                     valid = BuildingInfo.isCityRaw(lower.getChunkX(), lower.getChunkZ(), provider, profile) && BuildingInfo.isCityRaw(higher.getChunkX(), higher.getChunkZ(), provider, profile);
                  } else {
                     valid = BuildingInfo.isCityRaw(lower.getChunkX(), lower.getChunkZ(), provider, profile) || BuildingInfo.isCityRaw(higher.getChunkX(), higher.getChunkZ(), provider, profile);
                  }

                  if (valid) {
                     switch(profile.HIGHWAY_LEVEL_FROM_CITIES_MODE) {
                     case 0:
                        level = BuildingInfo.getCityLevel(lower.getChunkX(), lower.getChunkZ(), provider);
                        break;
                     case 1:
                        level = Math.min(BuildingInfo.getCityLevel(lower.getChunkX(), lower.getChunkZ(), provider), BuildingInfo.getCityLevel(higher.getChunkX(), higher.getChunkZ(), provider));
                        break;
                     case 2:
                        level = Math.max(BuildingInfo.getCityLevel(lower.getChunkX(), lower.getChunkZ(), provider), BuildingInfo.getCityLevel(higher.getChunkX(), higher.getChunkZ(), provider));
                        break;
                     case 3:
                        level = (BuildingInfo.getCityLevel(lower.getChunkX(), lower.getChunkZ(), provider) + BuildingInfo.getCityLevel(higher.getChunkX(), higher.getChunkZ(), provider)) / 2;
                        break;
                     default:
                        throw new RuntimeException("Bad value for 'highwayLevelFromCities'!");
                     }

                     for(ChunkCoord cc = lower; cc.getCoord(orientation) <= higher.getCoord(orientation); cc = cc.higher(orientation)) {
                        cache.put(cc, Integer.valueOf(level));
                     }
                  }
               }

               return level;
            }
         }
      }
   }

   private static boolean hasXHighway(ChunkCoord cp, LostCityProfile profile) {
      return perlinX.getValue((double)((float)cp.getChunkX() / profile.HIGHWAY_MAINPERLIN_SCALE), (double)((float)cp.getChunkZ() / profile.HIGHWAY_SECONDARYPERLIN_SCALE)) > (double)profile.HIGHWAY_PERLIN_FACTOR;
   }

   private static boolean hasZHighway(ChunkCoord cp, LostCityProfile profile) {
      return perlinZ.getValue((double)((float)cp.getChunkX() / profile.HIGHWAY_SECONDARYPERLIN_SCALE), (double)((float)cp.getChunkZ() / profile.HIGHWAY_MAINPERLIN_SCALE)) > (double)profile.HIGHWAY_PERLIN_FACTOR;
   }
}
