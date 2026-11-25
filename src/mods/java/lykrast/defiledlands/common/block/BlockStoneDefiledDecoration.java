package lykrast.defiledlands.common.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStoneDefiledDecoration extends BlockVariantCorrupted {
   public static final PropertyEnum<BlockStoneDefiledDecoration.Variants> VARIANT = PropertyEnum.create("variant", BlockStoneDefiledDecoration.Variants.class);

   public BlockStoneDefiledDecoration(float hardness, float resistance, int harvestLevel) {
      super(Material.ROCK, SoundType.STONE, hardness, resistance, "pickaxe", harvestLevel, true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockStoneDefiledDecoration.Variants.BRICKS));
   }

   public BlockStoneDefiledDecoration.Variants[] getVariants() {
      return BlockStoneDefiledDecoration.Variants.values();
   }

   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      for(BlockStoneDefiledDecoration.Variants v : this.getVariants()) {
         list.add(new ItemStack(this, 1, this.getMetaFromState(this.blockState.getBaseState().withProperty(VARIANT, v))));
      }

   }

   public BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT});
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockStoneDefiledDecoration.Variants)state.getValue(VARIANT)).ordinal();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, this.getVariants()[meta]);
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      for(BlockStoneDefiledDecoration.Variants v : this.getVariants()) {
         ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), this.getMetaFromState(this.blockState.getBaseState().withProperty(VARIANT, v)), new ModelResourceLocation(this.getRegistryName() + "_" + v, "inventory"));
      }

   }

   public static enum Variants implements IStringSerializable {
      BRICKS,
      BRICKS_CRACKED,
      BRICKS_MOSSY,
      MOSSY;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase();
      }

      public String toString() {
         return this.getName();
      }
   }
}
