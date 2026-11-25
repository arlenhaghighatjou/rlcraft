package com.ferreusveritas.dynamictrees.systems.nodemappers;

import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NodeCoder implements INodeInspector {
   private final ArrayList<NodeCoder.Link> links = new ArrayList();

   public NodeCoder() {
   }

   public boolean run(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      NodeCoder.Link link = new NodeCoder.Link(pos);

      for(int i = this.links.size() - 1; i >= 0; --i) {
         NodeCoder.Link l = (NodeCoder.Link)this.links.get(i);
         if (pos.getX() + fromDir.getFrontOffsetX() == l.pos.getX() && pos.getY() + fromDir.getFrontOffsetY() == l.pos.getY() && pos.getZ() + fromDir.getFrontOffsetZ() == l.pos.getZ()) {
            l.links[fromDir.getOpposite().ordinal()] = link;
            link.links[fromDir.ordinal()] = l;
            l.forks += i != this.links.size() - 1 ? 1 : 0;
            break;
         }
      }

      this.links.add(link);
      return false;
   }

   public boolean returnRun(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      return false;
   }

   public byte[] compile(JoCode joCode) {
      JoCode.CodeCompiler cc = new JoCode.CodeCompiler();
      if (this.links.size() > 0) {
         this.nextLink(cc, (NodeCoder.Link)this.links.get(0), (NodeCoder.Link)null);
      }

      return cc.compile();
   }

   private void nextLink(JoCode.CodeCompiler cc, NodeCoder.Link link, NodeCoder.Link fromLink) {
      for(int dir = 0; dir < 6; ++dir) {
         NodeCoder.Link l = link.links[dir];
         if (l != null && l != fromLink) {
            if (link.forks > 0) {
               cc.addFork();
            }

            cc.addDirection((byte)dir);
            this.nextLink(cc, l, link);
            if (link.forks > 0) {
               cc.addReturn();
               --link.forks;
            }
         }
      }

   }

   private class Link {
      BlockPos pos;
      int forks;
      NodeCoder.Link[] links;

      public Link(BlockPos pos) {
         this.pos = pos;
         this.links = new NodeCoder.Link[6];
      }
   }
}
