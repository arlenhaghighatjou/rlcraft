package mcjty.lostcities.dimensions.world;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import mcjty.lostcities.config.BiomeSelectionStrategy;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.lost.CitySphere;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

public class LostWorldFilteredBiomeProvider extends BiomeProvider {
   private final World world;
   private final BiomeProvider original;
   private LostCityChunkGenerator provider;
   private final BiomeTranslator biomeTranslator;
   private final BiomeTranslator outsideTranslator;

   public LostWorldFilteredBiomeProvider(World world, BiomeProvider original, String[] allowedBiomeFactors, String[] manualBiomeMappings, BiomeSelectionStrategy strategy, String[] outsideBiomeFactors, String[] outsideManualBiomeMappings, BiomeSelectionStrategy outsideStrategy) {
      this.world = world;
      this.original = original;
      this.biomeTranslator = new BiomeTranslator(allowedBiomeFactors, manualBiomeMappings, strategy);
      this.outsideTranslator = new BiomeTranslator(outsideBiomeFactors.length == 0 ? allowedBiomeFactors : outsideBiomeFactors, outsideManualBiomeMappings, outsideStrategy);
   }

   private LostCityChunkGenerator getProvider() {
      if (this.provider == null) {
         this.provider = WorldTypeTools.getChunkGenerator(this.world.provider.getDimension());
      }

      return this.provider;
   }

   public Biome getBiome(BlockPos pos) {
      LostCityProfile profile = WorldTypeTools.getProfile(this.world);
      Biome originalBiome = this.original.getBiome(pos);
      if (!(this.world instanceof WorldServer)) {
         return originalBiome;
      } else if (this.getProvider() == null) {
         return originalBiome;
      } else {
         if (profile.isSpace() && profile.CITYSPHERE_LANDSCAPE_OUTSIDE) {
            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;
            CitySphere sphere = CitySphere.getCitySphere(chunkX, chunkZ, this.getProvider());
            if (!sphere.isEnabled()) {
               return this.outsideTranslator.translate(originalBiome);
            }

            float radius = sphere.getRadius();
            BlockPos cc = sphere.getCenterPos();
            double sqradiusOffset = (double)((radius - 2.0F) * (radius - 2.0F));
            int cx = cc.getX();
            int cz = cc.getZ();
            if (CitySphere.squaredDistance(cx, cz, pos.getX(), pos.getZ()) > sqradiusOffset) {
               return this.outsideTranslator.translate(originalBiome);
            }

            if (sphere.getBiome() != null) {
               return sphere.getBiome();
            }
         }

         return this.biomeTranslator.translate(originalBiome);
      }
   }

   private void translateList(Biome[] biomes, int topx, int topz, int width, int height) {
      if (this.world instanceof WorldServer) {
         if (this.getProvider() != null) {
            LostCityProfile profile = WorldTypeTools.getProfile(this.world);
            if (profile.isSpace() && profile.CITYSPHERE_LANDSCAPE_OUTSIDE) {
               int chunkX = topx >> 4;
               int chunkZ = topz >> 4;
               CitySphere sphere = CitySphere.getCitySphere(chunkX, chunkZ, this.getProvider());
               if (sphere.isEnabled()) {
                  float radius = sphere.getRadius();
                  double sqradiusOffset = (double)((radius - 2.0F) * (radius - 2.0F));
                  BlockPos cc = sphere.getCenterPos();
                  int cx = cc.getX();
                  int cz = cc.getZ();

                  for(int z = 0; z < height; ++z) {
                     for(int x = 0; x < width; ++x) {
                        int i = x + z * width;
                        if (CitySphere.squaredDistance(cx, cz, topx + x, topz + z) > sqradiusOffset) {
                           biomes[i] = this.outsideTranslator.translate(biomes[i]);
                        } else if (sphere.getBiome() != null) {
                           biomes[i] = sphere.getBiome();
                        } else {
                           biomes[i] = this.biomeTranslator.translate(biomes[i]);
                        }
                     }
                  }
               } else {
                  for(int i = 0; i < biomes.length; ++i) {
                     biomes[i] = this.outsideTranslator.translate(biomes[i]);
                  }
               }

            } else {
               for(int i = 0; i < biomes.length; ++i) {
                  biomes[i] = this.biomeTranslator.translate(biomes[i]);
               }

            }
         }
      }
   }

   public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
      biomes = this.original.getBiomesForGeneration(biomes, x, z, width, height);
      this.translateList(biomes, x, z, width, height);
      return biomes;
   }

   public Biome[] getBiomes(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth) {
      oldBiomeList = this.original.getBiomes(oldBiomeList, x, z, width, depth);
      this.translateList(oldBiomeList, x, z, width, depth);
      return oldBiomeList;
   }

   public Biome[] getBiomesAlternate(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth) {
      oldBiomeList = this.original.getBiomes(oldBiomeList, x, z, width, depth);

      for(int i = 0; i < oldBiomeList.length; ++i) {
         oldBiomeList[i] = this.outsideTranslator.translate(oldBiomeList[i]);
      }

      return oldBiomeList;
   }

   public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
      return this.getBiomes(listToReuse, x, z, width, length);
   }

   @Nullable
   public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
      return this.original.findBiomePosition(x, z, range, biomes, random);
   }

   public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
      boolean result = false;
      Biome[] biomeList = this.getBiomes((Biome[])null, x, z, radius, radius);

      for(int i = 0; i < biomeList.length; ++i) {
         if (allowed.contains(biomeList[i])) {
            result = true;
         }
      }

      return result;
   }

   public boolean isFixedBiome() {
      return false;
   }

   public Biome getFixedBiome() {
      return null;
   }
}
