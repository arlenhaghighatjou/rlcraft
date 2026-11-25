package ivorius.reccomplex.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTyped extends Block {
   public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 15);

   public BlockTyped(Material materialIn) {
      super(materialIn);
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, Integer.valueOf(0)));
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(TYPE)).intValue();
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      for(int j = 0; j < 16; ++j) {
         list.add(new ItemStack(this, 1, j));
      }

   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(TYPE)).intValue();
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{TYPE});
   }
}
