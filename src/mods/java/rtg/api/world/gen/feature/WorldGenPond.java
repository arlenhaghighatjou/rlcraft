package rtg.api.world.gen.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenPond extends WorldGenerator {
   private IBlockState fill;

   public WorldGenPond(IBlockState fill) {
      this.fill = fill;
   }

   public boolean generate(World world, EaglercraftRandom rand, BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      x = x - 8;

      for(z = z - 8; y > 5 && world.isAirBlock(new BlockPos(x, y, z)); --y) {
         ;
      }

      if (y <= 4) {
         return false;
      } else {
         y = y - 4;
         boolean[] aboolean = new boolean[2048];
         int l = rand.nextInt(4) + 4;

         for(int i1 = 0; i1 < l; ++i1) {
            double d0 = rand.nextDouble() * 6.0D + 3.0D;
            double d1 = rand.nextDouble() * 4.0D + 2.0D;
            double d2 = rand.nextDouble() * 6.0D + 3.0D;
            double d3 = rand.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
            double d4 = rand.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
            double d5 = rand.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

            for(int k1 = 1; k1 < 15; ++k1) {
               for(int l1 = 1; l1 < 15; ++l1) {
                  for(int i2 = 1; i2 < 7; ++i2) {
                     double d6 = ((double)k1 - d3) / (d0 / 2.0D);
                     double d7 = ((double)i2 - d4) / (d1 / 2.0D);
                     double d8 = ((double)l1 - d5) / (d2 / 2.0D);
                     double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                     if (d9 < 1.0D) {
                        aboolean[(k1 * 16 + l1) * 8 + i2] = true;
                     }
                  }
               }
            }
         }

         for(int j1 = 0; j1 < 8; ++j1) {
            for(int j2 = 0; j2 < 16; ++j2) {
               int var37 = 0;
               aboolean[(var37 * 16 + j2) * 8 + j1] = false;
               var37 = 15;
               aboolean[(var37 * 16 + j2) * 8 + j1] = false;
            }

            for(int var39 = 0; var39 < 16; ++var39) {
               int var57 = 0;
               aboolean[(var39 * 16 + var57) * 8 + j1] = false;
               var57 = 15;
               aboolean[(var39 * 16 + var57) * 8 + j1] = false;
            }
         }

         boolean[] willBePond = new boolean[256];

         for(int var40 = 1; var40 < 15; ++var40) {
            for(int j2 = 1; j2 < 15; ++j2) {
               for(int var52 = 0; var52 < 4; ++var52) {
                  if (aboolean[(var40 * 16 + j2) * 8 + var52]) {
                     willBePond[var40 * 16 + j2] = true;
                  }
               }
            }
         }

         boolean[] willBeShore = new boolean[256];

         for(int var41 = 1; var41 < 14; ++var41) {
            for(int j2 = 1; j2 < 14; ++j2) {
               if (!willBePond[var41 * 16 + j2]) {
                  if (j2 < 15 && willBePond[var41 * 16 + j2 + 1]) {
                     willBeShore[var41 * 16 + j2] = true;
                  }

                  if (j2 > 0 && willBePond[var41 * 16 + j2 - 1]) {
                     willBeShore[var41 * 16 + j2] = true;
                  }

                  if (var41 < 15 && willBePond[(var41 + 1) * 16 + j2]) {
                     willBeShore[var41 * 16 + j2] = true;
                  }

                  if (var41 > 0 && willBePond[(var41 - 1) * 16 + j2]) {
                     willBeShore[var41 * 16 + j2] = true;
                  }
               }
            }
         }

         int[] heightCounts = new int[257];
         int shoreBlockCount = 0;

         for(int var42 = 1; var42 < 15; ++var42) {
            for(int j2 = 1; j2 < 15; ++j2) {
               if (willBeShore[var42 * 16 + j2]) {
                  int topHeight = world.getTopSolidOrLiquidBlock(new BlockPos(x + var42, 0, z + j2)).getY();
                  if (topHeight < 1 || topHeight > 255) {
                     return false;
                  }

                  ++heightCounts[topHeight];
                  ++shoreBlockCount;
               }
            }
         }

         int shoreSoFar = 0;
         int lakeLevel = 0;
         int bottomBlock = 257;

         for(int height = 0; height < 257; ++height) {
            shoreSoFar += heightCounts[height];
            if (heightCounts[height] > 0 && bottomBlock > height) {
               bottomBlock = height;
            }

            if (shoreSoFar * 2 >= shoreBlockCount) {
               lakeLevel = height;
               if (height > bottomBlock) {
                  lakeLevel = bottomBlock;
               }
            }
         }

         y = lakeLevel - 4;

         for(int var43 = 0; var43 < 16; ++var43) {
            for(int j2 = 0; j2 < 16; ++j2) {
               if (willBePond[var43 * 16 + j2]) {
                  int top = world.getTopSolidOrLiquidBlock(new BlockPos(x + var43, 0, z + j2)).getY();
                  if (top > lakeLevel + 4) {
                     return false;
                  }
               }
            }
         }

         for(int var44 = 1; var44 < 15; ++var44) {
            for(int j2 = 1; j2 < 15; ++j2) {
               if (willBeShore[var44 * 16 + j2]) {
                  int shoreHeight = world.getTopSolidOrLiquidBlock(new BlockPos(x + var44, 0, z + j2)).getY();
                  if (shoreHeight < lakeLevel) {
                     Biome biomegenbase = world.getBiome(new BlockPos(x + var44, 0, z + j2));

                     for(int height = shoreHeight; height < lakeLevel; ++height) {
                        if (biomegenbase.topBlock == Blocks.MYCELIUM.getDefaultState()) {
                           world.setBlockState(new BlockPos(x + var44, height, z + j2), Blocks.MYCELIUM.getDefaultState(), 2);
                        } else {
                           world.setBlockState(new BlockPos(x + var44, height, z + j2), Blocks.GRASS.getDefaultState(), 2);
                        }
                     }
                  }
               }
            }
         }

         for(int var45 = 0; var45 < 16; ++var45) {
            for(int j2 = 0; j2 < 16; ++j2) {
               for(int var53 = 0; var53 < 8; ++var53) {
                  boolean flag = !aboolean[(var45 * 16 + j2) * 8 + var53] && (var45 < 15 && aboolean[((var45 + 1) * 16 + j2) * 8 + var53] || var45 > 0 && aboolean[((var45 - 1) * 16 + j2) * 8 + var53] || j2 < 15 && aboolean[(var45 * 16 + j2 + 1) * 8 + var53] || j2 > 0 && aboolean[(var45 * 16 + (j2 - 1)) * 8 + var53] || var53 < 7 && aboolean[(var45 * 16 + j2) * 8 + var53 + 1] || var53 > 0 && aboolean[(var45 * 16 + j2) * 8 + (var53 - 1)]);
                  if (flag) {
                     Material material = world.getBlockState(new BlockPos(x + var45, y + var53, z + j2)).getMaterial();
                     if (var53 >= 4 && material.isLiquid()) {
                        return false;
                     }

                     if (var53 < 4 && !material.isSolid() && world.getBlockState(new BlockPos(x + var45, y + var53, z + j2)) != this.fill) {
                        return false;
                     }
                  }
               }
            }
         }

         for(int var46 = 0; var46 < 16; ++var46) {
            for(int j2 = 0; j2 < 16; ++j2) {
               for(int var54 = 0; var54 < 8; ++var54) {
                  if (var54 < 4) {
                     if (aboolean[(var46 * 16 + j2) * 8 + var54]) {
                        world.setBlockState(new BlockPos(x + var46, y + var54, z + j2), this.fill, 2);
                     }
                  } else if (willBePond[var46 * 16 + j2]) {
                     world.setBlockState(new BlockPos(x + var46, y + var54, z + j2), Blocks.AIR.getDefaultState(), 2);
                  }
               }
            }
         }

         for(int var47 = 0; var47 < 16; ++var47) {
            for(int j2 = 0; j2 < 16; ++j2) {
               for(int var55 = 4; var55 < 8; ++var55) {
                  if (aboolean[(var47 * 16 + j2) * 8 + var55] && world.getBlockState(new BlockPos(x + var47, y + var55 - 1, z + j2)) == Blocks.DIRT.getDefaultState() && world.getLightFor(EnumSkyBlock.SKY, new BlockPos(x + var47, y + var55, z + j2)) > 0) {
                     Biome biomegenbase = world.getBiome(new BlockPos(x + var47, 0, z + j2));
                     if (biomegenbase.topBlock == Blocks.MYCELIUM.getDefaultState()) {
                        world.setBlockState(new BlockPos(x + var47, y + var55 - 1, z + j2), Blocks.MYCELIUM.getDefaultState(), 2);
                     } else {
                        world.setBlockState(new BlockPos(x + var47, y + var55 - 1, z + j2), biomegenbase.topBlock, 2);
                     }
                  }
               }
            }
         }

         if (this.fill.getMaterial() == Material.LAVA) {
            for(int var48 = 0; var48 < 16; ++var48) {
               for(int j2 = 0; j2 < 16; ++j2) {
                  for(int var56 = 0; var56 < 8; ++var56) {
                     boolean flag = !aboolean[(var48 * 16 + j2) * 8 + var56] && (var48 < 15 && aboolean[((var48 + 1) * 16 + j2) * 8 + var56] || var48 > 0 && aboolean[((var48 - 1) * 16 + j2) * 8 + var56] || j2 < 15 && aboolean[(var48 * 16 + j2 + 1) * 8 + var56] || j2 > 0 && aboolean[(var48 * 16 + (j2 - 1)) * 8 + var56] || var56 < 7 && aboolean[(var48 * 16 + j2) * 8 + var56 + 1] || var56 > 0 && aboolean[(var48 * 16 + j2) * 8 + (var56 - 1)]);
                     if (flag && (var56 < 4 || rand.nextInt(2) != 0) && world.getBlockState(new BlockPos(x + var48, y + var56, z + j2)).getMaterial().isSolid()) {
                        world.setBlockState(new BlockPos(x + var48, y + var56, z + j2), Blocks.STONE.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

         if (this.fill.getMaterial() == Material.WATER) {
            for(int var49 = 0; var49 < 16; ++var49) {
               for(int j2 = 0; j2 < 16; ++j2) {
                  byte b0 = 4;
                  if (world.canBlockFreezeWater(new BlockPos(x + var49, y + b0, z + j2))) {
                     world.setBlockState(new BlockPos(x + var49, y + b0, z + j2), Blocks.ICE.getDefaultState(), 2);
                  }
               }
            }
         }

         for(int var50 = 1; var50 < 15; ++var50) {
            for(int j2 = 1; j2 < 15; ++j2) {
               if (willBeShore[var50 * 16 + j2]) {
                  int shoreHeight = world.getTopSolidOrLiquidBlock(new BlockPos(x + var50, 0, z + j2)).getY();
                  if (shoreHeight > lakeLevel) {
                     --shoreHeight;
                     Biome biomegenbase = world.getBiome(new BlockPos(x + var50, 0, z + j2));
                     world.setBlockState(new BlockPos(x + var50, shoreHeight, z + j2), Blocks.AIR.getDefaultState(), 2);
                     --shoreHeight;
                     if (biomegenbase.topBlock == Blocks.MYCELIUM.getDefaultState()) {
                        world.setBlockState(new BlockPos(x + var50, shoreHeight, z + j2), Blocks.MYCELIUM.getDefaultState(), 2);
                     } else {
                        world.setBlockState(new BlockPos(x + var50, shoreHeight, z + j2), biomegenbase.topBlock, 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
