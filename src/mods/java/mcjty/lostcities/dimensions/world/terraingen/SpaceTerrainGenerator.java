package mcjty.lostcities.dimensions.world.terraingen;

import mcjty.lostcities.config.LostCityConfiguration;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.dimensions.world.driver.IPrimerDriver;
import mcjty.lostcities.dimensions.world.driver.OptimizedDriver;
import mcjty.lostcities.dimensions.world.driver.SafeDriver;
import mcjty.lostcities.dimensions.world.lost.CitySphere;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class SpaceTerrainGenerator {
   private LostCityChunkGenerator provider;
   private NoiseGeneratorPerlin surfaceNoise;
   private double[] surfaceBuffer = new double[256];
   private IPrimerDriver driver;

   public SpaceTerrainGenerator() {
   }

   public void setup(World world, LostCityChunkGenerator provider) {
      this.provider = provider;
      this.surfaceNoise = new NoiseGeneratorPerlin(provider.rand, 4);
      this.driver = (IPrimerDriver)(LostCityConfiguration.OPTIMIZED_CHUNKGEN ? new OptimizedDriver() : new SafeDriver());
   }

   public void generate(int chunkX, int chunkZ, ChunkPrimer primer, LostCitiesTerrainGenerator terrainGenerator) {
      this.driver.setPrimer(primer);
      CitySphere sphere = CitySphere.getCitySphere(chunkX, chunkZ, this.provider);
      CitySphere.initSphere(sphere, this.provider);
      LostCityProfile profile = this.provider.getProfile();
      LostCityProfile profileOut = this.provider.getOutsideProfile();
      boolean outsideLandscape = profile.CITYSPHERE_LANDSCAPE_OUTSIDE;
      Character baseLiquid = terrainGenerator.liquidChar;
      Character baseChar = terrainGenerator.baseChar;
      char airChar = LostCitiesTerrainGenerator.airChar;
      this.surfaceBuffer = this.surfaceNoise.getRegion(this.surfaceBuffer, (double)(chunkX * 16), (double)(chunkZ * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);
      if (sphere.isEnabled()) {
         float radius = sphere.getRadius();
         BlockPos cc = sphere.getCenterPos();
         int cx = cc.getX() - chunkX * 16;
         int cz = cc.getZ() - chunkZ * 16;
         this.fillSphere(cx, profile.GROUNDLEVEL, cz, (int)radius, sphere.getGlassBlock(), sphere.getBaseBlock(), sphere.getSideBlock(), baseLiquid.charValue(), baseChar.charValue(), outsideLandscape);
      } else if (outsideLandscape) {
         int waterLevel = profileOut.GROUNDLEVEL - profileOut.WATERLEVEL_OFFSET;

         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               double vr = profile.CITYSPHERE_OUTSIDE_SURFACE_VARIATION < 0.01F ? 0.0D : this.surfaceBuffer[x + z * 16] / (double)profile.CITYSPHERE_OUTSIDE_SURFACE_VARIATION;
               this.driver.current(x, 0, z);

               for(int y = 0; y <= Math.max(waterLevel, profileOut.GROUNDLEVEL + 30); ++y) {
                  if (y == 0) {
                     this.driver.add(LostCitiesTerrainGenerator.bedrockChar);
                  } else if ((double)y <= vr + (double)profileOut.GROUNDLEVEL) {
                     this.driver.add(terrainGenerator.baseChar);
                  } else if (y <= waterLevel) {
                     this.driver.add(baseLiquid.charValue());
                  } else {
                     this.driver.add(airChar);
                  }
               }
            }
         }
      }

   }

   private void fillSphere(int centerx, int centery, int centerz, int radius, char glass, char block, char sideBlock, char liquidChar, char baseChar, boolean outsideLandscape) {
      double sqradius = (double)(radius * radius);
      double sqradiusOffset = (double)((radius - 2) * (radius - 2));
      LostCityProfile profile = this.provider.getProfile();
      LostCityProfile profileOut = this.provider.getOutsideProfile();
      int waterLevelOut = profileOut.GROUNDLEVEL - profileOut.WATERLEVEL_OFFSET;
      int waterLevel = profile.GROUNDLEVEL - profile.WATERLEVEL_OFFSET;

      for(int x = 0; x < 16; ++x) {
         double dxdx = (double)((x - centerx) * (x - centerx));

         for(int z = 0; z < 16; ++z) {
            double dzdz = (double)((z - centerz) * (z - centerz));
            double vo = profile.CITYSPHERE_OUTSIDE_SURFACE_VARIATION < 0.01F ? 0.0D : this.surfaceBuffer[x + z * 16] / (double)profile.CITYSPHERE_OUTSIDE_SURFACE_VARIATION;
            double vr = profile.CITYSPHERE_SURFACE_VARIATION < 0.01F ? 0.0D : this.surfaceBuffer[x + z * 16] / (double)profile.CITYSPHERE_SURFACE_VARIATION;
            if (outsideLandscape) {
               this.driver.current(x, 0, z);

               for(int y = 0; y <= Math.max(Math.min(centery + radius, 255), waterLevel); ++y) {
                  double dydy = (double)((y - centery) * (y - centery));
                  double sqdist = dxdx + dydy + dzdz;
                  if (y == 0) {
                     this.driver.block(LostCitiesTerrainGenerator.bedrockChar);
                  } else if (sqdist <= sqradius) {
                     if (sqdist >= sqradiusOffset) {
                        if (y > centery) {
                           this.driver.block(glass);
                        } else {
                           this.driver.block(sideBlock);
                        }
                     } else if ((double)y < (double)centery + vr) {
                        this.driver.block(block);
                     } else if (y < waterLevel) {
                        this.driver.block(liquidChar);
                     }
                  } else if ((double)y <= vo + (double)profileOut.GROUNDLEVEL) {
                     this.driver.block(baseChar);
                  } else if (y <= waterLevelOut) {
                     this.driver.block(liquidChar);
                  }

                  this.driver.incY();
               }
            } else {
               int starty = Math.max(centery - radius, 0);
               this.driver.current(x, starty, z);

               for(int y = starty; y <= Math.min(centery + radius, 255); ++y) {
                  double dydy = (double)((y - centery) * (y - centery));
                  double sqdist = dxdx + dydy + dzdz;
                  if (sqdist <= sqradius) {
                     if (sqdist >= sqradiusOffset) {
                        if (y > centery) {
                           this.driver.block(glass);
                        } else {
                           this.driver.block(sideBlock);
                        }
                     } else if ((double)y < (double)centery + vr) {
                        this.driver.block(block);
                     } else if (y < waterLevel) {
                        this.driver.block(liquidChar);
                     }
                  }

                  this.driver.incY();
               }
            }
         }
      }

   }

   public void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer primer, Biome[] biomes, LostCitiesTerrainGenerator terrainGenerator) {
      CitySphere sphere = CitySphere.getCitySphere(chunkX, chunkZ, this.provider);
      int outsideGround = this.provider.getOutsideProfile().GROUNDLEVEL;
      int outsideWater = outsideGround - this.provider.getOutsideProfile().WATERLEVEL_OFFSET;
      int groundlevel = this.provider.getProfile().GROUNDLEVEL;
      int water = groundlevel - this.provider.getProfile().WATERLEVEL_OFFSET;
      if (sphere.isEnabled()) {
         float radius = sphere.getRadius();
         BlockPos cc = sphere.getCenterPos();
         double sqradiusOffset = (double)((radius - 2.0F) * (radius - 2.0F));
         int centerx = cc.getX() - chunkX * 16;
         int centerz = cc.getZ() - chunkZ * 16;

         for(int z = 0; z < 16; ++z) {
            double dzdz = (double)((z - centerz) * (z - centerz));

            for(int x = 0; x < 16; ++x) {
               double dxdx = (double)((x - centerx) * (x - centerx));
               double sqdist = dzdz + dxdx;
               Biome biome = biomes[x + z * 16];
               if (sqdist >= sqradiusOffset) {
                  this.genBiomeTerrain(biome, primer, chunkX * 16 + z, chunkZ * 16 + x, outsideGround, outsideWater, terrainGenerator);
               } else {
                  this.genBiomeTerrain(biome, primer, chunkX * 16 + z, chunkZ * 16 + x, groundlevel, water, terrainGenerator);
               }
            }
         }
      } else {
         for(int z = 0; z < 16; ++z) {
            for(int x = 0; x < 16; ++x) {
               Biome biome = biomes[x + z * 16];
               this.genBiomeTerrain(biome, primer, chunkX * 16 + z, chunkZ * 16 + x, outsideGround, outsideWater, terrainGenerator);
            }
         }
      }

   }

   private void genBiomeTerrain(Biome Biome, ChunkPrimer primer, int x, int z, int topLevel, int waterLevel, LostCitiesTerrainGenerator terrainGenerator) {
      this.driver.setPrimer(primer);
      char air = LostCitiesTerrainGenerator.airChar;
      char baseBlock = terrainGenerator.baseChar;
      char gravelBlock = LostCitiesTerrainGenerator.gravelChar;
      char fillerBlock = (char)Block.BLOCK_STATE_IDS.get(Biome.fillerBlock);
      char topBlock = (char)Block.BLOCK_STATE_IDS.get(Biome.topBlock);
      int cx = x & 15;
      int cz = z & 15;
      int cnt = 0;

      for(int y = topLevel + 20; y >= topLevel - 8; --y) {
         this.driver.current(cx, y, cz);
         if (this.driver.getBlock() == baseBlock) {
            if (y < waterLevel) {
               this.driver.block(gravelBlock);
            } else if (cnt == 0) {
               this.driver.block(topBlock);
            } else {
               if (cnt >= 3) {
                  break;
               }

               this.driver.block(fillerBlock);
            }

            ++cnt;
         }
      }

   }
}
