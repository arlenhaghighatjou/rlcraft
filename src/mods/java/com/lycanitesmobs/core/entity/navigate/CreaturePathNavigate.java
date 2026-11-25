package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class CreaturePathNavigate extends PathNavigate {
   public BaseCreatureEntity entityCreature;
   protected BlockPos targetPosition;

   public CreaturePathNavigate(BaseCreatureEntity entityCreature, World world) {
      super(entityCreature, world);
      this.entityCreature = entityCreature;
   }

   protected PathFinder getPathFinder() {
      this.nodeProcessor = new CreatureNodeProcessor();
      this.nodeProcessor.setCanEnterDoors(true);
      return new PathFinder(this.nodeProcessor);
   }

   protected boolean canNavigate() {
      if (this.entityCreature.isFlying()) {
         return true;
      } else if (this.entityCreature.isInWater()) {
         return this.entityCreature.canWade() || this.entityCreature.isStrongSwimmer();
      } else {
         return this.entity.onGround || this.entity.isRiding();
      }
   }

   public void setCanOpenDoors(boolean setBreakDoors) {
      this.nodeProcessor.setCanOpenDoors(setBreakDoors);
   }

   public boolean getCanOpenDoors() {
      return this.nodeProcessor.getCanOpenDoors();
   }

   public void setEnterDoors(boolean setBreakDoors) {
      this.nodeProcessor.setCanEnterDoors(setBreakDoors);
   }

   public boolean getEnterDoors() {
      return this.nodeProcessor.getCanEnterDoors();
   }

   public Path getPathToPos(BlockPos pos) {
      this.targetPosition = this.getSuitableDestination(pos);
      Path path = super.getPathToPos(this.targetPosition);
      return path;
   }

   public Path getPathToEntityLiving(Entity entity) {
      this.targetPosition = new BlockPos(entity);
      Path path = super.getPathToEntityLiving(entity);
      return path;
   }

   protected BlockPos getSuitableDestination(BlockPos pos) {
      IBlockState targetBlockState = this.world.getBlockState(pos);
      if (targetBlockState.getMaterial() == Material.AIR) {
         return this.entityCreature.isFlying() ? pos : this.getGround(pos);
      } else {
         return !targetBlockState.getMaterial().isSolid() ? pos : this.getSurface(pos);
      }
   }

   protected Vec3d getEntityPosition() {
      return new Vec3d(this.entity.posX, (double)this.getPathablePosY(), this.entity.posZ);
   }

   protected int getPathablePosY() {
      if (this.entityCreature.isInWater() && this.entityCreature.canFloat() && !this.entityCreature.canDive()) {
         int posY = (int)this.entity.getEntityBoundingBox().minY;
         Block block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.posX), posY, MathHelper.floor(this.entity.posZ))).getBlock();
         int searchCount = 0;

         while(this.isSwimmableBlock(block)) {
            ++posY;
            block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.posX), posY, MathHelper.floor(this.entity.posZ))).getBlock();
            ++searchCount;
            if (searchCount > 16) {
               return (int)this.entity.getEntityBoundingBox().minY;
            }
         }

         return posY;
      } else {
         return (int)(this.entity.getEntityBoundingBox().minY + 0.5D);
      }
   }

   public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
      Path path = this.getPathToEntityLiving(entityIn);
      if (path != null) {
         return this.setPath(path, speedIn);
      } else if (this.entityCreature.canClimb()) {
         this.targetPosition = new BlockPos(entityIn);
         this.speed = speedIn;
         return true;
      } else {
         return false;
      }
   }

   protected boolean isSwimmableBlock(Block block) {
      return this.isSwimmableBlock(block, 0);
   }

   protected boolean isSwimmableBlock(Block block, int cachedCheck) {
      if (block != null && block != Blocks.AIR) {
         if (cachedCheck != 1 && !this.isWaterBlock(block)) {
            if (cachedCheck != 2 && !this.isLavaBlock(block)) {
               if (cachedCheck != 3 && !this.isOozeBlock(block)) {
                  return false;
               } else {
                  return !this.entityCreature.canFreeze();
               }
            } else {
               return !this.entityCreature.canBurn();
            }
         } else {
            return !this.entityCreature.waterDamage();
         }
      } else {
         return false;
      }
   }

   protected boolean isWaterBlock(Block block) {
      return block == Blocks.FLOWING_WATER || block == Blocks.WATER;
   }

   protected boolean isLavaBlock(Block block) {
      return block == Blocks.FLOWING_LAVA || block == Blocks.LAVA || block == ObjectManager.getBlock("purelava");
   }

   protected boolean isOozeBlock(Block block) {
      return block == ObjectManager.getBlock("ooze");
   }

   public boolean canEntityStandOnPos(BlockPos pos) {
      if (this.entityCreature.isFlying() || this.entityCreature.isInWater() && this.entityCreature.isStrongSwimmer()) {
         IBlockState blockState = this.world.getBlockState(pos);
         if (blockState.getMaterial().isLiquid()) {
            return this.entityCreature.isStrongSwimmer();
         } else {
            return !blockState.getMaterial().isSolid();
         }
      } else {
         return super.canEntityStandOnPos(pos);
      }
   }

   public BlockPos getGround(BlockPos pos) {
      BlockPos resultPos;
      for(resultPos = pos.down(); resultPos.getY() > 0 && !this.world.getBlockState(resultPos).getMaterial().isSolid(); resultPos = resultPos.down()) {
         ;
      }

      resultPos = resultPos.up();
      return resultPos.getY() == 0 && !this.world.getBlockState(resultPos).isTopSolid() ? pos : resultPos;
   }

   public BlockPos getSurface(BlockPos pos) {
      BlockPos resultPos;
      for(resultPos = pos.up(); resultPos.getY() < this.world.getHeight() && this.world.getBlockState(resultPos).getMaterial().isSolid(); resultPos = resultPos.up()) {
         ;
      }

      return resultPos.getY() == this.world.getHeight() && this.world.getBlockState(resultPos).getMaterial().isSolid() ? pos : resultPos;
   }

   protected boolean isDirectPathBetweenPoints(Vec3d startVec, Vec3d endVec, int sizeX, int sizeY, int sizeZ) {
      if (!this.entityCreature.isFlying() && !this.entityCreature.isInWater()) {
         int i = MathHelper.floor(startVec.x);
         int j = MathHelper.floor(startVec.z);
         double distanceX = endVec.x - startVec.x;
         double distanceY = endVec.z - startVec.z;
         double distance = distanceX * distanceX + distanceY * distanceY;
         if (distance < 1.0E-8D) {
            return false;
         } else {
            double d3 = 1.0D / Math.sqrt(distance);
            distanceX = distanceX * d3;
            distanceY = distanceY * d3;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;
            if (!this.isSafeToStandAt(i, (int)startVec.y, j, sizeX, sizeY, sizeZ, startVec, distanceX, distanceY)) {
               return false;
            } else {
               sizeX = sizeX - 2;
               sizeZ = sizeZ - 2;
               double d4 = 1.0D / Math.abs(distanceX);
               double d5 = 1.0D / Math.abs(distanceY);
               double d6 = (double)i - startVec.x;
               double d7 = (double)j - startVec.z;
               if (distanceX >= 0.0D) {
                  ++d6;
               }

               if (distanceY >= 0.0D) {
                  ++d7;
               }

               d6 = d6 / distanceX;
               d7 = d7 / distanceY;
               int k = distanceX < 0.0D ? -1 : 1;
               int l = distanceY < 0.0D ? -1 : 1;
               int i1 = MathHelper.floor(endVec.x);
               int j1 = MathHelper.floor(endVec.z);
               int k1 = i1 - i;
               int l1 = j1 - j;

               while(k1 * k > 0 || l1 * l > 0) {
                  if (d6 < d7) {
                     d6 += d4;
                     i += k;
                     k1 = i1 - i;
                  } else {
                     d7 += d5;
                     j += l;
                     l1 = j1 - j;
                  }

                  if (!this.isSafeToStandAt(i, (int)startVec.y, j, sizeX, sizeY, sizeZ, startVec, distanceX, distanceY)) {
                     return false;
                  }
               }

               return true;
            }
         }
      } else {
         RayTraceResult raytraceresult = this.world.rayTraceBlocks(startVec, new Vec3d(endVec.x, endVec.y + (double)this.entity.height * 0.5D, endVec.z), false, true, false);
         if (raytraceresult == null) {
            return true;
         } else if (raytraceresult.typeOfHit == Type.BLOCK) {
            IBlockState hitState = this.world.getBlockState(raytraceresult.getBlockPos());
            return !hitState.getMaterial().isSolid();
         } else {
            return raytraceresult.typeOfHit == Type.MISS;
         }
      }
   }

   protected boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d startVec, double distanceX, double distanceZ) {
      sizeX = Math.min(sizeX, 2);
      sizeY = Math.min(sizeY, 3);
      sizeZ = Math.min(sizeZ, 2);
      int i = x - sizeX / 2;
      int j = z - sizeZ / 2;
      if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, startVec, distanceX, distanceZ)) {
         return false;
      } else {
         for(int k = i; k < i + sizeX; ++k) {
            for(int l = j; l < j + sizeZ; ++l) {
               double d0 = (double)k + 0.5D - startVec.x;
               double d1 = (double)l + 0.5D - startVec.z;
               if (d0 * distanceX + d1 * distanceZ >= 0.0D) {
                  PathNodeType pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y - 1, l, this.entity, sizeX, sizeY, sizeZ, true, true);
                  if (pathnodetype == PathNodeType.WATER) {
                     return false;
                  }

                  if (pathnodetype == PathNodeType.LAVA) {
                     return false;
                  }

                  if (pathnodetype == PathNodeType.OPEN) {
                     return false;
                  }

                  pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y, l, this.entity, sizeX, sizeY, sizeZ, true, true);
                  float f = this.entity.getPathPriority(pathnodetype);
                  if (f < 0.0F || f >= 8.0F) {
                     return false;
                  }

                  if (this.entityCreature.canBurn() && (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER)) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d startVec, double distanceX, double distanceZ) {
      for(BlockPos blockpos : BlockPos.getAllInBox(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
         double d0 = (double)blockpos.getX() + 0.5D - startVec.x;
         double d1 = (double)blockpos.getZ() + 0.5D - startVec.z;
         if (d0 * distanceX + d1 * distanceZ >= 0.0D) {
            Block block = this.world.getBlockState(blockpos).getBlock();
            if (!block.isPassable(this.world, blockpos)) {
               return false;
            }
         }
      }

      return true;
   }

   protected void removeSunnyPath() {
      super.removeSunnyPath();
      if (this.entityCreature.getEntityWorld().isDaytime()) {
         if (this.entityCreature.daylightBurns()) {
            if (this.world.canSeeSky(this.entity.getPosition())) {
               return;
            }

            for(int j = 0; j < this.currentPath.getCurrentPathLength(); ++j) {
               PathPoint pathpoint2 = this.currentPath.getPathPointFromIndex(j);
               BlockPos pathPos = new BlockPos(pathpoint2.x, pathpoint2.y, pathpoint2.z);
               if (this.world.getBlockState(pathPos).getBlock() != Blocks.WATER && this.world.getBlockState(pathPos.down()).getBlock() != Blocks.WATER && this.world.canSeeSky(pathPos)) {
                  this.currentPath.setCurrentPathLength(j - 1);
                  return;
               }
            }
         }

      }
   }

   protected void pathFollow() {
      if (!this.entityCreature.isFlying() && !this.entityCreature.isInWater()) {
         super.pathFollow();
      } else {
         Vec3d entityVector = this.getEntityPosition();
         float entitySize = this.entity.width * this.entity.width;
         if (entityVector.squareDistanceTo(this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex())) < (double)entitySize) {
            this.currentPath.incrementPathIndex();
         }

         int pathIndexRange = 6;

         for(int pathIndex = Math.min(this.currentPath.getCurrentPathIndex() + pathIndexRange, this.currentPath.getCurrentPathLength() - 1); pathIndex > this.currentPath.getCurrentPathIndex(); --pathIndex) {
            Vec3d pathVector = this.currentPath.getVectorFromIndex(this.entity, pathIndex);
            if (pathVector.squareDistanceTo(entityVector) <= 36.0D && this.isDirectPathBetweenPoints(entityVector, pathVector, 0, 0, 0)) {
               this.currentPath.setCurrentPathIndex(pathIndex);
               break;
            }
         }

         this.checkForStuck(entityVector);
      }
   }

   public void onUpdateNavigation() {
      if (this.noPath() && this.entityCreature.canClimb()) {
         if (this.targetPosition != null) {
            double d0 = (double)(this.entity.width * this.entity.width);
            if (this.entity.getDistanceSqToCenter(this.targetPosition) < d0 || this.entity.posY > (double)this.targetPosition.getY() && this.entity.getDistanceSqToCenter(new BlockPos(this.targetPosition.getX(), MathHelper.floor(this.entity.posY), this.targetPosition.getZ())) < d0) {
               this.targetPosition = null;
            } else {
               this.entity.getMoveHelper().setMoveTo((double)this.targetPosition.getX(), (double)this.targetPosition.getY(), (double)this.targetPosition.getZ(), this.speed);
            }
         }
      } else {
         super.onUpdateNavigation();
      }

   }
}
