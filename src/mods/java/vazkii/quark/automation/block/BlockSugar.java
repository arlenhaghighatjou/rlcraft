package vazkii.quark.automation.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.block.BlockModFalling;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockSugar extends BlockModFalling implements IQuarkBlock {
   public BlockSugar() {
      super("sugar_block", new String[0]);
      this.setHardness(0.5F);
      this.setSoundType(SoundType.SAND);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   protected boolean tryTouchWater(World worldIn, BlockPos pos) {
      boolean flag = false;

      for(EnumFacing enumfacing : EnumFacing.values()) {
         if (enumfacing != EnumFacing.DOWN) {
            BlockPos blockpos = pos.offset(enumfacing);
            if (worldIn.getBlockState(blockpos).getMaterial() == Material.WATER) {
               flag = true;
               break;
            }
         }
      }

      if (flag) {
         worldIn.playEvent(2001, pos, Block.getStateId(worldIn.getBlockState(pos)));
         worldIn.setBlockToAir(pos);
      }

      return flag;
   }

   public void neighborChanged(IBlockState state, World worldIn, @Nonnull BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!this.tryTouchWater(worldIn, pos)) {
         super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
      }

   }

   public void onBlockAdded(World worldIn, @Nonnull BlockPos pos, IBlockState state) {
      if (!this.tryTouchWater(worldIn, pos)) {
         super.onBlockAdded(worldIn, pos, state);
      }

   }
}
