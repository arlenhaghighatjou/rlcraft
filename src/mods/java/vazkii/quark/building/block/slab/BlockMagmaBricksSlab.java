package vazkii.quark.building.block.slab;

import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkSlab;

public class BlockMagmaBricksSlab extends BlockQuarkSlab {
   public BlockMagmaBricksSlab(boolean doubleSlab) {
      super("magma_bricks_slab", Material.ROCK, doubleSlab);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
      this.setLightLevel(0.2F);
   }

   public boolean isFireSource(@Nonnull World world, BlockPos pos, EnumFacing side) {
      return this.isSideSolid(world.getBlockState(pos), world, pos, side);
   }

   @SideOnly(Side.CLIENT)
   public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, @Nonnull BlockPos pos) {
      return 15728880;
   }
}
