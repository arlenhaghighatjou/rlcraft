package vazkii.quark.tweaks.ai;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIDoorInteract;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EntityAIOpenDoubleDoor extends EntityAIDoorInteract {
   private final boolean closeDoor;
   private int closingDoorTime;

   public EntityAIOpenDoubleDoor(EntityLiving living, boolean shouldClose) {
      super(living);
      this.entity = living;
      this.closeDoor = shouldClose;
   }

   public boolean shouldContinueExecuting() {
      return this.closeDoor && this.closingDoorTime > 0 && super.shouldContinueExecuting();
   }

   public void startExecuting() {
      this.closingDoorTime = 20;
      IBlockState state = this.entity.world.getBlockState(this.doorPosition).getActualState(this.entity.world, this.doorPosition);
      if (state.getBlock() == this.doorBlock) {
         EnumFacing direction = (EnumFacing)state.getValue(BlockDoor.FACING);
         boolean isOpen = ((Boolean)state.getValue(BlockDoor.OPEN)).booleanValue();
         EnumHingePosition isMirrored = (EnumHingePosition)state.getValue(BlockDoor.HINGE);
         BlockPos mirrorPos = this.doorPosition.offset(isMirrored == EnumHingePosition.RIGHT ? direction.rotateYCCW() : direction.rotateY());
         BlockPos doorPos = state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER ? mirrorPos : mirrorPos.down();
         IBlockState other = this.entity.world.getBlockState(doorPos).getActualState(this.entity.world, doorPos);
         if (state.getMaterial() != Material.IRON && other.getBlock() == this.doorBlock && other.getValue(BlockDoor.FACING) == direction && ((Boolean)other.getValue(BlockDoor.OPEN)).booleanValue() == isOpen && other.getValue(BlockDoor.HINGE) != isMirrored) {
            IBlockState newState = other.cycleProperty(BlockDoor.OPEN);
            this.entity.world.setBlockState(doorPos, newState, 10);
         }

         this.doorBlock.toggleDoor(this.entity.world, this.doorPosition, true);
      }
   }

   public void resetTask() {
      if (this.closeDoor) {
         IBlockState state = this.entity.world.getBlockState(this.doorPosition).getActualState(this.entity.world, this.doorPosition);
         if (!state.getPropertyKeys().contains(BlockDoor.OPEN)) {
            return;
         }

         EnumFacing direction = (EnumFacing)state.getValue(BlockDoor.FACING);
         boolean isOpen = ((Boolean)state.getValue(BlockDoor.OPEN)).booleanValue();
         EnumHingePosition isMirrored = (EnumHingePosition)state.getValue(BlockDoor.HINGE);
         BlockPos mirrorPos = this.doorPosition.offset(isMirrored == EnumHingePosition.RIGHT ? direction.rotateYCCW() : direction.rotateY());
         BlockPos doorPos = state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER ? mirrorPos : mirrorPos.down();
         IBlockState other = this.entity.world.getBlockState(doorPos).getActualState(this.entity.world, doorPos);
         if (state.getMaterial() != Material.IRON && other.getBlock() == this.doorBlock && other.getValue(BlockDoor.FACING) == direction && ((Boolean)other.getValue(BlockDoor.OPEN)).booleanValue() == isOpen && other.getValue(BlockDoor.HINGE) != isMirrored) {
            IBlockState newState = other.cycleProperty(BlockDoor.OPEN);
            this.entity.world.setBlockState(doorPos, newState, 10);
         }

         this.doorBlock.toggleDoor(this.entity.world, this.doorPosition, false);
      }

   }

   public void updateTask() {
      --this.closingDoorTime;
      super.updateTask();
   }
}
