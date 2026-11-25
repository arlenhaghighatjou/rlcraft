package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockParasiteRubbleDense extends BlockBase implements IMetaName {
   public static final PropertyEnum<BlockParasiteRubbleDense.EnumType> VARIANT = PropertyEnum.create("variant", BlockParasiteRubbleDense.EnumType.class);

   public BlockParasiteRubbleDense(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
      super(material, name, hardness, creative, tickRandom);
      this.setHarvestLevel("pickaxe", 3);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockParasiteRubbleDense.EnumType.WALL));
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(BlockParasiteRubbleDense.EnumType variant : BlockParasiteRubbleDense.EnumType.values()) {
         items.add(new ItemStack(this, 1, variant.ordinal()));
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockParasiteRubbleDense.EnumType.values()[meta]);
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockParasiteRubbleDense.EnumType)state.getValue(VARIANT)).ordinal();
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT});
   }

   public Enum[] getVariants() {
      return BlockParasiteRubbleDense.EnumType.values();
   }

   public ItemBlock getItemBlock() {
      return new ItemBlockVariant(this);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
   }

   public static enum EnumType implements IStringSerializable {
      WALL,
      BIOME,
      COLONY;

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
