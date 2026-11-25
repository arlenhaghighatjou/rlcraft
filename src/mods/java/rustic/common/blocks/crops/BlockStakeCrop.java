package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.BlockBase;
import rustic.common.blocks.ModBlocks;

public class BlockStakeCrop extends BlockBase implements IGrowable, IPlantable {
   public static final Material STAKE_CROP = new Material(MapColor.FOLIAGE) {
      public boolean isSolid() {
         return false;
      }

      public boolean blocksLight() {
         return false;
      }

      public EnumPushReaction getMobilityFlag() {
         return EnumPushReaction.BLOCK;
      }
   };
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
   protected static final AxisAlignedBB CROP_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
   protected static final AxisAlignedBB STAKE_AABB = new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D);

   public BlockStakeCrop(String name) {
      super(STAKE_CROP, name, false);
      this.setSoundType(SoundType.PLANT);
      this.setHardness(0.0F);
      this.setTickRandomly(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
      GameRegistry.findRegistry(Block.class).register(this);
      Blocks.FIRE.setFireInfo(this, 30, 100);
   }

   public int getMaxAge() {
      return 3;
   }

   public int getMaxHeight() {
      return 3;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return CROP_AABB;
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return STAKE_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      super.breakBlock(world, pos, state);
      world.setBlockState(pos, ModBlocks.CROP_STAKE.getDefaultState(), 3);
      this.dropBlockAsItem(world, pos, state, 0);
   }

   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      List<ItemStack> stacks = new ArrayList();
      EaglercraftRandom rand = world instanceof World ? ((World)world).rand : new EaglercraftRandom();
      stacks.add(new ItemStack(this.getSeed(), rand.nextInt(2) + 1));
      if (((Integer)state.getValue(AGE)).intValue() >= this.getMaxAge()) {
         stacks.add(new ItemStack(this.getCrop()));
      }

      return stacks;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
      this.checkAndDropBlock(worldIn, pos, state);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      super.updateTick(worldIn, pos, state, rand);
      this.checkAndDropBlock(worldIn, pos, state);
      if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
         int i = ((Integer)state.getValue(AGE)).intValue();
         if (i < this.getMaxAge()) {
            float f = getGrowthChance(this, worldIn, pos);
            if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int)(25.0F / f) + 1) == 0)) {
               worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i + 1)), 2);
               ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
         } else if (worldIn.getBlockState(pos.up()).getBlock() == ModBlocks.CROP_STAKE && worldIn.getBlockState(pos.down(this.getMaxHeight() - 1)).getBlock() != this) {
            float f = getGrowthChance(this, worldIn, pos);
            if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int)(25.0F / f) + 1) == 0)) {
               worldIn.setBlockState(pos.up(), state.withProperty(AGE, Integer.valueOf(0)), 3);
               ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
         }
      }

   }

   protected static float getGrowthChance(Block block, World world, BlockPos pos) {
      float growth = 0.125F * (float)(world.getLight(pos) - 11);
      IBlockState soil = world.getBlockState(pos.add(0, -1, 0));
      if (soil.getBlock().isFertile(world, pos.add(0, -1, 0)) || soil.getBlock() == block) {
         growth *= 1.5F;
      }

      return 3.5F + growth;
   }

   protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canBlockStay(worldIn, pos, state)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockState(pos, ModBlocks.CROP_STAKE.getDefaultState(), 3);
      }

   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return ((Integer)state.getValue(AGE)).intValue() < this.getMaxAge();
   }

   public boolean canUseBonemeal(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      int i = ((Integer)state.getValue(AGE)).intValue() + this.getBonemealAgeIncrease(worldIn);
      int j = this.getMaxAge();
      if (i > j) {
         i = j;
      }

      worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i)), 2);
   }

   protected int getBonemealAgeIncrease(World worldIn) {
      return MathHelper.getInt(worldIn.rand, 2, 5);
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

   public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
      return EnumPlantType.Crop;
   }

   public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
      return this.getDefaultState();
   }

   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      IBlockState soil = worldIn.getBlockState(pos.down());
      return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && (soil.getBlock() == this || soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), EnumFacing.UP, this));
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(this.getSeed());
   }

   protected Item getSeed() {
      return null;
   }

   protected Item getCrop() {
      return null;
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (((Integer)state.getValue(AGE)).intValue() >= this.getMaxAge()) {
         world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(this.getMaxAge() - 1)), 2);
         Block.spawnAsEntity(world, pos.offset(side), new ItemStack(this.getCrop()));
         return true;
      } else {
         return false;
      }
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return side != EnumFacing.UP && side != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
   }
}
