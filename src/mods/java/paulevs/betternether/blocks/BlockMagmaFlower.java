package paulevs.betternether.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMagma;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import paulevs.betternether.BetterNether;

public class BlockMagmaFlower extends Block {
   public static final PropertyInteger SIZE = PropertyInteger.create("size", 0, 3);
   private static final AxisAlignedBB COLLIDE_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.75D, 0.9375D);

   public BlockMagmaFlower() {
      super(Material.PLANTS, MapColor.ORANGE_STAINED_HARDENED_CLAY);
      this.setRegistryName("magma_flower");
      this.setUnlocalizedName("magma_flower");
      this.setHardness(0.5F);
      this.setSoundType(SoundType.PLANT);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SIZE, Integer.valueOf(0)));
      this.setCreativeTab(BetterNether.BN_TAB);
      this.setTickRandomly(true);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return COLLIDE_AABB;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).getBlock() instanceof BlockMagma;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!this.canPlaceBlockAt(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
      }

   }

   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
      if (!worldIn.isRemote) {
         super.updateTick(worldIn, pos, state, random);
         int size = ((Integer)state.getValue(SIZE)).intValue();
         if (!this.canPlaceBlockAt(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
         } else if (size < 3 && random.nextInt(16) == 0) {
            worldIn.setBlockState(pos, state.withProperty(SIZE, Integer.valueOf(size + 1)));
         }
      }

   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SIZE, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(SIZE)).intValue();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{SIZE});
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public EnumOffsetType getOffsetType() {
      return EnumOffsetType.XYZ;
   }

   private void spawnSeeds(World world, BlockPos pos, Random random) {
      if (!world.isRemote) {
         ItemStack drop = new ItemStack(Items.MAGMA_CREAM, 1 + random.nextInt(3));
         EntityItem itemEntity = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, drop);
         world.spawnEntity(itemEntity);
         drop = new ItemStack(this, 1 + random.nextInt(3));
         itemEntity = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, drop);
         world.spawnEntity(itemEntity);
      }

   }

   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      if (((Integer)state.getValue(SIZE)).intValue() == 2) {
         this.spawnSeeds(worldIn, pos, worldIn.rand);
      }

      worldIn.destroyBlock(pos, true);
   }
}
