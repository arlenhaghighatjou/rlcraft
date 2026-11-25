package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockSlabBase extends BlockSlab {
   public static final PropertyEnum<BlockSlabBase.EnumType> VARIANT = PropertyEnum.create("variant", BlockSlabBase.EnumType.class);

   public BlockSlabBase(Material materialIn, String name, float hardness, boolean creative, boolean tickRandom, BlockSlab half) {
      super(materialIn);
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setHardness(hardness);
      if (creative) {
         this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      }

      this.useNeighborBrightness = !this.isDouble();
      IBlockState state = this.blockState.getBaseState().withProperty(VARIANT, BlockSlabBase.EnumType.DEFAULT);
      if (!this.isDouble()) {
         state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
      }

   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(this);
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(this, 1, ((BlockSlabBase.EnumType)state.getValue(VARIANT)).ordinal());
   }

   public String getUnlocalizedName(int meta) {
      return super.getUnlocalizedName();
   }

   public IProperty<?> getVariantProperty() {
      return VARIANT;
   }

   public Comparable<?> getTypeForItem(ItemStack stack) {
      return BlockSlabBase.EnumType.DEFAULT;
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(BlockSlabBase.EnumType variant : BlockSlabBase.EnumType.values()) {
         items.add(new ItemStack(this, 1, variant.ordinal()));
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState state = this.blockState.getBaseState().withProperty(VARIANT, BlockSlabBase.EnumType.DEFAULT);
      if (!this.isDouble()) {
         state = state.withProperty(HALF, (meta & 8) != 0 ? EnumBlockHalf.TOP : EnumBlockHalf.BOTTOM);
      }

      return state;
   }

   public int getMetaFromState(IBlockState state) {
      int meta = 0;
      if (!this.isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP) {
         meta |= 8;
      }

      return meta;
   }

   protected BlockStateContainer createBlockState() {
      return this.isDouble() ? new BlockStateContainer(this, new IProperty[]{VARIANT}) : new BlockStateContainer(this, new IProperty[]{HALF, VARIANT});
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public static enum EnumType implements IStringSerializable {
      DEFAULT;

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
