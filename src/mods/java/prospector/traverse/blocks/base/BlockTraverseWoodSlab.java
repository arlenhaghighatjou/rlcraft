package prospector.traverse.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class BlockTraverseWoodSlab extends BlockTraverseSlab {
   public BlockTraverseWoodSlab(String name) {
      super(name, Material.WOOD, SoundType.WOOD);
      this.setHarvestLevel("axe", 0);
      this.setHardness(2.0F);
      this.setResistance(15.0F);
   }

   public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return true;
   }

   public static class Double extends BlockTraverseWoodSlab {
      public Double(String name, Block half) {
         super(name);
         this.halfslab = half;
      }

      public boolean isDouble() {
         return true;
      }
   }

   public static class Half extends BlockTraverseWoodSlab {
      public Half(String name) {
         super(name);
      }

      public boolean isDouble() {
         return false;
      }
   }
}
