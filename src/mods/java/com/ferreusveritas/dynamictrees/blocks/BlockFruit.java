package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.seasons.SeasonHelper;
import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class BlockFruit extends Block implements IGrowable {
   protected static final AxisAlignedBB[] FRUIT_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.4375D, 1.0D, 0.4375D, 0.5625D, 0.9375D, 0.5625D), new AxisAlignedBB(0.4375D, 1.0D, 0.4375D, 0.5625D, 0.875D, 0.5625D), new AxisAlignedBB(0.375D, 1.0D, 0.375D, 0.625D, 0.75D, 0.625D), new AxisAlignedBB(0.375D, 0.9375D, 0.375D, 0.625D, 0.6875D, 0.625D)};
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
   public static final String name = "fruit";
   private static final Map<Species, BlockFruit> speciesFruitMap = new HashMap();
   protected ItemStack droppedFruit;
   protected boolean bonemealable;
   protected Species species;

   public static BlockFruit getFruitBlockForSpecies(Species species) {
      return (BlockFruit)speciesFruitMap.getOrDefault(species, (Object)null);
   }

   public BlockFruit() {
      this("fruit");
   }

   public BlockFruit(String name) {
      super(Material.PLANTS);
      this.droppedFruit = ItemStack.EMPTY;
      this.bonemealable = false;
      this.species = Species.NULLSPECIES;
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
      this.setRegistryName(name);
      this.setUnlocalizedName(name);
      this.setTickRandomly(true);
      this.setHardness(0.3F);
   }

   public BlockFruit setBonemealable(boolean bonemealable) {
      this.bonemealable = bonemealable;
      return this;
   }

   public void setSpecies(Species species) {
      speciesFruitMap.put(species, this);
      this.species = species;
   }

   public Species getSpecies() {
      return this.species;
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
      if (!this.canBlockStay(world, pos, state)) {
         this.dropBlock(world, pos, state);
      } else {
         int age = ((Integer)state.getValue(AGE)).intValue();
         Float season = SeasonHelper.getSeasonValue(world, pos);
         if (season != null && this.getSpecies() != null) {
            if (this.getSpecies().seasonalFruitProductionFactor(world, pos) < 0.2F) {
               this.outOfSeasonAction(world, pos);
               return;
            }

            if (age == 0 && this.getSpecies().testFlowerSeasonHold(world, pos, season.floatValue())) {
               return;
            }
         }

         if (age < 3) {
            boolean doGrow = rand.nextFloat() < this.getGrowthChance(world, pos);
            boolean eventGrow = ForgeHooks.onCropsGrowPre(world, pos, state, doGrow);
            if (season != null) {
               if (!doGrow && !eventGrow) {
                  return;
               }
            } else if (!eventGrow) {
               return;
            }

            world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(age + 1)), 2);
            ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
         } else if (age == 3) {
            switch(this.matureAction(world, pos, state, rand)) {
            case Nothing:
            case Custom:
            default:
               break;
            case Drop:
               this.dropBlock(world, pos, state);
               break;
            case Rot:
               world.setBlockToAir(pos);
            }
         }

      }
   }

   protected float getGrowthChance(World world, BlockPos blockPos) {
      return 0.2F;
   }

   protected BlockFruit.MatureFruitAction matureAction(World world, BlockPos pos, IBlockState state, Random rand) {
      return BlockFruit.MatureFruitAction.Nothing;
   }

   protected void outOfSeasonAction(World world, BlockPos pos) {
      world.setBlockToAir(pos);
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!this.canBlockStay(worldIn, pos, state)) {
         this.dropBlock(worldIn, pos, state);
      }

   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (((Integer)state.getValue(AGE)).intValue() >= 3) {
         this.dropBlock(worldIn, pos, state);
         return true;
      } else {
         return false;
      }
   }

   private void dropBlock(World worldIn, BlockPos pos, IBlockState state) {
      worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
      this.dropBlockAsItem(worldIn, pos, state, 0);
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return this.getFruitDrop();
   }

   public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
      return world.getBlockState(pos.up()).getBlock() instanceof BlockLeaves;
   }

   public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
      return ((Integer)state.getValue(AGE)).intValue() < 3;
   }

   public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
      return this.bonemealable;
   }

   public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
      int age = ((Integer)state.getValue(AGE)).intValue();
      int newAge = MathHelper.clamp(age + 1, 0, 3);
      if (newAge != age) {
         world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(newAge)), 2);
      }

   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      super.getDrops(drops, world, pos, state, fortune);
      if (((Integer)state.getValue(AGE)).intValue() >= 3) {
         ItemStack toDrop = this.getFruitDrop();
         if (!toDrop.isEmpty()) {
            drops.add(toDrop);
         }
      }

   }

   public BlockFruit setDroppedItem(ItemStack stack) {
      this.droppedFruit = stack;
      return this;
   }

   public ItemStack getFruitDrop() {
      return this.droppedFruit.copy();
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return FRUIT_AABB[((Integer)state.getValue(AGE)).intValue()];
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta & 3));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(AGE)).intValue() & 3;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AGE});
   }

   public IBlockState getStateForAge(int age) {
      return this.getDefaultState().withProperty(AGE, Integer.valueOf(age));
   }

   public int getAgeForWorldGen(World world, BlockPos pos) {
      Float seasonValue = SeasonHelper.getSeasonValue(world, pos);
      return seasonValue != null ? this.getAgeForSeasonalWorldGen(world, pos, seasonValue.floatValue()) : 3;
   }

   public int getAgeForSeasonalWorldGen(World world, BlockPos pos, float seasonValue) {
      return this.getSpecies().testFlowerSeasonHold(world, pos, seasonValue) ? 0 : Math.min(world.rand.nextInt(6), 3);
   }

   static enum MatureFruitAction {
      Nothing,
      Drop,
      Rot,
      Custom;

      private MatureFruitAction() {
      }
   }
}
