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

public class BlockRavagingDecoration extends BlockVariantCorrupted {
   public static final PropertyEnum<BlockRavagingDecoration.Variants> VARIANT = PropertyEnum.create("variant", BlockRavagingDecoration.Variants.class);

   public BlockRavagingDecoration(float hardness, float resistance, int harvestLevel) {
      super(Material.ROCK, SoundType.STONE, hardness, resistance, "pickaxe", harvestLevel, true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockRavagingDecoration.Variants.STONE));
      this.setLightLevel(0.4F);
   }

   public BlockRavagingDecoration.Variants[] getVariants() {
      return BlockRavagingDecoration.Variants.values();
   }

   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      for(BlockRavagingDecoration.Variants v : this.getVariants()) {
         list.add(new ItemStack(this, 1, this.getMetaFromState(this.blockState.getBaseState().withProperty(VARIANT, v))));
      }

   }

   public BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT});
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockRavagingDecoration.Variants)state.getValue(VARIANT)).ordinal();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, this.getVariants()[meta]);
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      for(BlockRavagingDecoration.Variants v : this.getVariants()) {
         ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), this.getMetaFromState(this.blockState.getBaseState().withProperty(VARIANT, v)), new ModelResourceLocation(this.getRegistryName() + "_" + v, "inventory"));
      }

   }

   public static enum Variants implements IStringSerializable {
      STONE,
      BRICKS;

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
