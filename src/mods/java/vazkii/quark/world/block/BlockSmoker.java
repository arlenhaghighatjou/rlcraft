package vazkii.quark.world.block;

import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.tile.TileSmoker;

public class BlockSmoker extends BlockModContainer implements IQuarkBlock {
   public BlockSmoker() {
      super("smoker", Material.ROCK, new String[0]);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setHardness(0.4F);
      this.setSoundType(SoundType.STONE);
   }

   public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
      return new TileSmoker();
   }
}
