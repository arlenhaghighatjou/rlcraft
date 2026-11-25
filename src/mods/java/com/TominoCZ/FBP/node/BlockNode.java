package com.TominoCZ.FBP.node;

import com.TominoCZ.FBP.particle.FBPParticleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockNode {
   public IBlockState state;
   public Block originalBlock;
   public int meta;
   public FBPParticleBlock particle;

   public BlockNode(IBlockState s, FBPParticleBlock p) {
      this.particle = p;
      this.state = s;
      this.originalBlock = s.getBlock();
      this.meta = this.originalBlock.getMetaFromState(s);
   }
}
