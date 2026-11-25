package mcjty.lostcities.dimensions.world.lost;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import mcjty.lostcities.api.RailChunkType;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.varia.QualityRandom;

public class Railway {
   public static final int RAILWAY_LEVEL_OFFSET = -3;
   private static Map<ChunkCoord, Railway.RailChunkInfo> railInfo = new HashMap();

   public Railway() {
   }

   public static void cleanCache() {
      railInfo.clear();
   }

   private static Railway.RailChunkInfo getRailChunkTypeInternal(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      Random rand = new QualityRandom(provider.seed + (long)chunkZ * 2600003897L + (long)chunkX * 43600002517L);
      rand.nextFloat();
      rand.nextFloat();
      LostCityProfile profile = BuildingInfo.getProfile(chunkX, chunkZ, provider);
      float r = rand.nextFloat();
      int mx = Math.floorMod(chunkX + 1, 20);
      int mz = Math.floorMod(chunkZ + 1, 20);
      if (mx == 0 && mz == 10) {
         if (BuildingInfo.isCityRaw(chunkX, chunkZ, provider, profile)) {
            return getStationType(chunkX, chunkZ, provider, profile, r, 3, rand.nextFloat() < 0.5F ? "station_open" : "station_openroof");
         } else {
            if (profile.RAILWAYS_CAN_END) {
               boolean cityEast = BuildingInfo.isCityRaw(chunkX + 10, chunkZ, provider, profile) || BuildingInfo.isCityRaw(chunkX + 10, chunkZ - 10, provider, profile) || BuildingInfo.isCityRaw(chunkX + 10, chunkZ + 10, provider, profile);
               boolean cityWest = BuildingInfo.isCityRaw(chunkX - 10, chunkZ, provider, profile) || BuildingInfo.isCityRaw(chunkX - 10, chunkZ - 10, provider, profile) || BuildingInfo.isCityRaw(chunkX - 10, chunkZ + 10, provider, profile);
               if (!cityEast && !cityWest) {
                  return Railway.RailChunkInfo.NOTHING;
               }

               if (!cityEast) {
                  return new Railway.RailChunkInfo(RailChunkType.RAILS_END_HERE, Railway.RailDirection.WEST, -3, 3);
               }

               if (!cityWest) {
                  return new Railway.RailChunkInfo(RailChunkType.RAILS_END_HERE, Railway.RailDirection.EAST, -3, 3);
               }
            }

            return new Railway.RailChunkInfo(RailChunkType.HORIZONTAL, Railway.RailDirection.BI, -3, 3);
         }
      } else if (mx == 10 && mz == 0) {
         if (BuildingInfo.isCityRaw(chunkX, chunkZ, provider, profile)) {
            return getStationType(chunkX, chunkZ, provider, profile, r, 2, rand.nextFloat() < 0.5F ? "station_open" : "station_openroof");
         } else {
            if (profile.RAILWAYS_CAN_END) {
               boolean cityEast = BuildingInfo.isCityRaw(chunkX + 10, chunkZ - 10, provider, profile) || BuildingInfo.isCityRaw(chunkX + 10, chunkZ + 10, provider, profile);
               boolean cityWest = BuildingInfo.isCityRaw(chunkX - 10, chunkZ - 10, provider, profile) || BuildingInfo.isCityRaw(chunkX - 10, chunkZ + 10, provider, profile);
               if (!cityEast && !cityWest) {
                  return Railway.RailChunkInfo.NOTHING;
               }

               if (!cityEast) {
                  return new Railway.RailChunkInfo(RailChunkType.RAILS_END_HERE, Railway.RailDirection.WEST, -3, 2);
               }

               if (!cityWest) {
                  return new Railway.RailChunkInfo(RailChunkType.RAILS_END_HERE, Railway.RailDirection.EAST, -3, 2);
               }
            }

            return new Railway.RailChunkInfo(RailChunkType.HORIZONTAL, Railway.RailDirection.BI, -3, 2);
         }
      } else if (mx == 10 && mz == 10) {
         if (!BuildingInfo.isCityRaw(chunkX, chunkZ, provider, profile)) {
            if (profile.RAILWAYS_CAN_END) {
               boolean cityEast = BuildingInfo.isCityRaw(chunkX + 10, chunkZ, provider, profile);
               boolean cityWest = BuildingInfo.isCityRaw(chunkX - 10, chunkZ, provider, profile);
               if (!cityEast && !cityWest) {
                  return Railway.RailChunkInfo.NOTHING;
               }

               if (!cityEast) {
                  return new Railway.RailChunkInfo(RailChunkType.RAILS_END_HERE, Railway.RailDirection.WEST, -3, 1);
               }

               if (!cityWest) {
                  return new Railway.RailChunkInfo(RailChunkType.RAILS_END_HERE, Railway.RailDirection.EAST, -3, 1);
               }
            }

            return new Railway.RailChunkInfo(RailChunkType.HORIZONTAL, Railway.RailDirection.BI, -3, 1);
         } else {
            return getStationType(chunkX, chunkZ, provider, profile, r, 1, rand.nextFloat() < 0.5F ? "station_open" : "station_openroof");
         }
      } else if (mx == 0 && mz == 0) {
         return Railway.RailChunkInfo.NOTHING;
      } else if (mz != 0 && mz != 10) {
         if (mx == 5) {
            if (profile.RAILWAYS_CAN_END) {
               Railway.RailChunkInfo typeNorth = getRailChunkType(chunkX, chunkZ - mz % 10, provider, profile);
               Railway.RailChunkInfo typeSouth = getRailChunkType(chunkX, chunkZ - mz % 10 + 10, provider, profile);
               if (typeNorth.getType() == RailChunkType.NONE || typeSouth.getType() == RailChunkType.NONE) {
                  return Railway.RailChunkInfo.NOTHING;
               }
            }

            return new Railway.RailChunkInfo(RailChunkType.VERTICAL, Railway.RailDirection.EAST, -3, 1);
         } else if (mx != 15) {
            return Railway.RailChunkInfo.NOTHING;
         } else {
            if (profile.RAILWAYS_CAN_END) {
               Railway.RailChunkInfo typeNorth = getRailChunkType(chunkX, chunkZ - mz % 10, provider, profile);
               Railway.RailChunkInfo typeSouth = getRailChunkType(chunkX, chunkZ - mz % 10 + 10, provider, profile);
               if (typeNorth.getType() == RailChunkType.NONE || typeSouth.getType() == RailChunkType.NONE) {
                  return Railway.RailChunkInfo.NOTHING;
               }
            }

            return new Railway.RailChunkInfo(RailChunkType.VERTICAL, Railway.RailDirection.WEST, -3, 1);
         }
      } else if ((mx < 16 || mz == 0) && (mx < 6 || mx > 9)) {
         if ((mx < 1 || mx > 4 || mz == 0) && (mx < 11 || mx > 14)) {
            if (mz == 0 && mx == 5) {
               if (profile.RAILWAYS_CAN_END) {
                  boolean cityWest = BuildingInfo.isCityRaw(chunkX - 5, chunkZ - 10, provider, profile) || BuildingInfo.isCityRaw(chunkX - 5, chunkZ + 10, provider, profile);
                  boolean cityEast = BuildingInfo.isCityRaw(chunkX + 5, chunkZ, provider, profile);
                  if (!cityEast && !cityWest) {
                     return Railway.RailChunkInfo.NOTHING;
                  }
               }

               return new Railway.RailChunkInfo(RailChunkType.DOUBLE_BEND, Railway.RailDirection.EAST, -3, 1);
            } else if (mz == 0 && mx == 15) {
               if (profile.RAILWAYS_CAN_END) {
                  boolean cityEast = BuildingInfo.isCityRaw(chunkX + 5, chunkZ - 10, provider, profile) || BuildingInfo.isCityRaw(chunkX + 5, chunkZ + 10, provider, profile);
                  boolean cityWest = BuildingInfo.isCityRaw(chunkX - 5, chunkZ, provider, profile);
                  if (!cityEast && !cityWest) {
                     return Railway.RailChunkInfo.NOTHING;
                  }
               }

               return new Railway.RailChunkInfo(RailChunkType.DOUBLE_BEND, Railway.RailDirection.WEST, -3, 1);
            } else if (mz == 10 && mx == 5) {
               if (profile.RAILWAYS_CAN_END) {
                  boolean cityEast = BuildingInfo.isCityRaw(chunkX + 5, chunkZ, provider, profile);
                  boolean cityWest = BuildingInfo.isCityRaw(chunkX - 5, chunkZ, provider, profile);
                  if (!cityEast && !cityWest) {
                     Railway.RailChunkInfo typeNorth = getRailChunkType(chunkX, chunkZ - 10, provider, profile);
                     if (typeNorth.getType() == RailChunkType.NONE) {
                        Railway.RailChunkInfo typeSouth = getRailChunkType(chunkX, chunkZ - 10, provider, profile);
                        if (typeSouth.getType() == RailChunkType.NONE) {
                           return Railway.RailChunkInfo.NOTHING;
                        }
                     }
                  }
               }

               return new Railway.RailChunkInfo(RailChunkType.THREE_SPLIT, Railway.RailDirection.EAST, -3, 3);
            } else if (mz == 10 && mx == 15) {
               if (profile.RAILWAYS_CAN_END) {
                  boolean cityEast = BuildingInfo.isCityRaw(chunkX + 5, chunkZ, provider, profile);
                  boolean cityWest = BuildingInfo.isCityRaw(chunkX - 5, chunkZ, provider, profile);
                  if (!cityEast && !cityWest) {
                     Railway.RailChunkInfo typeNorth = getRailChunkType(chunkX, chunkZ - 10, provider, profile);
                     if (typeNorth.getType() == RailChunkType.NONE) {
                        Railway.RailChunkInfo typeSouth = getRailChunkType(chunkX, chunkZ - 10, provider, profile);
                        if (typeSouth.getType() == RailChunkType.NONE) {
                           return Railway.RailChunkInfo.NOTHING;
                        }
                     }
                  }
               }

               return new Railway.RailChunkInfo(RailChunkType.THREE_SPLIT, Railway.RailDirection.WEST, -3, 3);
            } else {
               return Railway.RailChunkInfo.NOTHING;
            }
         } else {
            Railway.RailChunkInfo adjacent = getRailChunkType(chunkX - 1, chunkZ, provider, profile);
            Railway.RailDirection direction = adjacent.getDirection();
            if (direction == Railway.RailDirection.BI || adjacent.getType() == RailChunkType.RAILS_END_HERE) {
               direction = Railway.RailDirection.EAST;
            }

            return testAdjacentRailChunk(r, adjacent, direction, chunkX + 1, chunkZ, provider, profile);
         }
      } else {
         Railway.RailChunkInfo adjacent = getRailChunkType(chunkX + 1, chunkZ, provider, profile);
         Railway.RailDirection direction = adjacent.getDirection();
         if (direction == Railway.RailDirection.BI || adjacent.getType() == RailChunkType.RAILS_END_HERE) {
            direction = Railway.RailDirection.WEST;
         }

         return testAdjacentRailChunk(r, adjacent, direction, chunkX - 1, chunkZ, provider, profile);
      }
   }

   private static Railway.RailChunkInfo getStationType(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile, float r, int rails, String part) {
      int cityLevel = BuildingInfo.getCityLevel(chunkX, chunkZ, provider);
      if (cityLevel > 2) {
         return new Railway.RailChunkInfo(RailChunkType.STATION_UNDERGROUND, Railway.RailDirection.BI, -3, rails);
      } else {
         int highwayX = Highway.getXHighwayLevel(chunkX, chunkZ, provider, profile);
         int highwayZ = Highway.getZHighwayLevel(chunkX, chunkZ, provider, profile);
         if ((highwayX == -1 || cityLevel < highwayX) && (highwayZ == -1 || cityLevel < highwayZ)) {
            highwayZ = Highway.getZHighwayLevel(chunkX - 1, chunkZ, provider, profile);
            if (highwayZ != -1 && cityLevel >= highwayZ) {
               return new Railway.RailChunkInfo(RailChunkType.STATION_UNDERGROUND, Railway.RailDirection.BI, -3, rails);
            } else {
               highwayZ = Highway.getZHighwayLevel(chunkX + 1, chunkZ, provider, profile);
               if (highwayZ != -1 && cityLevel >= highwayZ) {
                  return new Railway.RailChunkInfo(RailChunkType.STATION_UNDERGROUND, Railway.RailDirection.BI, -3, rails);
               } else {
                  return r < 0.5F ? new Railway.RailChunkInfo(RailChunkType.STATION_SURFACE, Railway.RailDirection.BI, cityLevel, rails, part) : new Railway.RailChunkInfo(RailChunkType.STATION_UNDERGROUND, Railway.RailDirection.BI, -3, rails);
               }
            }
         } else {
            return new Railway.RailChunkInfo(RailChunkType.STATION_UNDERGROUND, Railway.RailDirection.BI, -3, rails);
         }
      }
   }

   public static Railway.RailChunkInfo getRailChunkType(int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      ChunkCoord key = new ChunkCoord(provider.dimensionId, chunkX, chunkZ);
      if (railInfo.containsKey(key)) {
         return (Railway.RailChunkInfo)railInfo.get(key);
      } else {
         Railway.RailChunkInfo info = getRailChunkTypeInternal(chunkX, chunkZ, provider);
         if (provider.getProfile().isSpace() && CitySphere.onCitySphereBorder(chunkX, chunkZ, provider)) {
            info = Railway.RailChunkInfo.NOTHING;
         } else if (info.getType().isStation()) {
            if (!profile.RAILWAY_STATIONS_ENABLED) {
               info = Railway.RailChunkInfo.NOTHING;
            }
         } else if (!profile.RAILWAYS_ENABLED) {
            info = Railway.RailChunkInfo.NOTHING;
         }

         railInfo.put(key, info);
         return info;
      }
   }

   private static Railway.RailChunkInfo testAdjacentRailChunk(float r, Railway.RailChunkInfo adjacent, Railway.RailDirection direction, int chunkX, int chunkZ, LostCityChunkGenerator provider, LostCityProfile profile) {
      switch(adjacent.getType()) {
      case NONE:
         return Railway.RailChunkInfo.NOTHING;
      case STATION_SURFACE:
         int highwayX = Highway.getXHighwayLevel(chunkX, chunkZ, provider, profile);
         int highwayZ = Highway.getZHighwayLevel(chunkX, chunkZ, provider, profile);
         if (highwayX != -1 && adjacent.getLevel() == highwayX || highwayZ != -1 && adjacent.getLevel() == highwayZ) {
            r = 1.0F;
         }

         if (r < 0.4F) {
            return new Railway.RailChunkInfo(RailChunkType.STATION_EXTENSION_SURFACE, direction, adjacent.getLevel(), adjacent.getRails());
         } else {
            return (adjacent.getLevel() & 1) == 0 ? new Railway.RailChunkInfo(RailChunkType.GOING_DOWN_ONE_FROM_SURFACE, direction, adjacent.getLevel() - 1, adjacent.getRails()) : new Railway.RailChunkInfo(RailChunkType.GOING_DOWN_TWO_FROM_SURFACE, direction, adjacent.getLevel() - 2, adjacent.getRails());
         }
      case STATION_UNDERGROUND:
         return r < 0.4F ? new Railway.RailChunkInfo(RailChunkType.STATION_EXTENSION_UNDERGROUND, direction, adjacent.getLevel(), adjacent.getRails()) : new Railway.RailChunkInfo(RailChunkType.HORIZONTAL, direction, adjacent.getLevel(), adjacent.getRails());
      case STATION_EXTENSION_SURFACE:
         if ((adjacent.getLevel() & 1) == 0) {
            return new Railway.RailChunkInfo(RailChunkType.GOING_DOWN_ONE_FROM_SURFACE, direction, adjacent.getLevel() - 1, adjacent.getRails());
         }

         return new Railway.RailChunkInfo(RailChunkType.GOING_DOWN_TWO_FROM_SURFACE, direction, adjacent.getLevel() - 2, adjacent.getRails());
      case STATION_EXTENSION_UNDERGROUND:
         return new Railway.RailChunkInfo(RailChunkType.HORIZONTAL, direction, adjacent.getLevel(), adjacent.getRails());
      case GOING_DOWN_FURTHER:
      case GOING_DOWN_ONE_FROM_SURFACE:
      case GOING_DOWN_TWO_FROM_SURFACE:
         if (adjacent.getLevel() == -3) {
            return new Railway.RailChunkInfo(RailChunkType.HORIZONTAL, direction, adjacent.getLevel(), adjacent.getRails());
         }

         return new Railway.RailChunkInfo(RailChunkType.GOING_DOWN_FURTHER, direction, adjacent.getLevel() - 2, adjacent.getRails());
      case RAILS_END_HERE:
         if (direction == adjacent.getDirection()) {
            return new Railway.RailChunkInfo(RailChunkType.HORIZONTAL, direction, adjacent.getLevel(), adjacent.getRails());
         }

         return Railway.RailChunkInfo.NOTHING;
      case HORIZONTAL:
         return adjacent;
      default:
         throw new RuntimeException("This is really impossible!");
      }
   }

   public static void main(String[] args) {
      int chunkX = -16;
      int chunkZ = -1;
      int mx = Math.floorMod(chunkX + 1, 20);
      int mz = Math.floorMod(chunkZ + 1, 20);
      System.out.println("mx = " + mx);
      System.out.println("mz = " + mz);

      for(int i = -40; i < 40; ++i) {
         System.out.println("Math.floorMod(" + i + ", 20) = " + Math.floorMod(i, 20));
      }

   }

   public static class RailChunkInfo {
      private final RailChunkType type;
      private final Railway.RailDirection direction;
      private final int level;
      private final int rails;
      private final String part;
      public static final Railway.RailChunkInfo NOTHING = new Railway.RailChunkInfo(RailChunkType.NONE, Railway.RailDirection.BI, 0, 0);

      public RailChunkInfo(RailChunkType type, Railway.RailDirection direction, int level, int rails) {
         this(type, direction, level, rails, (String)null);
      }

      public RailChunkInfo(RailChunkType type, Railway.RailDirection direction, int level, int rails, String part) {
         this.type = type;
         this.direction = direction;
         this.level = level;
         this.rails = rails;
         this.part = part;
      }

      public RailChunkType getType() {
         return this.type;
      }

      public Railway.RailDirection getDirection() {
         return this.direction;
      }

      public int getLevel() {
         return this.level;
      }

      public int getRails() {
         return this.rails;
      }

      public String getPart() {
         return this.part;
      }
   }

   public static enum RailDirection {
      BI,
      WEST,
      EAST;

      private RailDirection() {
      }
   }
}
