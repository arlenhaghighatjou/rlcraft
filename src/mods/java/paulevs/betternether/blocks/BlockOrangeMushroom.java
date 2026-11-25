package paulevs.betternether.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
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

public class BlockOrangeMushroom extends Block implements IGrowable {
   public static final PropertyInteger SIZE = PropertyInteger.create("size", 0, 3);
   private static final AxisAlignedBB[] AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.5625D, 0.8125D), new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.75D, 0.9375D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

   public BlockOrangeMushroom() {
      super(Material.PLANTS, MapColor.ORANGE_STAINED_HARDENED_CLAY);
      this.setCreativeTab(BetterNether.BN_TAB);
      this.setHardness(0.5F);
      this.setSoundType(SoundType.WOOD);
      this.setRegistryName("orange_mushroom");
      this.setUnlocalizedName("orange_mushroom");
      this.setTickRandomly(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SIZE, Integer.valueOf(0)));
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB[((Integer)state.getValue(SIZE)).intValue()];
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

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return this.canStay(worldIn, pos) && ((Integer)state.getValue(SIZE)).intValue() < 3;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return ((Integer)state.getValue(SIZE)).intValue() < 3 && rand.nextInt(4) == 0;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      int s = ((Integer)state.getValue(SIZE)).intValue() + 1;
      if (s < 4) {
         worldIn.setBlockState(pos, state.withProperty(SIZE, Integer.valueOf(((Integer)state.getValue(SIZE)).intValue() + 1)));
      }

   }

   private boolean canStay(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).getBlock() instanceof BlockNetherMycelium;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!this.canStay(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!this.canStay(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      } else if (rand.nextInt(8) == 0) {
         int s = ((Integer)state.getValue(SIZE)).intValue() + 1;
         if (s < 4) {
            worldIn.setBlockState(pos, state.withProperty(SIZE, Integer.valueOf(((Integer)state.getValue(SIZE)).intValue() + 1)));
         }
      }

   }

   private void spawnSeeds(World world, BlockPos pos, Random random) {
      if (!world.isRemote) {
         ItemStack drop = new ItemStack(this, 1 + random.nextInt(3));
         EntityItem itemEntity = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, drop);
         world.spawnEntity(itemEntity);
         drop = new ItemStack(Items.DYE, 1 + random.nextInt(2), 5);
         itemEntity = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, drop);
         world.spawnEntity(itemEntity);
         drop = new ItemStack(Items.DYE, 1 + random.nextInt(2), 14);
         itemEntity = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, drop);
         world.spawnEntity(itemEntity);
      }

   }

   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      if (((Integer)state.getValue(SIZE)).intValue() > 2) {
         this.spawnSeeds(worldIn, pos, worldIn.rand);
      }

      worldIn.destroyBlock(pos, true);
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return this.canStay(worldIn, pos);
   }
}
