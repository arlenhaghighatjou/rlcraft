package vazkii.quark.decoration.block;

import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.quark.base.block.BlockQuarkPane;

public class BlockPaperWall extends BlockQuarkPane {
   public BlockPaperWall(String name) {
      super(name, Material.CLOTH);
      this.setHardness(1.5F);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setHarvestLevel("axe", 0);
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 30;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 60;
   }

   public boolean canPlaceTorchOnTop(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
      return true;
   }
}
