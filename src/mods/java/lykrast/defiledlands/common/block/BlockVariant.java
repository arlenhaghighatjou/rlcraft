package lykrast.defiledlands.common.block;

import lykrast.defiledlands.common.item.ItemBlockMetadata;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockVariant extends BlockGeneric implements ICustomItemBlock, ICustomModel {
   protected boolean variantNames = false;

   public BlockVariant(Material material, SoundType soundType, float hardness, float resistance) {
      super(material, soundType, hardness, resistance);
   }

   public BlockVariant(Material material, SoundType soundType, float hardness, float resistance, boolean names) {
      super(material, soundType, hardness, resistance);
      this.variantNames = names;
   }

   public BlockVariant(Material material, SoundType soundType, float hardness, float resistance, String tool, int harvestLevel) {
      super(material, soundType, hardness, resistance, tool, harvestLevel);
   }

   public BlockVariant(Material material, SoundType soundType, float hardness, float resistance, String tool, int harvestLevel, boolean names) {
      super(material, soundType, hardness, resistance, tool, harvestLevel);
      this.variantNames = names;
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public boolean hasVariantNames() {
      return this.variantNames;
   }

   public ItemBlock getItemBlock() {
      return new ItemBlockMetadata(this, this.hasVariantNames());
   }

   public abstract Enum[] getVariants();

   public abstract void getSubBlocks(CreativeTabs var1, NonNullList<ItemStack> var2);

   public abstract BlockStateContainer createBlockState();

   public abstract int getMetaFromState(IBlockState var1);

   public abstract IBlockState getStateFromMeta(int var1);

   @SideOnly(Side.CLIENT)
   public abstract void initModel();
}
