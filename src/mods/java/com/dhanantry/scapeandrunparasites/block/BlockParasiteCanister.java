package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockParasiteCanister extends BlockBase implements IMetaName {
   public static final PropertyEnum<BlockParasiteCanister.EnumType> VARIANT = PropertyEnum.create("variant", BlockParasiteCanister.EnumType.class);
   protected static final AxisAlignedBB TALL_GRASS_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.55D, 0.9D);

   public BlockParasiteCanister(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
      super(material, name, hardness, creative, tickRandom);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockParasiteCanister.EnumType.SAC));
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      switch((BlockParasiteCanister.EnumType)state.getValue(VARIANT)) {
      case CYST:
         return TALL_GRASS_AABB;
      default:
         return super.getBoundingBox(state, source, pos);
      }
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (worldIn.isAreaLoaded(pos, 3)) {
         if (!worldIn.isRemote && (BlockParasiteCanister.EnumType)state.getValue(VARIANT) == BlockParasiteCanister.EnumType.CYST && worldIn.getBlockState(pos.down()).getBlock() == Blocks.AIR) {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
         }

      }
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      switch((BlockParasiteCanister.EnumType)state.getValue(VARIANT)) {
      case CYST:
         return super.getItemDropped(state, rand, fortune);
      case BAG:
         return super.getItemDropped(state, rand, fortune);
      case LUMP:
         return super.getItemDropped(state, rand, fortune);
      case SAC:
         return SRPItems.itemlurecomponent6;
      default:
         return super.getItemDropped(state, rand, fortune);
      }
   }

   public int quantityDropped(IBlockState state, int fortune, EaglercraftRandom random) {
      if ((BlockParasiteCanister.EnumType)state.getValue(VARIANT) == BlockParasiteCanister.EnumType.SAC) {
         return random.nextDouble() < 0.05D ? 1 : 0;
      } else {
         return super.quantityDropped(state, fortune, random);
      }
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(BlockParasiteCanister.EnumType variant : BlockParasiteCanister.EnumType.values()) {
         items.add(new ItemStack(this, 1, variant.ordinal()));
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockParasiteCanister.EnumType.values()[meta]);
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockParasiteCanister.EnumType)state.getValue(VARIANT)).ordinal();
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT});
   }

   public Enum[] getVariants() {
      return BlockParasiteCanister.EnumType.values();
   }

   public ItemBlock getItemBlock() {
      return new ItemBlockVariant(this);
   }

   public static enum EnumType implements IStringSerializable {
      SAC,
      CYST,
      LUMP,
      BAG;

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
