package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RandomSpawnLocation extends BlockSpawnLocation {
   protected int limit = 32;
   protected boolean solidGround = false;
   protected double easyDifficultyRangeScale = 1.5D;
   protected double normalDifficultyRangeScale = 1.0D;
   protected double hardDifficultyRangeScale = 0.5D;

   public RandomSpawnLocation() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("limit")) {
         this.limit = json.get("limit").getAsInt();
      }

      if (json.has("solidGround")) {
         this.solidGround = json.get("solidGround").getAsBoolean();
      }

      if (json.has("easyDifficultyRangeScale")) {
         this.easyDifficultyRangeScale = json.get("easyDifficultyRangeScale").getAsDouble();
      }

      if (json.has("normalDifficultyRangeScale")) {
         this.normalDifficultyRangeScale = json.get("normalDifficultyRangeScale").getAsDouble();
      }

      if (json.has("hardDifficultyRangeScale")) {
         this.hardDifficultyRangeScale = json.get("hardDifficultyRangeScale").getAsDouble();
      }

      this.listType = "whitelist";
      this.blockIds.add(Blocks.AIR.getRegistryName().toString());
      this.blockIds.add(Blocks.TALLGRASS.getRegistryName().toString());
      super.loadFromJSON(json);
   }

   public List<BlockPos> getSpawnPositions(World world, EntityPlayer player, BlockPos triggerPos) {
      List<BlockPos> spawnPositions = new ArrayList();
      LycanitesMobs.logDebug("JSONSpawner", "Getting " + this.limit + " Random Spawn Positions");

      for(int i = 0; i < this.limit; ++i) {
         BlockPos randomPos = this.getRandomPosition(world, player, triggerPos);
         if (randomPos != null) {
            spawnPositions.add(randomPos);
         }
      }

      return this.sortSpawnPositions(spawnPositions, world, triggerPos);
   }

   public BlockPos getRandomPosition(World world, EntityPlayer player, BlockPos triggerPos) {
      int[] xz = this.getRandomXZCoord(world, triggerPos);
      int x = xz[0];
      int z = xz[1];
      int y = this.getRandomYCoord(world, new BlockPos(x, triggerPos.getY(), z));
      return y > -1 ? new BlockPos(x, y, z) : null;
   }

   public int[] getRandomXZCoord(World world, BlockPos triggerPos) {
      double difficultyScale = this.normalDifficultyRangeScale;
      if (world.getDifficulty().getDifficultyId() <= 1) {
         difficultyScale = this.easyDifficultyRangeScale;
      } else if (world.getDifficulty().getDifficultyId() >= 3) {
         difficultyScale = this.hardDifficultyRangeScale;
      }

      int xPos = 0;
      int rangeMaxX = Math.round((float)this.rangeMax.getX() * (float)difficultyScale);
      int rangeMinX = Math.round((float)this.rangeMin.getX() * (float)difficultyScale);
      if ((double)rangeMaxX * difficultyScale > 0.0D) {
         xPos = world.rand.nextInt(rangeMaxX);
         if (world.rand.nextBoolean()) {
            xPos = xPos + rangeMinX;
         } else {
            xPos = -xPos - rangeMinX;
         }
      }

      int zPos = 0;
      int rangeMaxZ = Math.round((float)this.rangeMax.getZ() * (float)difficultyScale);
      int rangeMinZ = Math.round((float)this.rangeMin.getZ() * (float)difficultyScale);
      if ((double)rangeMaxZ * difficultyScale > 0.0D) {
         zPos = world.rand.nextInt(rangeMaxZ);
         if (world.rand.nextBoolean()) {
            zPos = zPos + rangeMinZ;
         } else {
            zPos = -zPos - rangeMinZ;
         }
      }

      return new int[]{triggerPos.getX() + xPos, triggerPos.getZ() + zPos};
   }

   public int getRandomYCoord(World world, BlockPos triggerPos) {
      double difficultyScale = this.normalDifficultyRangeScale;
      if (world.getDifficulty().getDifficultyId() <= 1) {
         difficultyScale = this.easyDifficultyRangeScale;
      } else if (world.getDifficulty().getDifficultyId() >= 3) {
         difficultyScale = this.hardDifficultyRangeScale;
      }

      int rangeMaxY = Math.round((float)this.rangeMax.getY() * (float)difficultyScale);
      int rangeMinY = Math.round((float)this.rangeMin.getY() * (float)difficultyScale);
      int originX = triggerPos.getX();
      int originY = triggerPos.getY();
      int originZ = triggerPos.getZ();
      int minY = Math.max(originY - rangeMaxY, 0);
      if (this.yMin >= 0) {
         minY = Math.max(minY, this.yMin);
      }

      int maxY = Math.min(originY + rangeMaxY, world.getHeight() - 1);
      if (this.yMax >= 0) {
         maxY = Math.min(maxY, this.yMax);
      }

      List<Integer> yCoordsLow = new ArrayList();
      List<Integer> yCoordsHigh = new ArrayList();

      for(int nextY = minY; nextY <= maxY; ++nextY) {
         if (nextY > originY - rangeMinY && nextY < originY + rangeMinY) {
            nextY = originY + rangeMinY;
         }

         BlockPos spawnPos = new BlockPos(originX, nextY, originZ);
         if (this.isValidBlock(world, spawnPos)) {
            boolean lastYPos = false;
            if (world.canBlockSeeSky(spawnPos)) {
               if (!this.solidGround) {
                  int floatRange = maxY - nextY;
                  if (floatRange > 1) {
                     if (world.getBlockState(spawnPos).getBlock() != Blocks.AIR) {
                        floatRange = this.getValidBlockHeight(world, spawnPos, maxY);
                     }

                     nextY += world.rand.nextInt(floatRange + 1) - 1;
                  }
               }

               lastYPos = true;
            }

            if (super.isValidBlock(world, spawnPos.up())) {
               if (nextY <= 64) {
                  yCoordsLow.add(Integer.valueOf(nextY));
               } else {
                  yCoordsHigh.add(Integer.valueOf(nextY));
               }
            }

            if (lastYPos) {
               break;
            }
         }
      }

      int y = -1;
      if (yCoordsHigh.size() > 0 && (yCoordsLow.size() <= 0 || world.rand.nextFloat() > 0.25F)) {
         if (yCoordsHigh.size() == 1) {
            y = ((Integer)yCoordsHigh.get(0)).intValue();
         } else {
            y = ((Integer)yCoordsHigh.get(world.rand.nextInt(yCoordsHigh.size() - 1))).intValue();
         }
      } else if (yCoordsLow.size() > 0) {
         if (yCoordsLow.size() == 1) {
            y = ((Integer)yCoordsLow.get(0)).intValue();
         } else {
            y = ((Integer)yCoordsLow.get(world.rand.nextInt(yCoordsLow.size() - 1))).intValue();
         }
      }

      return y;
   }

   public boolean isValidBlock(World world, BlockPos blockPos) {
      if (!super.isValidBlock(world, blockPos)) {
         return false;
      } else {
         return this.solidGround ? this.posHasGround(world, blockPos) : true;
      }
   }

   public boolean posHasGround(World world, BlockPos pos) {
      if (pos != null && pos.getY() != 0) {
         IBlockState possibleGroundBlockState = world.getBlockState(pos.down());

         try {
            if (possibleGroundBlockState.isNormalCube()) {
               return true;
            }
         } catch (Exception var6) {
            ;
         }

         try {
            if (possibleGroundBlockState.getBlock() == Blocks.SNOW_LAYER) {
               return true;
            }

            if (possibleGroundBlockState.isSideSolid(world, pos.down(), EnumFacing.UP)) {
               return true;
            }

            if (possibleGroundBlockState.isSideSolid(world, pos.down(), EnumFacing.DOWN)) {
               return true;
            }
         } catch (Exception var5) {
            ;
         }

         return false;
      } else {
         return false;
      }
   }

   public int getValidBlockHeight(World world, BlockPos startPos, int maxY) {
      int y;
      for(y = startPos.getY(); y <= maxY; ++y) {
         BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
         if (!this.isValidBlock(world, checkPos)) {
            break;
         }
      }

      return y - startPos.getY();
   }
}
