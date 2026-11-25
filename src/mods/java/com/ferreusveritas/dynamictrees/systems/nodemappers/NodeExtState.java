package com.ferreusveritas.dynamictrees.systems.nodemappers;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

public class NodeExtState implements INodeInspector {
   private final Map<BlockPos, IExtendedBlockState> map = new HashMap();
   private final BlockPos origin;

   public NodeExtState(BlockPos origin) {
      this.origin = origin;
   }

   public Map<BlockPos, IExtendedBlockState> getExtStateMap() {
      return this.map;
   }

   public boolean run(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      BlockBranch branch = TreeHelper.getBranch(blockState);
      if (branch != null) {
         this.map.put(pos.subtract(this.origin), (IExtendedBlockState)blockState.getBlock().getExtendedState(blockState, world, pos));
      }

      return true;
   }

   public boolean returnRun(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      return false;
   }
}
