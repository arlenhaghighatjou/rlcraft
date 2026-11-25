package svenhjol.charm.automation.block;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import svenhjol.charm.automation.feature.RedstoneSand;
import svenhjol.meson.iface.IMesonBlock;

public class BlockRedstoneSand extends BlockFalling implements IMesonBlock {
   protected String name = "redstone_sand";

   public BlockRedstoneSand() {
      this.register(this.name);
      this.setSoundType(SoundType.SAND);
      this.setHardness(RedstoneSand.hardness);
      this.setCreativeTab(CreativeTabs.REDSTONE);
      this.setHarvestLevel("shovel", 0);
   }

   public String getModId() {
      return "charm";
   }

   public String getName() {
      return this.name;
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return 15;
   }
}
