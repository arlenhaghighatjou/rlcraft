package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.quark.base.block.BlockQuarkPane;

public class BlockFramedGlassPane extends BlockQuarkPane {
   public BlockFramedGlassPane() {
      super("framed_glass_pane", Material.GLASS);
      this.setHardness(3.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.GLASS);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
      this.setHarvestLevel("pickaxe", 1);
   }

   public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
      return super.canPaneConnectTo(world, pos, dir) || world.getBlockState(pos.offset(dir)).getBlock() instanceof BlockFramedGlass;
   }
}
