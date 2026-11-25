package vazkii.quark.building.block;

import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockHardenedClayTiles extends BlockMod implements IQuarkBlock {
   public BlockHardenedClayTiles() {
      super("hardened_clay_tiles", Material.ROCK, new String[0]);
      this.setHardness(1.25F);
      this.setResistance(7.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   @Nonnull
   public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
      return MapColor.ADOBE;
   }
}
