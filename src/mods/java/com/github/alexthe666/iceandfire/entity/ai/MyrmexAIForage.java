package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MyrmexAIForage extends EntityAIBase {
   private static final int RADIUS = 8;
   private final EntityMyrmexWorker myrmex;
   private final MyrmexAIForage.BlockSorter targetSorter;
   private BlockPos targetBlock = BlockPos.ORIGIN;
   private int wanderRadius = 16;

   public MyrmexAIForage(EntityMyrmexWorker myrmex) {
      this.myrmex = myrmex;
      this.targetSorter = new MyrmexAIForage.BlockSorter();
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.myrmex.canMove() && !this.myrmex.holdingBaby() && this.myrmex.getNavigator().noPath() && this.myrmex.canSeeSky() && !this.myrmex.shouldEnterHive() && this.myrmex.keepSearching) {
         List<BlockPos> allBlocks = new ArrayList();

         for(BlockPos pos : BlockPos.getAllInBox(this.myrmex.getPosition().add(-8, -8, -8), this.myrmex.getPosition().add(8, 8, 8))) {
            if (EntityMyrmexBase.isEdibleBlock(this.myrmex.world.getBlockState(pos))) {
               allBlocks.add(pos);
               this.myrmex.keepSearching = false;
            }
         }

         if (allBlocks.isEmpty()) {
            this.myrmex.keepSearching = true;
            this.wanderRadius += 8;
            Vec3d vec = RandomPositionGenerator.findRandomTarget(this.myrmex, this.wanderRadius, 7);
            if (vec != null) {
               this.targetBlock = new BlockPos(vec);
            }

            return true;
         } else {
            allBlocks.sort(this.targetSorter);
            this.targetBlock = (BlockPos)allBlocks.get(0);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      if (!this.myrmex.keepSearching && this.targetBlock == null) {
         return false;
      } else if (this.myrmex.shouldEnterHive()) {
         this.myrmex.keepSearching = false;
         return false;
      } else {
         return this.myrmex.getNavigator().noPath();
      }
   }

   public void updateTask() {
      if (this.myrmex.keepSearching) {
         this.myrmex.getNavigator().tryMoveToXYZ((double)this.targetBlock.getX() + 0.5D, (double)this.targetBlock.getY(), (double)this.targetBlock.getZ() + 0.5D, 1.0D);
         if (this.myrmex.getDistanceSqToCenter(this.targetBlock) < 2.0D) {
            this.resetTask();
         }
      } else if (this.targetBlock != null) {
         this.myrmex.getNavigator().tryMoveToXYZ((double)this.targetBlock.getX() + 0.5D, (double)this.targetBlock.getY(), (double)this.targetBlock.getZ() + 0.5D, 1.0D);
         IBlockState block = this.myrmex.world.getBlockState(this.targetBlock);
         if (EntityMyrmexBase.isEdibleBlock(block)) {
            double distance = this.getDistance(this.targetBlock);
            if (distance <= 5.0D) {
               List<ItemStack> drops = block.getBlock().getDrops(this.myrmex.world, this.targetBlock, block, 0);
               if (!drops.isEmpty()) {
                  this.myrmex.world.destroyBlock(this.targetBlock, false);
                  ItemStack heldStack = ((ItemStack)drops.get(0)).copy();
                  heldStack.setCount(1);
                  ((ItemStack)drops.get(0)).shrink(1);
                  this.myrmex.setHeldItem(EnumHand.MAIN_HAND, heldStack);

                  for(ItemStack stack : drops) {
                     EntityItem itemEntity = new EntityItem(this.myrmex.world, (double)this.targetBlock.getX() + this.myrmex.getRNG().nextDouble(), (double)this.targetBlock.getY() + this.myrmex.getRNG().nextDouble(), (double)this.targetBlock.getZ() + this.myrmex.getRNG().nextDouble(), stack);
                     itemEntity.setDefaultPickupDelay();
                     if (!this.myrmex.world.isRemote) {
                        this.myrmex.world.spawnEntity(itemEntity);
                     }
                  }

                  this.targetBlock = null;
                  this.resetTask();
                  this.myrmex.keepSearching = false;
                  this.wanderRadius = 8;
                  return;
               }
            }
         }
      }

   }

   public void resetTask() {
      this.targetBlock = BlockPos.ORIGIN;
      this.myrmex.keepSearching = true;
   }

   private double getDistance(BlockPos pos) {
      double deltaX = this.myrmex.posX - ((double)pos.getX() + 0.5D);
      double deltaY = this.myrmex.posY + (double)this.myrmex.getEyeHeight() - ((double)pos.getY() + 0.5D);
      double deltaZ = this.myrmex.posZ - ((double)pos.getZ() + 0.5D);
      return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
   }

   public class BlockSorter implements Comparator<BlockPos> {
      public BlockSorter() {
      }

      public int compare(BlockPos pos1, BlockPos pos2) {
         double distance1 = MyrmexAIForage.this.getDistance(pos1);
         double distance2 = MyrmexAIForage.this.getDistance(pos2);
         return Double.compare(distance1, distance2);
      }
   }
}
