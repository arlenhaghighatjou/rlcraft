package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.quark.base.block.BlockQuarkPillar;

public class BlockReed extends BlockQuarkPillar {
   public BlockReed() {
      super("reed_block", Material.WOOD);
      this.setHardness(0.5F);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 20;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 60;
   }
}
