package snownee.kiwi.item;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.kiwi.client.ModelUtil;

public class ItemModVariantsNew<T, E extends IVariant<T>> extends ItemMod {
   private final E[] values;

   public ItemModVariantsNew(String name, E[] values) {
      super(name);
      this.values = values;
      this.setHasSubtypes(values.length > 1);
   }

   @SideOnly(Side.CLIENT)
   public void mapModel() {
      ModelUtil.mapItemVariantsModelNew(this, this.getName() + "_", this.values, "");
   }

   public List<E> getVariants() {
      return Arrays.asList(this.values);
   }

   public ItemStack getItemStack(E variant) {
      return this.getItemStack(variant, 1);
   }

   public ItemStack getItemStack(E variant, int amount) {
      return new ItemStack(this, amount, variant.getMeta());
   }

   public String getUnlocalizedName(ItemStack stack) {
      return stack.getMetadata() < this.values.length ? super.getUnlocalizedName(stack) + "." + this.values[stack.getMetadata()].getName() : super.getUnlocalizedName(stack);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         this.getVariants().forEach((v) -> {
            items.add(this.getItemStack(v));
         });
      }

   }
}
