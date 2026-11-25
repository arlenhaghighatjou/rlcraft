package vazkii.quark.building.block.slab;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import vazkii.arl.block.BlockModSlab;
import vazkii.quark.base.block.BlockQuarkSlab;

public class BlockVanillaSlab extends BlockQuarkSlab {
   private final IBlockState parent;

   public BlockVanillaSlab(String name, IBlockState state, boolean doubleSlab) {
      super(name, state.getMaterial(), doubleSlab);
      this.parent = state;
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
      return this.parent.getBlock().getExplosionResistance(world, pos, exploder, explosion) * 5.0F / 3.0F;
   }

   public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
      return this.parent.getBlockHardness(worldIn, pos);
   }

   @Nonnull
   public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
      return this.parent.getBlock().getSoundType(this.parent, world, pos, entity);
   }

   public static BlockModSlab initSlab(Block base, int meta, IBlockState state, String name) {
      BlockModSlab slab = new BlockVanillaSlab(name, state, false);
      BlockModSlab.initSlab(base, meta, slab, new BlockVanillaSlab(name, state, true));
      return slab;
   }
}
