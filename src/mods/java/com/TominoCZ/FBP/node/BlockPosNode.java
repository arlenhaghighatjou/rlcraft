package com.TominoCZ.FBP.node;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.util.math.BlockPos;

public class BlockPosNode {
   ConcurrentSet<BlockPos> possible = new ConcurrentSet();
   public boolean checked = false;

   public BlockPosNode() {
   }

   public void add(BlockPos pos) {
      this.possible.add(pos);
   }

   public boolean hasPos(BlockPos p1) {
      return this.possible.contains(p1);
   }
}
