package paulevs.betternether.blocks;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulevs.betternether.BetterNether;

public class BlockNetherReed extends Block {
   public static final PropertyEnum<BlockNetherReed.EnumShape> SHAPE = PropertyEnum.create("shape", BlockNetherReed.EnumShape.class);

   public BlockNetherReed() {
      super(Material.PLANTS, MapColor.CYAN);
      this.setUnlocalizedName("nether_reed");
      this.setSoundType(SoundType.PLANT);
      this.setRegistryName("nether_reed");
      this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockNetherReed.EnumShape.TOP));
      this.setTickRandomly(true);
      this.setCreativeTab(BetterNether.BN_TAB);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      IBlockState state = worldIn.getBlockState(pos.down());
      Block block = state.getBlock();
      if (block == this) {
         return true;
      } else if (!(block instanceof BlockNetherrack) && block != Blocks.SOUL_SAND) {
         return false;
      } else {
         BlockPos blockpos = pos.down();

         for(EnumFacing enumfacing : Plane.HORIZONTAL) {
            IBlockState iblockstate = worldIn.getBlockState(blockpos.offset(enumfacing));
            if (iblockstate.getMaterial() == Material.LAVA) {
               return true;
            }
         }

         return false;
      }
   }

   protected final boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (this.canBlockStay(worldIn, pos)) {
         return true;
      } else {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
         return false;
      }
   }

   public boolean canBlockStay(World worldIn, BlockPos pos) {
      return this.canPlaceBlockAt(worldIn, pos);
   }

   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{SHAPE});
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if ((worldIn.getBlockState(pos.down()).getBlock() == this || this.checkForDrop(worldIn, pos, state)) && worldIn.isAirBlock(pos.up())) {
         int i;
         for(i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {
            ;
         }

         if (i < 3 && ForgeHooks.onCropsGrowPre(worldIn, pos, state, true)) {
            if (rand.nextInt(16) == 0) {
               worldIn.setBlockState(pos.up(), this.getDefaultState());
            }

            ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
         }
      }

   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (this.canPlaceBlockAt(worldIn, pos)) {
         worldIn.setBlockState(pos, state, 2);
      }

   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      this.checkForDrop(worldIn, pos, state);
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.up()).getBlock() == this ? state.withProperty(SHAPE, BlockNetherReed.EnumShape.BOTTOM) : state.withProperty(SHAPE, BlockNetherReed.EnumShape.TOP);
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public static enum EnumShape implements IStringSerializable {
      TOP(0, "top"),
      BOTTOM(1, "bottom");

      private final String name;
      private final int index;

      private EnumShape(int index, String name) {
         this.name = name;
         this.index = index;
      }

      public String toString() {
         return this.name;
      }

      public String getName() {
         return this.name;
      }

      public int getIndex() {
         return this.index;
      }
   }
}
