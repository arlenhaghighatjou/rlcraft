package mcjty.lostcities.dimensions.world;

import mcjty.lostcities.config.LandscapeType;
import mcjty.lostcities.dimensions.world.driver.IPrimerDriver;
import mcjty.lostcities.dimensions.world.terraingen.LostCitiesTerrainGenerator;

public class ChunkHeightmap {
   private byte[] heightmap = new byte[256];

   public ChunkHeightmap(IPrimerDriver driver, LandscapeType type, int groundLevel, char baseChar) {
      char air = LostCitiesTerrainGenerator.airChar;
      if (type == LandscapeType.CAVERN) {
         int base = Math.max(groundLevel - 20, 1);

         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               int y = base;
               driver.current(x, base, z);

               while(y < 100 && driver.getBlock() != air) {
                  ++y;
                  driver.incY();
               }

               if (y >= 100) {
                  y = 128;
               } else {
                  while(y > 0 && driver.getBlock() == air) {
                     --y;
                     driver.decY();
                  }
               }

               this.heightmap[z * 16 + x] = (byte)y;
            }
         }
      } else if (type == LandscapeType.SPACE) {
         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               int y = 255;
               driver.current(x, y, z);

               while(y > 0 && driver.getBlock() != baseChar) {
                  --y;
                  driver.decY();
               }

               this.heightmap[z * 16 + x] = (byte)y;
            }
         }
      } else {
         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               int y = 255;
               driver.current(x, y, z);

               while(y > 0 && driver.getBlock() == air) {
                  --y;
                  driver.decY();
               }

               this.heightmap[z * 16 + x] = (byte)y;
            }
         }
      }

   }

   public int getHeight(int x, int z) {
      return this.heightmap[z * 16 + x] & 255;
   }

   public int getAverageHeight() {
      int cnt = 0;
      int y = 0;
      int yy = this.getHeight(2, 2);
      if (yy > 5) {
         y += yy;
         ++cnt;
      }

      yy = this.getHeight(13, 2);
      if (yy > 5) {
         y += yy;
         ++cnt;
      }

      yy = this.getHeight(2, 13);
      if (yy > 5) {
         y += yy;
         ++cnt;
      }

      yy = this.getHeight(13, 13);
      if (yy > 5) {
         y += yy;
         ++cnt;
      }

      yy = this.getHeight(8, 8);
      if (yy > 5) {
         y += yy;
         ++cnt;
      }

      return cnt > 0 ? y / cnt : 0;
   }

   public int getMinimumHeight() {
      int y = 255;
      int yy = this.getHeight(2, 2);
      if (yy < y) {
         y = yy;
      }

      yy = this.getHeight(13, 2);
      if (yy < y) {
         y = yy;
      }

      yy = this.getHeight(2, 13);
      if (yy < y) {
         y = yy;
      }

      yy = this.getHeight(13, 13);
      if (yy < y) {
         y = yy;
      }

      yy = this.getHeight(8, 8);
      if (yy < y) {
         y = yy;
      }

      return y;
   }
}
