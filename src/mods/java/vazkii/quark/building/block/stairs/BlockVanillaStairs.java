package vazkii.quark.building.block.stairs;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockQuarkStairs;

public class BlockVanillaStairs extends BlockQuarkStairs {
   public BlockVanillaStairs(String name, IBlockState state) {
      super(name, state);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }
}
