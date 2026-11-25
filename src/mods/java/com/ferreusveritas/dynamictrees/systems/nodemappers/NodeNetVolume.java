package com.ferreusveritas.dynamictrees.systems.nodemappers;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NodeNetVolume implements INodeInspector {
   public static final int VOXELSPERLOG = 4096;
   private int volume;

   public NodeNetVolume() {
   }

   public boolean run(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      if (TreeHelper.isBranch(blockState)) {
         int radius = TreeHelper.getTreePart(blockState).getRadius(blockState);
         this.volume += radius * radius * 64;
      }

      return true;
   }

   public boolean returnRun(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      return false;
   }

   public float getVolume() {
      return (float)this.volume / 4096.0F;
   }
}
