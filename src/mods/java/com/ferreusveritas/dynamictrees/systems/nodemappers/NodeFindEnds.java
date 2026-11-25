package com.ferreusveritas.dynamictrees.systems.nodemappers;

import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NodeFindEnds implements INodeInspector {
   private final List<BlockPos> endPoints;
   private BlockPos last;

   public NodeFindEnds() {
      this.endPoints = new ArrayList(32);
      this.last = BlockPos.ORIGIN;
   }

   public NodeFindEnds(List<BlockPos> ends) {
      this.endPoints = ends;
      this.last = BlockPos.ORIGIN;
   }

   public boolean run(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      return true;
   }

   public boolean returnRun(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      BlockPos dPos = pos.subtract(this.last);
      if (dPos.getX() * dPos.getX() + dPos.getY() * dPos.getY() + dPos.getZ() * dPos.getZ() != 1) {
         this.endPoints.add(pos);
      }

      this.last = pos;
      return false;
   }

   public List<BlockPos> getEnds() {
      if (this.endPoints.isEmpty()) {
         this.endPoints.add(this.last);
      }

      return this.endPoints;
   }
}
