package com.ferreusveritas.dynamictrees.systems.nodemappers;

import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NodeCollector implements INodeInspector {
   private final Set<BlockPos> nodeSet;

   public NodeCollector(Set nodeSet) {
      this.nodeSet = nodeSet;
   }

   public boolean run(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      this.nodeSet.add(pos);
      return false;
   }

   public boolean returnRun(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      return false;
   }

   public boolean contains(BlockPos pos) {
      return this.nodeSet.contains(pos);
   }
}
