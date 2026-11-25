package mcjty.lostcities.dimensions.world.lost;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lostcities.LostCities;
import mcjty.lostcities.api.ILostSphere;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.BiomeTranslator;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.dimensions.world.lost.cityassets.AssetRegistries;
import mcjty.lostcities.dimensions.world.lost.cityassets.CityStyle;
import mcjty.lostcities.dimensions.world.lost.cityassets.PredefinedCity;
import mcjty.lostcities.dimensions.world.lost.cityassets.PredefinedSphere;
import mcjty.lostcities.varia.ChunkCoord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

public class CitySphere implements ILostSphere {
   private static Map<ChunkCoord, CitySphere> citySphereCache = new HashMap();
   public static final CitySphere EMPTY = new CitySphere(new ChunkCoord(0, 0, 0), 0.0F, new BlockPos(0, 0, 0), false);
   private final ChunkCoord center;
   private final BlockPos centerPos;
   private final float radius;
   private final boolean enabled;
   private Biome biome;
   private boolean monorailNorthCandidate;
   private boolean monorailSouthCandidate;
   private boolean monorailWestCandidate;
   private boolean monorailEastCandidate;
   private char glassBlock = 0;
   private char baseBlock = 0;
   private char sideBlock = 0;

   private CitySphere(ChunkCoord center, float radius, BlockPos centerPos, boolean enabled) {
      this.enabled = enabled;
      this.center = center;
      this.radius = radius;
      this.centerPos = centerPos;
   }

   public static void initSphere(CitySphere sphere, LostCityChunkGenerator provider) {
      if (sphere.getBaseBlock() == 0) {
         ChunkCoord center = sphere.getCenter();
         BuildingInfo info = BuildingInfo.getBuildingInfo(center.getChunkX(), center.getChunkZ(), provider);
         CityStyle cs = info.getCityStyle();
         EaglercraftRandom rand = new EaglercraftRandom(info.provider.seed + (long)center.getChunkX() * 837971201L + (long)center.getChunkZ() * 961744153L);
         rand.nextFloat();
         rand.nextFloat();
         Character glass = info.getCompiledPalette().get(cs.getSphereGlassBlock().charValue(), rand);
         Character base = info.getCompiledPalette().get(cs.getSphereBlock().charValue(), rand);
         Character side = info.getCompiledPalette().get(cs.getSphereSideBlock().charValue(), rand);
         sphere.setBlocks(glass.charValue(), base.charValue(), side.charValue());
      }
   }

   public static boolean isInSphere(int chunkX, int chunkZ, BlockPos pos, LostCityChunkGenerator provider) {
      boolean sphere = false;
      if (provider.getProfile().isSpace()) {
         CitySphere citySphere = getCitySphere(chunkX, chunkZ, provider);
         if (citySphere.isEnabled()) {
            double sqdist = squaredDistance(citySphere.getCenterPos().getX(), citySphere.getCenterPos().getZ(), pos.getX(), pos.getZ());
            if (sqdist <= (double)(citySphere.getRadius() * citySphere.getRadius())) {
               sphere = true;
            }
         }
      }

      return sphere;
   }

   public static float getRelativeDistanceToCityCenter(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      CitySphere sphere = getCitySphere(chunkX, chunkZ, provider);
      BlockPos centerPos = sphere.getCenterPos();
      float radius = sphere.getRadius();
      int cx = chunkX * 16 + 8;
      int cz = chunkZ * 16 + 8;
      int sqdist = (cx - centerPos.getX()) * (cx - centerPos.getX()) + (cz - centerPos.getZ()) * (cz - centerPos.getZ());
      return (float)(Math.sqrt((double)sqdist) / (double)radius);
   }

   private static boolean hasNonStationMonoRail(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      if (fullyInsideCitySpere(chunkX, chunkZ, provider)) {
         return false;
      } else {
         return hasHorizontalMonorail(chunkX, chunkZ, provider) || hasVerticalMonorail(chunkX, chunkZ, provider);
      }
   }

   public static boolean hasMonorailStation(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      if (!fullyInsideCitySpere(chunkX, chunkZ, provider)) {
         return false;
      } else {
         return hasNonStationMonoRail(chunkX - 1, chunkZ, provider) || hasNonStationMonoRail(chunkX + 1, chunkZ, provider) || hasNonStationMonoRail(chunkX, chunkZ - 1, provider) || hasNonStationMonoRail(chunkX, chunkZ + 1, provider);
      }
   }

   public ChunkCoord getCenter() {
      return this.center;
   }

   public BlockPos getCenterPos() {
      return this.centerPos;
   }

   public float getRadius() {
      return this.radius;
   }

   @Nullable
   public Biome getBiome() {
      return this.biome;
   }

   public void setBlocks(char glassBlock, char baseBlock, char sideBlock) {
      this.glassBlock = glassBlock;
      this.baseBlock = baseBlock;
      this.sideBlock = sideBlock;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public char getGlassBlock() {
      return this.glassBlock;
   }

   public char getBaseBlock() {
      return this.baseBlock;
   }

   public char getSideBlock() {
      return this.sideBlock;
   }

   public static void cleanCache() {
      citySphereCache.clear();
   }

   public static boolean hasHorizontalMonorail(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      if ((chunkZ & 15) != 8) {
         return false;
      } else {
         boolean result = false;

         for(int cx = chunkX + 1; cx < chunkX + 64; ++cx) {
            if ((cx & 15) == 8) {
               CitySphere sphere = getCitySphere(cx, chunkZ, provider);
               if (sphere.isEnabled()) {
                  if (!sphere.monorailWestCandidate) {
                     return false;
                  }

                  result = true;
                  break;
               }
            }
         }

         if (!result) {
            return false;
         } else {
            result = false;

            for(int cx = chunkX - 1; cx > chunkX - 64; --cx) {
               if ((cx & 15) == 8) {
                  CitySphere sphere = getCitySphere(cx, chunkZ, provider);
                  if (sphere.isEnabled()) {
                     if (!sphere.monorailEastCandidate) {
                        return false;
                     }

                     result = true;
                     break;
                  }
               }
            }

            return result;
         }
      }
   }

   public static boolean hasVerticalMonorail(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      if ((chunkX & 15) != 8) {
         return false;
      } else {
         boolean result = false;

         for(int cz = chunkZ + 1; cz < chunkZ + 64; ++cz) {
            if ((cz & 15) == 8) {
               CitySphere sphere = getCitySphere(chunkX, cz, provider);
               if (sphere.isEnabled()) {
                  if (!sphere.monorailNorthCandidate) {
                     return false;
                  }

                  result = true;
                  break;
               }
            }
         }

         if (!result) {
            return false;
         } else {
            result = false;

            for(int cz = chunkZ - 1; cz > chunkZ - 64; --cz) {
               if ((cz & 15) == 8) {
                  CitySphere sphere = getCitySphere(chunkX, cz, provider);
                  if (sphere.isEnabled()) {
                     if (!sphere.monorailSouthCandidate) {
                        return false;
                     }

                     result = true;
                     break;
                  }
               }
            }

            return result;
         }
      }
   }

   private static float getSphereRadius(ChunkCoord center, LostCityChunkGenerator provider, EaglercraftRandom rand) {
      PredefinedCity city = City.getPredefinedCity(center.getChunkX(), center.getChunkZ(), provider);
      LostCityProfile profile = provider.getProfile();
      return city != null ? (float)city.getRadius() * profile.CITYSPHERE_FACTOR : (float)profile.CITY_MINRADIUS + (float)rand.nextInt(profile.CITY_MAXRADIUS - profile.CITY_MINRADIUS) * profile.CITYSPHERE_FACTOR;
   }

   public static boolean fullyInsideCitySpere(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      CitySphere sphere = getCitySphere(chunkX, chunkZ, provider);
      if (!sphere.isEnabled()) {
         return false;
      } else {
         float radius = sphere.getRadius();
         BlockPos cc = sphere.getCenterPos();
         double sqradiusOffset = (double)((radius - 2.0F) * (radius - 2.0F));
         int cx = cc.getX();
         int cz = cc.getZ();
         if (squaredDistance(cx, cz, chunkX * 16, chunkZ * 16) > sqradiusOffset) {
            return false;
         } else if (squaredDistance(cx, cz, chunkX * 16 + 15, chunkZ * 16) > sqradiusOffset) {
            return false;
         } else if (squaredDistance(cx, cz, chunkX * 16, chunkZ * 16 + 15) > sqradiusOffset) {
            return false;
         } else {
            return squaredDistance(cx, cz, chunkX * 16 + 15, chunkZ * 16 + 15) <= sqradiusOffset;
         }
      }
   }

   public static boolean intersectsWithCitySphere(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      CitySphere sphere = getCitySphere(chunkX, chunkZ, provider);
      if (!sphere.isEnabled()) {
         return false;
      } else {
         float radius = sphere.getRadius();
         BlockPos cc = sphere.getCenterPos();
         return intersectChunkWithSphere(chunkX, chunkZ, radius, cc);
      }
   }

   private static boolean intersectChunkWithSphere(int chunkX, int chunkZ, float radius, BlockPos cc) {
      double sqradiusOffset = (double)(radius * radius);
      int cx = cc.getX();
      int cz = cc.getZ();
      if (squaredDistance(cx, cz, chunkX * 16, chunkZ * 16) <= sqradiusOffset) {
         return true;
      } else if (squaredDistance(cx, cz, chunkX * 16 + 15, chunkZ * 16) <= sqradiusOffset) {
         return true;
      } else if (squaredDistance(cx, cz, chunkX * 16, chunkZ * 16 + 15) <= sqradiusOffset) {
         return true;
      } else {
         return squaredDistance(cx, cz, chunkX * 16 + 15, chunkZ * 16 + 15) <= sqradiusOffset;
      }
   }

   public static boolean onCitySphereBorder(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      CitySphere sphere = getCitySphere(chunkX, chunkZ, provider);
      if (!sphere.isEnabled()) {
         return false;
      } else {
         float radius = sphere.getRadius();
         BlockPos cc = sphere.getCenterPos();
         double sqradiusOffset = (double)(radius * radius);
         int cx = cc.getX();
         int cz = cc.getZ();
         int cnt = 0;
         if (squaredDistance(cx, cz, chunkX * 16, chunkZ * 16) <= sqradiusOffset) {
            ++cnt;
         }

         if (squaredDistance(cx, cz, chunkX * 16 + 15, chunkZ * 16) <= sqradiusOffset) {
            ++cnt;
         }

         if (squaredDistance(cx, cz, chunkX * 16, chunkZ * 16 + 15) <= sqradiusOffset) {
            ++cnt;
         }

         if (squaredDistance(cx, cz, chunkX * 16 + 15, chunkZ * 16 + 15) <= sqradiusOffset) {
            ++cnt;
         }

         return cnt > 0 && cnt < 4;
      }
   }

   public static double squaredDistance(int cx, int cz, int x, int z) {
      return (double)((cx - x) * (cx - x) + (cz - z) * (cz - z));
   }

   private static CitySphere getSphereAtCenter(ChunkCoord center, LostCityChunkGenerator provider, @Nullable PredefinedSphere predef) {
      int chunkX = center.getChunkX();
      int chunkZ = center.getChunkZ();
      EaglercraftRandom rand = new EaglercraftRandom(provider.seed + (long)chunkX * 961744153L + (long)chunkZ * 837971201L);
      rand.nextFloat();
      rand.nextFloat();
      LostCityProfile profile = provider.getProfile();
      boolean enabled = predef != null || rand.nextFloat() < profile.CITYSPHERE_CHANCE;
      float radius = predef != null ? (float)predef.getRadius() : getSphereRadius(center, provider, rand);
      BlockPos centerPosition = predef != null ? new BlockPos(predef.getCenterX(), profile.GROUNDLEVEL, predef.getCenterZ()) : getSphereCenterPosition(center, provider, rand);
      CitySphere citySphere = new CitySphere(center, radius, centerPosition, enabled);
      if (enabled) {
         citySphere.monorailNorthCandidate = rand.nextFloat() < profile.CITYSPHERE_MONORAIL_CHANCE;
         citySphere.monorailSouthCandidate = rand.nextFloat() < profile.CITYSPHERE_MONORAIL_CHANCE;
         citySphere.monorailWestCandidate = rand.nextFloat() < profile.CITYSPHERE_MONORAIL_CHANCE;
         citySphere.monorailEastCandidate = rand.nextFloat() < profile.CITYSPHERE_MONORAIL_CHANCE;
         if (predef != null && predef.getBiome() != null) {
            citySphere.biome = (Biome)Biome.REGISTRY.getObject(new ResourceLocation(predef.getBiome()));
            if (citySphere.biome == null) {
               LostCities.setup.getLogger().warn("Could not find biome '" + predef.getBiome() + "'!");
            }
         } else if (profile.CITYSPHERE_SINGLE_BIOME) {
            if (profile.ALLOWED_BIOME_FACTORS.length == 0) {
               List<ResourceLocation> biomeKeys = new ArrayList(ForgeRegistries.BIOMES.getKeys());
               biomeKeys.sort((l1, l2) -> {
                  return l1.compareTo(l2);
               });
               citySphere.biome = (Biome)ForgeRegistries.BIOMES.getValue((ResourceLocation)biomeKeys.get(rand.nextInt(biomeKeys.size())));
            } else {
               List<Pair<Float, Biome>> pairs = BiomeTranslator.parseBiomes(profile.ALLOWED_BIOME_FACTORS);
               float total = 0.0F;

               for(Pair<Float, Biome> pair : pairs) {
                  total += 3.0F / ((Float)pair.getKey()).floatValue();
               }

               if ((double)total > 0.001D) {
                  float f = rand.nextFloat() * total;

                  for(Pair<Float, Biome> pair : pairs) {
                     f -= 3.0F / ((Float)pair.getKey()).floatValue();
                     if (f <= 0.0F) {
                        citySphere.biome = (Biome)pair.getRight();
                        break;
                     }
                  }
               }
            }
         }
      }

      return citySphere;
   }

   @Nonnull
   public static CitySphere getCitySphere(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      ChunkCoord coord = new ChunkCoord(provider.dimensionId, chunkX, chunkZ);
      if (!citySphereCache.containsKey(coord)) {
         for(PredefinedSphere predef : AssetRegistries.PREDEFINED_SPHERES.getIterable()) {
            if (predef.getDimension() == provider.dimensionId && intersectChunkWithSphere(chunkX, chunkZ, (float)predef.getRadius(), new BlockPos(predef.getCenterX(), 0, predef.getCenterZ()))) {
               ChunkCoord center = new ChunkCoord(provider.dimensionId, predef.getChunkX(), predef.getChunkZ());
               CitySphere sphere = getSphereAtCenter(center, provider, predef);
               updateCache(coord, sphere);
               return sphere;
            }
         }

         CitySphere sphere;
         if (provider.getProfile().CITYSPHERE_ONLY_PREDEFINED) {
            sphere = EMPTY;
         } else {
            int cx = (chunkX & -16) + 8;
            int cz = (chunkZ & -16) + 8;
            ChunkCoord center = new ChunkCoord(provider.dimensionId, cx, cz);
            sphere = getSphereAtCenter(center, provider, (PredefinedSphere)null);
         }

         updateCache(coord, sphere);
         return sphere;
      } else {
         return (CitySphere)citySphereCache.get(coord);
      }
   }

   private static void updateCache(ChunkCoord coord, CitySphere sphere) {
      citySphereCache.put(coord, sphere);
      BlockPos centerPos = sphere.getCenterPos();
      int radius = (int)sphere.getRadius();
      if ((float)radius < 1.0E-4F) {
         citySphereCache.put(sphere.center, sphere);
      } else {
         for(int cx = centerPos.getX() - radius - 16; cx <= centerPos.getX() + radius + 16; cx += 16) {
            for(int cz = centerPos.getZ() - radius - 16; cz <= centerPos.getZ() + radius + 16; cz += 16) {
               ChunkCoord cc = new ChunkCoord(sphere.getCenter().getDimension(), cx >> 4, cz >> 4);
               if (intersectChunkWithSphere(cc.getChunkX(), cc.getChunkZ(), (float)radius, centerPos)) {
                  citySphereCache.put(cc, sphere);
               }
            }
         }

      }
   }

   private static BlockPos getSphereCenterPosition(ChunkCoord center, LostCityChunkGenerator provider, EaglercraftRandom rand) {
      int cx = center.getChunkX() * 16 + rand.nextInt(16) - 8;
      int cz = center.getChunkZ() * 16 + rand.nextInt(16) - 8;
      return new BlockPos(cx, provider.getProfile().GROUNDLEVEL, cz);
   }
}
