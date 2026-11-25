package crafttweaker.api.block;

import crafttweaker.util.ArrayUtil;
import java.util.ArrayList;
import java.util.List;

public class BlockPatternOr implements IBlockPattern {
   private final IBlockPattern[] elements;

   public BlockPatternOr(IBlockPattern[] elements) {
      this.elements = elements;
   }

   public BlockPatternOr(IBlockPattern a, IBlockPattern b) {
      this.elements = new IBlockPattern[]{a, b};
   }

   public List<IBlock> getBlocks() {
      List<IBlock> blocks = new ArrayList();

      for(IBlockPattern pattern : this.elements) {
         blocks.addAll(pattern.getBlocks());
      }

      return blocks;
   }

   public boolean matches(IBlock block) {
      for(IBlockPattern pattern : this.elements) {
         if (pattern.matches(block)) {
            return true;
         }
      }

      return false;
   }

   public String getDisplayName() {
      StringBuilder result = new StringBuilder();
      boolean first = true;

      for(IBlockPattern pattern : this.elements) {
         if (first) {
            first = false;
         } else {
            result.append(" | ");
         }

         result.append(pattern.getDisplayName());
      }

      return result.toString();
   }

   public IBlockPattern or(IBlockPattern pattern) {
      return new BlockPatternOr(ArrayUtil.append(this.elements, pattern));
   }
}
