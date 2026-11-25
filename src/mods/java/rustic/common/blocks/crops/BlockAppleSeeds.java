package rustic.common.blocks.crops;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.BlockBase;
import rustic.common.blocks.ModBlocks;
import rustic.core.Rustic;

public class BlockAppleSeeds extends BlockBase implements IGrowable, IPlantable {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 1);
   private static final AxisAlignedBB[] CROPS_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D)};

   public BlockAppleSeeds() {
      super(Material.PLANTS, "apple_seeds");
      this.setSoundType(SoundType.PLANT);
      this.setHardness(0.0F);
      this.setTickRandomly(true);
      this.setCreativeTab(Rustic.farmingTab);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
   }

   public int getMaxAge() {
      return 1;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
      return EnumPlantType.Plains;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return CROPS_AABB[((Integer)state.getValue(AGE)).intValue()];
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      super.updateTick(worldIn, pos, state, rand);
      if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
         int i = ((Integer)state.getValue(AGE)).intValue();
         float f = getGrowthChance(this, worldIn, pos);
         if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int)(25.0F / f) + 1) == 0)) {
            if (i < this.getMaxAge()) {
               worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i + 1)), 2);
            } else {
               worldIn.setBlockState(pos, ModBlocks.APPLE_SAPLING.getDefaultState(), 3);
            }

            ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
         }
      }

   }

   protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos) {
      return 7.0F;
   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      if (((Integer)state.getValue(AGE)).intValue() < this.getMaxAge()) {
         worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(this.getMaxAge())), 2);
      } else {
         worldIn.setBlockState(pos, ModBlocks.APPLE_SAPLING.getDefaultState(), 3);
      }

   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      IBlockState soil = worldIn.getBlockState(pos.down());
      return super.canPlaceBlockAt(worldIn, pos) && soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), EnumFacing.UP, this);
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(AGE)).intValue();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AGE});
   }

   public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
      return this.getDefaultState();
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return BlockFaceShape.UNDEFINED;
   }
}
