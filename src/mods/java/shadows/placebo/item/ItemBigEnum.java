package shadows.placebo.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import shadows.placebo.interfaces.IPropertyEnum;
import shadows.placebo.registry.RegistryInformation;
import shadows.placebo.util.PlaceboUtil;

public class ItemBigEnum<E extends Enum<E> & IPropertyEnum> extends ItemBase {
   public final E[] values;

   public ItemBigEnum(String name, E[] values, RegistryInformation info) {
      super(name, info);
      this.setHasSubtypes(true);
      this.values = values;
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         for(E e : this.values) {
            items.add(new ItemStack(this, 1, e.ordinal()));
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void initModels(ModelRegistryEvent ev) {
      for(E e : this.values) {
         PlaceboUtil.sMRL("items", this, e.ordinal(), "item=" + ((IPropertyEnum)e).getName());
      }

   }

   public String getUnlocalizedName(ItemStack stack) {
      return "item." + this.getRegistryName().getResourceDomain() + "." + ((IPropertyEnum)this.values[stack.getMetadata() % this.values.length]).getName();
   }
}
