package rtg.api.world.deco.collection;

import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import rtg.api.config.BiomeConfig;
import rtg.api.world.deco.DecoBase;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;

/** @deprecated */
@Deprecated
public class DecoCollectionBase {
   public ArrayList<DecoBase> decos;
   public ArrayList<TreeRTG> rtgTrees;
   protected BiomeConfig config;

   public DecoCollectionBase(BiomeConfig config) {
      this.config = config;
      this.decos = new ArrayList();
      this.rtgTrees = new ArrayList();
   }

   public DecoCollectionBase addDeco(DecoBase deco) {
      if (!deco.properlyDefined()) {
         throw new RuntimeException();
      } else {
         this.decos.add(deco);
         return this;
      }
   }

   public DecoCollectionBase addDeco(DecoBase deco, boolean allowed) {
      if (allowed) {
         if (!deco.properlyDefined()) {
            throw new RuntimeException();
         }

         this.decos.add(deco);
      }

      return this;
   }

   public void addTree(TreeRTG tree, boolean allowed) {
      if (allowed) {
         this.rtgTrees.add(tree);
      }

   }

   public void addTree(TreeRTG tree) {
      this.addTree(tree, true);
   }

   public ArrayList<IBlockState> treeLogs() {
      ArrayList<IBlockState> logBlocks = new ArrayList();

      for(int i = 0; i < this.rtgTrees.size(); ++i) {
         logBlocks.add(((TreeRTG)this.rtgTrees.get(i)).getLogBlock());
      }

      return logBlocks;
   }

   public ArrayList<IBlockState> treeLeaves() {
      ArrayList<IBlockState> leafBlocks = new ArrayList();

      for(int i = 0; i < this.rtgTrees.size(); ++i) {
         leafBlocks.add(((TreeRTG)this.rtgTrees.get(i)).getLeavesBlock());
      }

      return leafBlocks;
   }
}
