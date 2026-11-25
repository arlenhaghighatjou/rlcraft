package com.ferreusveritas.dynamictrees.api.network;

import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeCollector;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MapSignal {
   protected ArrayList<INodeInspector> nodeInspectors;
   public BlockPos root;
   public int depth;
   public boolean multiroot;
   public boolean destroyLoopedNodes;
   public boolean trackVisited;
   public EnumFacing localRootDir;
   public boolean overflow;
   public boolean found;

   public MapSignal() {
      this.root = null;
      this.multiroot = false;
      this.destroyLoopedNodes = true;
      this.trackVisited = false;
      this.localRootDir = null;
      this.nodeInspectors = new ArrayList();
   }

   public MapSignal(INodeInspector... nis) {
      this();
      Collections.addAll(this.nodeInspectors, nis);
   }

   public boolean run(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      for(INodeInspector inspector : this.nodeInspectors) {
         inspector.run(blockState, world, pos, fromDir);
      }

      return false;
   }

   public boolean returnRun(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      for(INodeInspector inspector : this.nodeInspectors) {
         inspector.returnRun(blockState, world, pos, fromDir);
      }

      return false;
   }

   public ArrayList<INodeInspector> getInspectors() {
      return this.nodeInspectors;
   }

   public boolean doTrackingVisited(BlockPos pos) {
      if (this.nodeInspectors.size() > 0) {
         INodeInspector inspector = (INodeInspector)this.nodeInspectors.get(0);
         if (inspector instanceof NodeCollector) {
            return ((NodeCollector)inspector).contains(pos);
         }
      }

      return false;
   }
}
