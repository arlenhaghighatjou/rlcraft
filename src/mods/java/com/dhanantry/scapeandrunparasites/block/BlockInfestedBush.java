package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockInfestedBush extends BlockBush implements IMetaName {
   protected static final AxisAlignedBB TALL_GRASS_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);
   protected static final AxisAlignedBB REED_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
   public static final PropertyEnum<BlockInfestedBush.EnumType> VARIANT = PropertyEnum.create("variant", BlockInfestedBush.EnumType.class);

   public BlockInfestedBush(String name, float hardness) {
      super(Material.VINE);
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setSoundType(SoundType.PLANT);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockInfestedBush.EnumType.INFECTED));
      this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      this.setHardness(hardness);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      BlockInfestedBush.EnumType variant = (BlockInfestedBush.EnumType)state.getValue(VARIANT);
      return variant != BlockInfestedBush.EnumType.SPINE && variant != BlockInfestedBush.EnumType.VINE ? TALL_GRASS_AABB : REED_AABB;
   }

   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      if ((BlockInfestedBush.EnumType)state.getValue(VARIANT) == BlockInfestedBush.EnumType.SPINE) {
         return this.checkBushV(worldIn.getBlockState(pos.down()));
      } else {
         return (BlockInfestedBush.EnumType)state.getValue(VARIANT) == BlockInfestedBush.EnumType.VINE ? this.checkBushV(worldIn.getBlockState(pos.up())) : this.checkBush(worldIn.getBlockState(pos.down()));
      }
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return true;
   }

   protected boolean checkBush(IBlockState state) {
      if (!(state.getBlock() instanceof BlockBase)) {
         return false;
      } else {
         return state.isFullCube();
      }
   }

   protected boolean checkBushV(IBlockState state) {
      if (state.isFullCube() && state.getBlock() instanceof BlockBase) {
         return true;
      } else if (state.getBlock() == this && (BlockInfestedBush.EnumType)state.getValue(VARIANT) == BlockInfestedBush.EnumType.SPINE) {
         return true;
      } else {
         return state.getBlock() == this && (BlockInfestedBush.EnumType)state.getValue(VARIANT) == BlockInfestedBush.EnumType.VINE;
      }
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
      if (!this.canBlockStay(worldIn, fromPos, state)) {
         worldIn.setBlockToAir(pos);
      }

   }

   protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canBlockStay(worldIn, pos, state)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
      }

   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
   }

   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      super.onEntityWalk(worldIn, pos, entityIn);
   }

   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return null;
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      super.harvestBlock(worldIn, player, pos, state, te, stack);
   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return false;
   }

   public boolean canUseBonemeal(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      return false;
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(BlockInfestedBush.EnumType variant : BlockInfestedBush.EnumType.values()) {
         items.add(new ItemStack(this, 1, variant.ordinal()));
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockInfestedBush.EnumType.values()[meta]);
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockInfestedBush.EnumType)state.getValue(VARIANT)).ordinal();
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT});
   }

   public Enum[] getVariants() {
      return BlockInfestedBush.EnumType.values();
   }

   public ItemBlock getItemBlock() {
      return new ItemBlockVariant(this);
   }

   public static enum EnumType implements IStringSerializable {
      INFECTED,
      GRASS1,
      FLOWER1,
      SPINE,
      VINE,
      ARC;

      private EnumType() {
      }

      public String getName() {
         return this.name().toLowerCase();
      }

      public String toString() {
         return this.getName();
      }
   }
}
