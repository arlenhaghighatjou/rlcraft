package paulevs.betternether.blocks;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulevs.betternether.BetterNether;

public class BlockSmoker extends Block {
   public static final PropertyEnum<BlockSmoker.EnumShape> SHAPE = PropertyEnum.create("shape", BlockSmoker.EnumShape.class);
   private static AxisAlignedBB MIDDLE_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
   private static AxisAlignedBB TOP_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D);

   public BlockSmoker() {
      super(Material.WOOD, MapColor.BROWN);
      this.setCreativeTab(BetterNether.BN_TAB);
      this.setHardness(2.0F);
      this.setRegistryName("smoker");
      this.setUnlocalizedName("smoker");
      this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockSmoker.EnumShape.TOP));
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return (BlockSmoker.EnumShape)this.getActualState(state, source, pos).getValue(SHAPE) == BlockSmoker.EnumShape.TOP ? TOP_AABB : MIDDLE_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.up()).getBlock() == this ? (worldIn.getBlockState(pos.down()).getBlock() == this ? state.withProperty(SHAPE, BlockSmoker.EnumShape.MIDDLE) : state.withProperty(SHAPE, BlockSmoker.EnumShape.BOTTOM)) : state.withProperty(SHAPE, BlockSmoker.EnumShape.TOP);
   }

   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{SHAPE});
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      if (worldIn.isAirBlock(pos.up())) {
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   private void checkDrop(World world, BlockPos pos) {
      if (!world.getBlockState(pos.down()).getMaterial().isSolid() && world.getBlockState(pos.down()).getBlock() != this) {
         world.destroyBlock(pos, true);
      }

   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      this.checkDrop(worldIn, pos);
   }

   public static enum EnumShape implements IStringSerializable {
      TOP(0, "top"),
      MIDDLE(1, "middle"),
      BOTTOM(2, "bottom");

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
