package com.lycanitesmobs.core.dungeon.instance;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class SectorConnector {
   public BlockPos position;
   public SectorInstance parentSector;
   public SectorInstance childSector;
   public EnumFacing facing = EnumFacing.SOUTH;
   public boolean closed = false;
   public int level = 0;

   public SectorConnector(BlockPos position, SectorInstance parentSector, int level, EnumFacing facing) {
      this.position = position;
      this.parentSector = parentSector;
      this.level = level;
      this.facing = facing;
   }

   public boolean canConnect(DungeonLayout dungeonLayout, SectorInstance sectorInstance) {
      if (this.closed) {
         return false;
      } else if (sectorInstance == null) {
         return this.facing != EnumFacing.UP;
      } else if ("tower".equalsIgnoreCase(sectorInstance.dungeonSector.type)) {
         return this.facing == EnumFacing.UP;
      } else if (this.facing == EnumFacing.UP) {
         return false;
      } else {
         boolean tempConnector = false;
         if (sectorInstance.parentConnector == null) {
            sectorInstance.parentConnector = this;
            tempConnector = true;
         }

         for(SectorInstance nearbySector : sectorInstance.getNearbySectors()) {
            if (sectorInstance.collidesWith(nearbySector)) {
               if (tempConnector) {
                  sectorInstance.parentConnector = null;
               }

               return false;
            }
         }

         if (tempConnector) {
            sectorInstance.parentConnector = null;
         }

         return true;
      }
   }

   public void buildEntrance(World world, ChunkPos chunkPos, Random random) {
      SectorInstance sectorInstance = this.parentSector;
      if (sectorInstance == null) {
         sectorInstance = this.childSector;
      }

      if (sectorInstance != null) {
         Vec3i size = sectorInstance.getRoomSize();
         if (this.childSector != null && sectorInstance != this.childSector) {
            Vec3i childSize = this.childSector.getRoomSize();
            size = new Vec3i(Math.min(size.getX(), childSize.getX()), Math.min(size.getY(), childSize.getY()), Math.min(size.getZ(), childSize.getZ()));
         }

         int entranceHeight = 2;
         int entranceRadius = 1;
         int startX = this.position.getX();
         int stopX = this.position.getX();
         int startY = Math.max(1, this.position.getY() + 1);
         int stopY = Math.min(world.getHeight() - 1, this.position.getY() + 1 + entranceHeight);
         int startZ = this.position.getZ();
         int stopZ = this.position.getZ();
         if (this.facing.getAxis() == Axis.Z) {
            startX -= entranceRadius;
            stopX += entranceRadius;
            if (size.getX() % 2 != 0) {
               --startX;
            }

            if (this.facing == EnumFacing.SOUTH) {
               startZ -= 2;
               ++stopZ;
            } else {
               --startZ;
               stopZ += 2;
            }
         } else {
            startZ -= entranceRadius;
            stopZ += entranceRadius;
            if (size.getZ() % 2 != 0) {
               --startZ;
            }

            if (this.facing == EnumFacing.EAST) {
               startX -= 2;
               ++stopX;
            } else {
               --startX;
               stopX += 2;
            }
         }

         MutableBlockPos buildPos = new MutableBlockPos();

         for(int x = startX; x <= stopX; ++x) {
            for(int y = startY; y <= stopY; ++y) {
               for(int z = startZ; z <= stopZ; ++z) {
                  buildPos.setPos(x, y, z);
                  if (world.getTileEntity(buildPos) == null) {
                     IBlockState blockState = sectorInstance.getAirBlockForPos(buildPos);
                     if (blockState != null) {
                        sectorInstance.placeBlock(world, chunkPos, buildPos, blockState, this.facing, random);
                     }
                  }
               }
            }
         }

      }
   }

   public void buildTest(World world, ChunkPos chunkPos, Random random) {
      SectorInstance sectorInstance = this.parentSector;
      if (sectorInstance == null) {
         sectorInstance = this.childSector;
      }

      if (sectorInstance != null) {
         sectorInstance.placeBlock(world, chunkPos, this.position.add(0, 1, 0), Blocks.GOLD_BLOCK.getDefaultState(), EnumFacing.SOUTH, random);
         if (this.facing == EnumFacing.SOUTH) {
            for(int z = 1; z <= 3; ++z) {
               sectorInstance.placeBlock(world, chunkPos, this.position.add(0, 1, z), Blocks.REDSTONE_BLOCK.getDefaultState(), EnumFacing.SOUTH, random);
            }
         } else if (this.facing == EnumFacing.EAST) {
            for(int x = 1; x <= 3; ++x) {
               sectorInstance.placeBlock(world, chunkPos, this.position.add(x, 1, 0), Blocks.REDSTONE_BLOCK.getDefaultState(), EnumFacing.SOUTH, random);
            }
         } else if (this.facing == EnumFacing.NORTH) {
            for(int z = -1; z >= -3; --z) {
               sectorInstance.placeBlock(world, chunkPos, this.position.add(0, 1, z), Blocks.REDSTONE_BLOCK.getDefaultState(), EnumFacing.SOUTH, random);
            }
         } else {
            for(int x = -1; x >= -3; --x) {
               sectorInstance.placeBlock(world, chunkPos, this.position.add(x, 1, 0), Blocks.REDSTONE_BLOCK.getDefaultState(), EnumFacing.SOUTH, random);
            }
         }

      }
   }
}
