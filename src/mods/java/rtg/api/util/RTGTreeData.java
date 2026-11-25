package rtg.api.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RTGTreeData {
   public boolean firstLog = false;
   public boolean firstLeaves = false;
   public int minXLog = 0;
   public int maxXLog = 0;
   public int minZLog = 0;
   public int maxZLog = 0;
   public int minXLeaves = 0;
   public int maxXLeaves = 0;
   public int minZLeaves = 0;
   public int maxZLeaves = 0;
   public int logRangeX = 0;
   public int logRangeZ = 0;
   public int leavesRangeX = 0;
   public int leavesRangeZ = 0;

   public RTGTreeData() {
   }

   public void placeLogBlock(World world, BlockPos pos, IBlockState logBlock, int generateFlag) {
      if (this.firstLog) {
         this.minXLog = pos.getX() < this.minXLog ? pos.getX() : this.minXLog;
         this.maxXLog = pos.getX() > this.maxXLog ? pos.getX() : this.maxXLog;
         this.minZLog = pos.getZ() < this.minZLog ? pos.getZ() : this.minZLog;
         this.maxZLog = pos.getZ() > this.maxZLog ? pos.getZ() : this.maxZLog;
      } else {
         this.minXLog = pos.getX();
         this.maxXLog = pos.getX();
         this.minZLog = pos.getZ();
         this.maxZLog = pos.getZ();
         this.firstLog = true;
      }

      this.updateRanges();
   }

   public void placeLeavesBlock(World world, BlockPos pos, IBlockState logBlock, int generateFlag) {
      if (this.firstLeaves) {
         this.minXLeaves = pos.getX() < this.minXLeaves ? pos.getX() : this.minXLeaves;
         this.maxXLeaves = pos.getX() > this.maxXLeaves ? pos.getX() : this.maxXLeaves;
         this.minZLeaves = pos.getZ() < this.minZLeaves ? pos.getZ() : this.minZLeaves;
         this.maxZLeaves = pos.getZ() > this.maxZLeaves ? pos.getZ() : this.maxZLeaves;
      } else {
         this.minXLeaves = pos.getX();
         this.maxXLeaves = pos.getX();
         this.minZLeaves = pos.getZ();
         this.maxZLeaves = pos.getZ();
         this.firstLeaves = true;
      }

      this.updateRanges();
   }

   public void dumpTreeData() {
      if (this.logRangeX > 16) {
         Logger.error("Log X range = {} to {} ({} blocks)", this.minXLog, this.maxXLog, this.logRangeX);
      }

      if (this.logRangeZ > 16) {
         Logger.error("Log Z range = {} to {} ({} blocks)", this.minZLog, this.maxZLog, this.logRangeZ);
      }

      if (this.leavesRangeX > 16) {
         Logger.error("Leaves X range = {} to {} ({} blocks)", this.minXLeaves, this.maxXLeaves, this.leavesRangeX);
      }

      if (this.leavesRangeZ > 16) {
         Logger.error("Leaves Z range = {} to {} ({} blocks)", this.minZLeaves, this.maxZLeaves, this.leavesRangeZ);
      }

   }

   public void updateRanges() {
      this.logRangeX = Math.abs(this.maxXLog - this.minXLog);
      this.logRangeZ = Math.abs(this.maxZLog - this.minZLog);
      this.leavesRangeX = Math.abs(this.maxXLeaves - this.minXLeaves);
      this.leavesRangeZ = Math.abs(this.maxZLeaves - this.minZLeaves);
   }
}
