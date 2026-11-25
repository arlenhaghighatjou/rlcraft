package paulevs.betternether.blocks;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import paulevs.betternether.BetterNether;

public class BlockNetherGrass extends BlockBush implements IShearable {
   protected static final AxisAlignedBB GRASS_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.75D, 0.75D);

   public BlockNetherGrass() {
      this.setRegistryName("nether_grass");
      this.setUnlocalizedName("nether_grass");
      this.setCreativeTab(BetterNether.BN_TAB);
      this.setSoundType(SoundType.PLANT);
   }

   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return GRASS_AABB;
   }

   protected boolean canSustainBush(IBlockState state) {
      return state.getBlock() instanceof BlockNetherrack || state.getBlock() instanceof BlockSoulSand;
   }

   public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
      return true;
   }

   public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
      return NonNullList.withSize(1, new ItemStack(this, 1));
   }

   public int quantityDropped(Random random) {
      return 0;
   }

   public EnumOffsetType getOffsetType() {
      return EnumOffsetType.XZ;
   }
}
