package com.yungnickyoung.minecraft.bettercaves.world.carver.vanilla;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ICarver;
import java.util.Arrays;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class VanillaCaveCarver extends MapGenCaves implements ICarver {
   private int bottomY;
   private int topY;
   private int density;
   private int priority;
   private int liquidAltitude;
   private IBlockState debugBlock;
   private boolean isDebugVisualizerEnabled;
   private boolean isReplaceGravel;
   private boolean isFloodedUndergroundEnabled;

   public VanillaCaveCarver(VanillaCaveCarverBuilder builder) {
      this.bottomY = builder.getBottomY();
      this.topY = builder.getTopY();
      this.density = builder.getDensity();
      this.priority = builder.getPriority();
      this.liquidAltitude = builder.getLiquidAltitude();
      this.debugBlock = builder.getDebugBlock();
      this.isDebugVisualizerEnabled = builder.isDebugVisualizerEnabled();
      this.isReplaceGravel = builder.isReplaceGravel();
      this.isFloodedUndergroundEnabled = builder.isFloodedUndergroundEnabled();
      if (this.bottomY > this.topY) {
         BetterCaves.LOGGER.warn("Warning: Min altitude for vanilla caves should not be greater than max altitude.");
         BetterCaves.LOGGER.warn("Using default values...");
         this.bottomY = 40;
         this.topY = 128;
      }

   }

   public void generate(World worldIn, int chunkX, int chunkZ, ChunkPrimer primer, boolean addRooms, IBlockState[][] liquidBlocks, boolean[][] carvingMask) {
      int chunkRadius = this.range;
      this.world = worldIn;
      this.rand.setSeed(worldIn.getSeed());
      long j = this.rand.nextLong();
      long k = this.rand.nextLong();

      for(int currChunkX = chunkX - chunkRadius; currChunkX <= chunkX + chunkRadius; ++currChunkX) {
         for(int currChunkZ = chunkZ - chunkRadius; currChunkZ <= chunkZ + chunkRadius; ++currChunkZ) {
            long j1 = (long)currChunkX * j;
            long k1 = (long)currChunkZ * k;
            this.rand.setSeed(j1 ^ k1 ^ worldIn.getSeed());
            this.recursiveGenerate(currChunkX, currChunkZ, chunkX, chunkZ, primer, addRooms, liquidBlocks, carvingMask);
         }
      }

   }

   public void generate(World worldIn, int x, int z, ChunkPrimer primer, boolean addRooms, IBlockState[][] liquidBlocks) {
      boolean[][] carvingMask = new boolean[16][16];

      for(boolean[] row : carvingMask) {
         Arrays.fill(row, true);
      }

      this.generate(worldIn, x, z, primer, addRooms, liquidBlocks, carvingMask);
   }

   protected void recursiveGenerate(int chunkX, int chunkZ, int originalChunkX, int originalChunkZ, @Nonnull ChunkPrimer primer, boolean addRooms, IBlockState[][] liquidBlocks, boolean[][] carvingMask) {
      int numAttempts = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);
      if (this.rand.nextInt(100) > this.density) {
         numAttempts = 0;
      }

      for(int i = 0; i < numAttempts; ++i) {
         double caveStartX = (double)(chunkX * 16 + this.rand.nextInt(16));
         double caveStartY = (double)(this.rand.nextInt(this.topY - this.bottomY) + this.bottomY);
         double caveStartZ = (double)(chunkZ * 16 + this.rand.nextInt(16));
         int numAddTunnelCalls = 1;
         if (addRooms && this.rand.nextInt(4) == 0) {
            this.addRoom(this.rand.nextLong(), originalChunkX, originalChunkZ, primer, caveStartX, caveStartY, caveStartZ, liquidBlocks, carvingMask);
            numAddTunnelCalls += this.rand.nextInt(4);
         }

         for(int j = 0; j < numAddTunnelCalls; ++j) {
            float yaw = this.rand.nextFloat() * 6.2831855F;
            float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float width = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
            if (addRooms && this.rand.nextInt(10) == 0) {
               width *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
            }

            this.addTunnel(this.rand.nextLong(), originalChunkX, originalChunkZ, primer, caveStartX, caveStartY, caveStartZ, width, yaw, pitch, 0, 0, 1.0D, liquidBlocks, carvingMask);
         }
      }

   }

   public int getPriority() {
      return this.priority;
   }

   public int getTopY() {
      return this.topY;
   }

   protected void addRoom(long seed, int originChunkX, int originChunkZ, ChunkPrimer primer, double caveStartX, double caveStartY, double caveStartZ, IBlockState[][] liquidBlocks, boolean[][] carvingMask) {
      this.addTunnel(seed, originChunkX, originChunkZ, primer, caveStartX, caveStartY, caveStartZ, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D, liquidBlocks, carvingMask);
   }

   protected void addTunnel(long seed, int originChunkX, int originChunkZ, ChunkPrimer primer, double caveStartX, double caveStartY, double caveStartZ, float width, float yaw, float pitch, int startCounter, int endCounter, double heightModifier, IBlockState[][] liquidBlocks, boolean[][] carvingMask) {
      Random random = new Random(seed);
      double originBlockX = (double)(originChunkX * 16 + 8);
      double originBlockZ = (double)(originChunkZ * 16 + 8);
      float yawModifier = 0.0F;
      float pitchModifier = 0.0F;
      if (endCounter <= 0) {
         int i = this.range * 16 - 16;
         endCounter = i - random.nextInt(i / 4);
      }

      boolean comesFromRoom = false;
      if (startCounter == -1) {
         startCounter = endCounter / 2;
         comesFromRoom = true;
      }

      for(int randomCounterValue = random.nextInt(endCounter / 2) + endCounter / 4; startCounter < endCounter; ++startCounter) {
         double xzOffset = 1.5D + (double)(MathHelper.sin((float)startCounter * 3.1415927F / (float)endCounter) * width);
         double yOffset = xzOffset * heightModifier;
         float pitchXZ = MathHelper.cos(pitch);
         float pitchY = MathHelper.sin(pitch);
         caveStartX += (double)(MathHelper.cos(yaw) * pitchXZ);
         caveStartY += (double)pitchY;
         caveStartZ += (double)(MathHelper.sin(yaw) * pitchXZ);
         boolean flag = random.nextInt(6) == 0;
         if (flag) {
            pitch = pitch * 0.92F;
         } else {
            pitch = pitch * 0.7F;
         }

         pitch = pitch + pitchModifier * 0.1F;
         yaw += yawModifier * 0.1F;
         pitchModifier = pitchModifier * 0.9F;
         yawModifier = yawModifier * 0.75F;
         pitchModifier = pitchModifier + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         yawModifier = yawModifier + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (!comesFromRoom && startCounter == randomCounterValue && width > 1.0F && endCounter > 0) {
            this.addTunnel(random.nextLong(), originChunkX, originChunkZ, primer, caveStartX, caveStartY, caveStartZ, random.nextFloat() * 0.5F + 0.5F, yaw - 1.5707964F, pitch / 3.0F, startCounter, endCounter, 1.0D, liquidBlocks, carvingMask);
            this.addTunnel(random.nextLong(), originChunkX, originChunkZ, primer, caveStartX, caveStartY, caveStartZ, random.nextFloat() * 0.5F + 0.5F, yaw + 1.5707964F, pitch / 3.0F, startCounter, endCounter, 1.0D, liquidBlocks, carvingMask);
            return;
         }

         if (comesFromRoom || random.nextInt(4) != 0) {
            double caveStartXOffsetFromCenter = caveStartX - originBlockX;
            double caveStartZOffsetFromCenter = caveStartZ - originBlockZ;
            double distanceToEnd = (double)(endCounter - startCounter);
            double d7 = (double)(width + 2.0F + 16.0F);
            if (caveStartXOffsetFromCenter * caveStartXOffsetFromCenter + caveStartZOffsetFromCenter * caveStartZOffsetFromCenter - distanceToEnd * distanceToEnd > d7 * d7) {
               return;
            }

            if (caveStartX >= originBlockX - 16.0D - xzOffset * 2.0D && caveStartZ >= originBlockZ - 16.0D - xzOffset * 2.0D && caveStartX <= originBlockX + 16.0D + xzOffset * 2.0D && caveStartZ <= originBlockZ + 16.0D + xzOffset * 2.0D) {
               int minX = MathHelper.floor(caveStartX - xzOffset) - originChunkX * 16 - 1;
               int minY = MathHelper.floor(caveStartY - yOffset) - 1;
               int minZ = MathHelper.floor(caveStartZ - xzOffset) - originChunkZ * 16 - 1;
               int maxX = MathHelper.floor(caveStartX + xzOffset) - originChunkX * 16 + 1;
               int maxY = MathHelper.floor(caveStartY + yOffset) + 1;
               int maxZ = MathHelper.floor(caveStartZ + xzOffset) - originChunkZ * 16 + 1;
               if (minX < 0) {
                  minX = 0;
               }

               if (maxX > 16) {
                  maxX = 16;
               }

               if (minY < 1) {
                  minY = 1;
               }

               if (maxY > 248) {
                  maxY = 248;
               }

               if (minZ < 0) {
                  minZ = 0;
               }

               if (maxZ > 16) {
                  maxZ = 16;
               }

               for(int currX = minX; currX < maxX; ++currX) {
                  double xAxisDist = ((double)(currX + originChunkX * 16) + 0.5D - caveStartX) / xzOffset;

                  for(int currZ = minZ; currZ < maxZ; ++currZ) {
                     double zAxisDist = ((double)(currZ + originChunkZ * 16) + 0.5D - caveStartZ) / xzOffset;
                     if (carvingMask[currX][currZ] && xAxisDist * xAxisDist + zAxisDist * zAxisDist < 1.0D) {
                        for(int currY = maxY; currY > minY; --currY) {
                           double yAxisDist = ((double)(currY - 1) + 0.5D - caveStartY) / yOffset;
                           if (yAxisDist > -0.7D && xAxisDist * xAxisDist + yAxisDist * yAxisDist + zAxisDist * zAxisDist < 1.0D) {
                              IBlockState liquidBlock = liquidBlocks[BetterCavesUtils.getLocal(currX)][BetterCavesUtils.getLocal(currZ)];
                              if (this.isDebugVisualizerEnabled) {
                                 CarverUtils.debugDigBlock(primer, currX, currY, currZ, this.debugBlock, true);
                              } else {
                                 this.digBlock(this.world, primer, originChunkX, originChunkZ, currX, currY, currZ, liquidBlock, this.liquidAltitude, this.isReplaceGravel);
                              }
                           } else if (this.isDebugVisualizerEnabled) {
                              CarverUtils.debugDigBlock(primer, currX, currY, currZ, this.debugBlock, false);
                           }
                        }
                     }
                  }
               }

               if (comesFromRoom) {
                  break;
               }
            }
         }
      }

   }

   private void digBlock(World world, ChunkPrimer primer, int chunkX, int chunkZ, int localX, int y, int localZ, IBlockState liquidBlockState, int liquidAltitude, boolean replaceGravel) {
      BlockPos pos = new BlockPos(chunkX * 16 + localX, y, chunkZ * 16 + localZ);
      boolean flooded = this.isFloodedUndergroundEnabled && !this.isDebugVisualizerEnabled && BiomeDictionary.hasType(world.getBiome(pos), Type.OCEAN) && y < world.getSeaLevel();
      if (!flooded || BiomeDictionary.hasType(world.getBiome(pos.east()), Type.OCEAN) && BiomeDictionary.hasType(world.getBiome(pos.north()), Type.OCEAN) && BiomeDictionary.hasType(world.getBiome(pos.west()), Type.OCEAN) && BiomeDictionary.hasType(world.getBiome(pos.south()), Type.OCEAN)) {
         IBlockState airBlockState = flooded ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
         CarverUtils.digBlock(world, primer, pos, airBlockState, liquidBlockState, liquidAltitude, replaceGravel);
      }
   }

   protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
      return false;
   }
}
