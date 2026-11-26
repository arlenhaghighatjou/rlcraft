package sereneseasons.api;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;

public interface ISSBlock {
   ItemBlock createItemBlock();

   IProperty[] getPresetProperties();

   IProperty[] getNonRenderingProperties();

   String getStateName(IBlockState var1);
}
