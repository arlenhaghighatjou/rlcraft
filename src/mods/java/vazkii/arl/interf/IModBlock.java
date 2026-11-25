package vazkii.arl.interf;

import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModBlock extends IVariantHolder, IVariantEnumHolder, IStateMapperProvider {
   String getBareName();

   IProperty getVariantProp();

   IProperty[] getIgnoredProperties();

   EnumRarity getBlockRarity(ItemStack var1);

   default boolean shouldDisplayVariant(int variant) {
      return true;
   }

   default boolean useBlockstateForItem() {
      return true;
   }

   @SideOnly(Side.CLIENT)
   default IStateMapper getStateMapper() {
      return null;
   }
}
