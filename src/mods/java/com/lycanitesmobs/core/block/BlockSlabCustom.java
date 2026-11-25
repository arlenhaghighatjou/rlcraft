package com.lycanitesmobs.core.block;

import com.lycanitesmobs.client.localisation.LanguageManager;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSlabCustom extends BlockSlab {
   public static final PropertyEnum<BlockSlabCustom.Variant> VARIANT = PropertyEnum.create("variant", BlockSlabCustom.Variant.class);
   protected Block doubleBlock;

   public BlockSlabCustom(BlockBase block, Block doubleBlock) {
      super(block.getDefaultState().getMaterial());
      this.doubleBlock = doubleBlock;
      this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, EnumBlockHalf.BOTTOM).withProperty(VARIANT, BlockSlabCustom.Variant.DEFAULT));
      String slabName = "_slab";
      this.setRegistryName(new ResourceLocation(block.group.modid, block.blockName + slabName));
      this.setUnlocalizedName(block.blockName + slabName);
      block.copyAttributesTo(this);
   }

   public String getUnlocalizedName(int meta) {
      return super.getUnlocalizedName();
   }

   public boolean isDouble() {
      return false;
   }

   public Block getDoubleBlock() {
      return this.doubleBlock;
   }

   public String getLocalizedName() {
      return LanguageManager.translate(this.getUnlocalizedName() + ".name");
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.getDescription(stack, world));
   }

   public String getDescription(ItemStack itemStack, World world) {
      return "\u00a7a" + LanguageManager.translate(this.getUnlocalizedName() + ".description");
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{HALF, VARIANT});
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(HALF, meta == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
   }

   public int getMetaFromState(IBlockState state) {
      return state.getValue(HALF) == EnumBlockHalf.BOTTOM ? 0 : 1;
   }

   public IProperty<?> getVariantProperty() {
      return VARIANT;
   }

   public Comparable<?> getTypeForItem(ItemStack stack) {
      return BlockSlabCustom.Variant.DEFAULT;
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(this);
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(this);
   }

   public static enum Variant implements IStringSerializable {
      DEFAULT;

      private Variant() {
      }

      public String getName() {
         return "default";
      }
   }
}
