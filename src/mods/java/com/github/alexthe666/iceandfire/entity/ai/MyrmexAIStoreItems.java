package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.block.BlockMyrmexCocoon;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.MyrmexHive;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityMyrmexCocoon;
import com.github.alexthe666.iceandfire.structures.WorldGenMyrmexHive;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class MyrmexAIStoreItems extends EntityAIBase {
   private final EntityMyrmexBase myrmex;
   private final double movementSpeed;
   private Path path;
   private BlockPos nextRoom = BlockPos.ORIGIN;
   private BlockPos nextCocoon = null;

   public MyrmexAIStoreItems(EntityMyrmexBase entityIn, double movementSpeedIn) {
      this.myrmex = entityIn;
      this.movementSpeed = movementSpeedIn;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.myrmex.canMove() && (!(this.myrmex instanceof EntityMyrmexWorker) || !((EntityMyrmexWorker)this.myrmex).holdingBaby()) && (this.myrmex.shouldEnterHive() || this.myrmex.getNavigator().noPath()) && !this.myrmex.canSeeSky() && !this.myrmex.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
         MyrmexHive village = this.myrmex.getHive();
         if (village == null) {
            return false;
         } else {
            this.nextRoom = MyrmexHive.getGroundedPos(this.myrmex.world, village.getRandomRoom(WorldGenMyrmexHive.RoomType.FOOD, this.myrmex.getRNG(), this.myrmex.getPosition()));
            this.nextCocoon = this.getNearbyCocoon(this.nextRoom);
            if (this.nextCocoon != null) {
               this.path = this.myrmex.getNavigator().getPathToPos(this.nextCocoon);
               return this.path != null;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.myrmex.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && !this.myrmex.getNavigator().noPath() && this.myrmex.getDistanceSq(this.nextCocoon) > 3.0D && this.myrmex.shouldEnterHive() && this.nextCocoon != null && this.isUseableCocoon(this.nextCocoon);
   }

   public void startExecuting() {
      this.myrmex.getNavigator().setPath(this.path, this.movementSpeed);
   }

   public void updateTask() {
      if (this.nextCocoon != null && this.myrmex.getDistanceSq(this.nextCocoon) < 4.0D && !this.myrmex.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && this.isUseableCocoon(this.nextCocoon)) {
         TileEntityMyrmexCocoon cocoon = (TileEntityMyrmexCocoon)this.myrmex.world.getTileEntity(this.nextCocoon);
         ItemStack itemstack = this.myrmex.getHeldItem(EnumHand.MAIN_HAND);
         if (!itemstack.isEmpty()) {
            for(int i = 0; i < cocoon.getSizeInventory(); ++i) {
               if (!itemstack.isEmpty()) {
                  ItemStack itemstack1 = cocoon.getStackInSlot(i);
                  if (itemstack1.isEmpty()) {
                     cocoon.setInventorySlotContents(i, itemstack);
                     cocoon.markDirty();
                     this.myrmex.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                     this.myrmex.isEnteringHive = false;
                     return;
                  }

                  if (ItemStack.areItemsEqual(itemstack1, itemstack)) {
                     int j = Math.min(cocoon.getInventoryStackLimit(), itemstack1.getMaxStackSize());
                     int k = Math.min(itemstack.getCount(), j - itemstack1.getCount());
                     if (k > 0) {
                        itemstack1.grow(k);
                        itemstack.shrink(k);
                        if (itemstack.isEmpty()) {
                           cocoon.markDirty();
                        }

                        this.myrmex.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                        this.myrmex.isEnteringHive = false;
                        return;
                     }
                  }
               }
            }
         } else {
            this.resetTask();
         }
      }

   }

   public void resetTask() {
      this.nextRoom = BlockPos.ORIGIN;
      this.nextCocoon = null;
      this.myrmex.getNavigator().setPath((Path)null, this.movementSpeed);
   }

   public BlockPos getNearbyCocoon(BlockPos roomCenter) {
      int RADIUS_XZ = 15;
      int RADIUS_Y = 7;
      List<BlockPos> closeCocoons = new ArrayList();

      for(BlockPos blockpos : BlockPos.getAllInBox(roomCenter.add(-RADIUS_XZ, -RADIUS_Y, -RADIUS_XZ), roomCenter.add(RADIUS_XZ, RADIUS_Y, RADIUS_XZ))) {
         if (this.myrmex.world.getBlockState(blockpos).getBlock() instanceof BlockMyrmexCocoon && this.myrmex.world.getTileEntity(blockpos) != null && this.myrmex.world.getTileEntity(blockpos) instanceof TileEntityMyrmexCocoon && !((TileEntityMyrmexCocoon)this.myrmex.world.getTileEntity(blockpos)).isFull(this.myrmex.getHeldItem(EnumHand.MAIN_HAND))) {
            closeCocoons.add(blockpos);
         }
      }

      if (closeCocoons.isEmpty()) {
         return null;
      } else {
         return (BlockPos)closeCocoons.get(this.myrmex.getRNG().nextInt(Math.max(closeCocoons.size() - 1, 1)));
      }
   }

   public boolean isUseableCocoon(BlockPos blockpos) {
      return this.myrmex.world.getBlockState(blockpos).getBlock() instanceof BlockMyrmexCocoon && this.myrmex.world.getTileEntity(blockpos) != null && this.myrmex.world.getTileEntity(blockpos) instanceof TileEntityMyrmexCocoon && !((TileEntityMyrmexCocoon)this.myrmex.world.getTileEntity(blockpos)).isFull(this.myrmex.getHeldItem(EnumHand.MAIN_HAND));
   }
}
